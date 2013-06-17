package cox;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;
import java.lang.Math;

import Jama.Matrix;
//061213
public class CoxClient2 {
	public CoxClient2(String taskName, String dataPath, String root_property, int maxIteration, double epsilon, int taskStatus ){
		super();
		//if task has finished, do nothing
		System.out.println("taskStatus is " + taskStatus);
		if(taskStatus == 2){
			
			return;
		}
		try {
		    // data file name
		    String file_name;
		    String outputString = "Task name is: " + taskName + ".\r\n";
		    outputString = outputString + "Using max iteration "+ maxIteration + ", epsilon " + epsilon + ".\r\n";
		    
		    // used for reading the data file
		    FileInputStream file_stream;
		    DataInputStream file_in;
		    BufferedReader file_br;
		    String file_line;
		    String[] line_tokens;
		    
		   // String taskName = "t1";

		    // data structures used to hold the client data read in from files
			Vector<Double> ZTDrow;
			Vector< Vector<Double> > ZTDv = new Vector< Vector<Double> >();
			double[][] ZTDa;
			double[] DIa;
			double[][][] ZZa;
			int[] index, nonDeltaDI;
			Matrix ZTD, Z, T, Delta, DI, sumZ, sumZ_line, ZZ_line, ZZ, ZZv0;
			Matrix beta0, beta1, ZB, thetaZ, theta, thetaZtmp, thetatmp, Gv, G;
			Matrix TZTZ, TZTZ_line, TZTZtmp, neghessian;
			Matrix Tuniq=null;
			Vector< Matrix > ZZv, ZZtmp, thetaZZ, TZTZv;
			int m=-1, full_n=0, n, i, j, iter=0, k, ki;

//------------------ //check of filename will be in a different way jwc 10.9 -----------------------------------
		    file_name = dataPath;
		    System.out.println("Using data file '" + file_name + "'.");
		    outputString = outputString + "Using data file '" + file_name + "'.\r\n";
//---------------------------------------------------------------------------------------------------------------------------------------

		    // access the file
		    file_stream = new FileInputStream(file_name);
		    file_in = new DataInputStream(file_stream);
		    file_br = new BufferedReader(new InputStreamReader(file_in));
		    file_line = file_br.readLine();// property line
			while ((file_line = file_br.readLine()) != null) {
				full_n = full_n + 1;
				line_tokens = file_line.split("\t");
				if (m == -1) {
					m = line_tokens.length - 2;
				}else if (m != line_tokens.length - 2) {
					System.out.println("ERROR: data file dimensions don't " +
									   "match on line " + full_n + ".");
					System.exit(-1);
				}
				ZTDrow = new Vector<Double>();	
				for (i = 0; i < line_tokens.length; i++) {
					ZTDrow.add(new Double(line_tokens[i]));					
				}
				ZTDv.add(ZTDrow);
		    }
			file_in.close();

			//sort original data ZTD
			ZTDa = two_dim_vec_to_arr(ZTDv);
			ArrayComparator comparator = new ArrayComparator(ZTDa);
			Arrays.sort(ZTDa, comparator);
			ZTD = new Matrix(ZTDa);
			Z = ZTD.getMatrix(0,full_n-1,0,m-1);
			T = ZTD.getMatrix(0,full_n-1,m,m);
			Delta = ZTD.getMatrix(0,full_n-1,m+1,m+1); 
			
			//change T to avoid di=1			
			int count=0;
			double T_now=T.get(0,0);
			double T_not1=T_now;
			for (i=0; i<full_n; i++) {
				if(T.get(i,0)-T_now==0){
					count+=1;
				}else{
					if(count==1){							//if alone
						T.set(i-1,0,T_not1);
						T_now=T.get(i,0);
						if (i==1){							//if first alone
							T.set(i-1,0,T_now);	
							count=2;
						}
					}else{
						T_not1=T.get(i-1,0);
						T_now=T.get(i,0);
						count=1;	
						if(i==full_n-1)						//if last alone
							T.set(i,0,T_not1);
					}
				}
			}
			
			URL url = new URL(root_property + "coxserverservlet");
			URLConnection servletConnection = url.openConnection();			
			servletConnection.setDoInput(true);
			servletConnection.setDoOutput(true);			
			servletConnection.setUseCaches(false);
			servletConnection.setDefaultUseCaches(false);				
			servletConnection.setRequestProperty ("Content-Type", "application/x-java-serialized-object");	
			
			//send T to server and get Tuniq back
			DataIn2 sendT = new DataIn2(T, null, null, null, null, taskName,"sur");
		    OutputStream out = servletConnection.getOutputStream();				
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(sendT);
			oos.flush();
			oos.close();
			InputStream in = servletConnection.getInputStream();
			ObjectInputStream ois;
			ois = new ObjectInputStream(in);
			DataOut2 result = (DataOut2) ois.readObject();
			ois.close();
			if(result.getType().equals("sur")){
				Tuniq = result.getMatrix();
			}
			else{
				System.out.println("sur type needed");
			}
			n = Tuniq.getRowDimension();
			System.out.println("Using "+n+" Survival time:");
			Tuniq.print(1,1);
			outputString = outputString + "\r\nUsing "+n+" Survival time:\r\n";
			for(i=0;i<n;i++){
				outputString = outputString + Tuniq.get(i,0) + "\r\n";
			}
			
			//calculate ZZ
			ZZa = new double[full_n][m][m];
			for (i=0; i<m; i++) {
				for (j=0; j<m; j++) {
					ZZ_line = Z.getMatrix(0,full_n-1,i,i).arrayTimes(Z.getMatrix(0,full_n-1,j,j)).copy();
					for(k=0;k<full_n;k++){
						ZZa[k][i][j]=ZZ_line.get(k,0);
					}
				}
			}
			ZZv = new Vector< Matrix >();
			for(k=0;k<full_n;k++){
				ZZ=new Matrix(ZZa[k]);
				ZZv.add(ZZ);
			}
			ZZv0 = new Matrix(m,m,0);
			
			//calculate DI, sumZ
			sumZ_line = new Matrix(1,m,0);
			sumZ = new Matrix(n,m,0);
			DIa = new double[n];
			nonDeltaDI = new int[n];
			k=0;
			for (i=0; i<full_n; i++) {
				if(T.get(i,0)==Tuniq.get(k, 0)){
					if (Delta.get(i,0)==1){
						DIa[k]+=1;
						sumZ_line.plusEquals(Z.getMatrix(i,i,0,m-1));
					}
					nonDeltaDI[k]+=1;
					if(i==full_n-1){
						sumZ.setMatrix(k,k,0,m-1,sumZ_line);
						sumZ_line = new Matrix(1,m,0);
					}
				}else {
					sumZ.setMatrix(k,k,0,m-1,sumZ_line);
					sumZ_line = new Matrix(1,m,0);
					k+=1;i-=1;
				}
			}
			DI = new Matrix(DIa,DIa.length);

			//calculate index
			index = new int[n];
			index[0]=0;
			for(i=1;i<n;i++){
				index[i]=index[i-1]+nonDeltaDI[i-1];
			}
			
		    beta0 = new Matrix(m, 1, -1.0);
		    beta1 = new Matrix(m, 1, 0.0);

	    	System.out.println("maximum Iteration:"+maxIteration);
		    // iteratively update beta1
		    while (max_abs((beta1.minus(beta0)).getArray()) > epsilon) {
//				 if (iter == 20)
//				     break;
		    	 if (iter == maxIteration)//added by lph
			     {
			    	 System.out.println("Has reached the maximum iteration number!");
			    	 break; 
			     }
		    	url = new URL(root_property + "coxserverservlet");
		    	servletConnection = url.openConnection();			
				servletConnection.setDoInput(true);
				servletConnection.setDoOutput(true);			
				servletConnection.setUseCaches(false);
				servletConnection.setDefaultUseCaches(false);				
				servletConnection.setRequestProperty ("Content-Type", "application/x-java-serialized-object");	
				
				System.out.println("value: " + max_abs((beta1
			            .minus(beta0)).getArray()));

				System.out.println("Iteration " + iter);

				beta0 = beta1.copy();
				
				//calculate theta
				ZB = Z.times(beta0);
				exp(ZB.getArray());
				thetatmp = new Matrix(full_n+1,1,0);
				theta = new Matrix(n,1,0);
				thetatmp.set(full_n-1,0,ZB.get(full_n-1,0));
				for(i=full_n-2;i>=0;i--){
					thetatmp.set(i,0,(ZB.get(i,0)+thetatmp.get(i+1,0)));
				}
				for(i=0;i<n;i++){
					theta.set(i,0,thetatmp.get(index[i],0)); 
				}
				//calculate thetaZ
				thetaZtmp = new Matrix(full_n+1,m,0);
				thetaZ= new Matrix(n,m,0);
				for(i=0;i<full_n;i++){
					thetaZtmp.setMatrix(i,i,0,m-1,Z.getMatrix(i,i,0,m-1).times(ZB.get(i,0)));
				}
				for(i=full_n-2;i>=0;i--){
					thetaZtmp.setMatrix(i,i,0,m-1,thetaZtmp.getMatrix(i+1,i+1,0,m-1).plus(thetaZtmp.getMatrix(i,i,0,m-1)));
				}
				for(i=0;i<n;i++){
					thetaZ.setMatrix(i,i,0,m-1,thetaZtmp.getMatrix(index[i],index[i],0,m-1));
				}
				
				//calculate thetaZZ
				ZZtmp = new Vector< Matrix >();
				for(i=0;i<full_n;i++){
					ZZtmp.add(ZZv.get(i).times(ZB.get(i,0)));
				}
				ZZtmp.add(ZZv0);
				for(i=full_n-2;i>=0;i--){
					ZZtmp.set(i,ZZtmp.get(i).plus(ZZtmp.get(i+1)));
				}
				thetaZZ = new Vector< Matrix >();
				for(i=0;i<n;i++){					
					thetaZZ.add(ZZtmp.get(index[i]));
				}
				System.out.println("send data to server at iter: " + iter);
				//send theta, thetaZ, thetaZZ, sumZ, DI to calculate gradient and hessian
				DataIn2 sendthetaZ= new DataIn2(theta, thetaZ, thetaZZ, sumZ, DI, taskName, "beta");
			    out = servletConnection.getOutputStream();				
				oos = new ObjectOutputStream(out);
				oos.writeObject(sendthetaZ);
				oos.flush();
				oos.close();
				System.out.println("receive beta from server at iter: " + iter);
				//receive beta
				in = servletConnection.getInputStream();
				ois = new ObjectInputStream(in);
				result = (DataOut2) ois.readObject();
				ois.close();

				if(result.getType().equals("beta")){
					beta1 = result.getMatrix();
				}
				else{
					System.out.println("beta type needed");
				}
				System.out.println("beta at iteration"+iter);
				beta1.print(7,7);
				iter+=1;
		    }

		    System.out.println("value on exit: " + max_abs((beta1
		            .minus(beta0)).getArray()));
		    System.out.println("Finished iteration.");
		    
		    outputString = outputString + "\r\nUsing iteration: " + iter + ".\r\n";
		    outputString = outputString + "\r\nBeta result:\r\n";
			for(i=0;i<m;i++){
				outputString = outputString + beta1.get(i,0) + "\r\n";
			}
			
			//calculate Base of survival function/ baseline hazard function
			ZB = Z.times(beta1);
			exp(ZB.getArray());
			thetatmp = new Matrix(full_n+1,1,0);
			theta = new Matrix(n,1,0);
			thetatmp.set(full_n-1,0,ZB.get(full_n-1,0));
			for(i=full_n-2;i>=0;i--){
				thetatmp.set(i,0,(ZB.get(i,0)+thetatmp.get(i+1,0)));
			}
			for(i=0;i<n;i++){
				theta.set(i,0,thetatmp.get(index[i],0)); 
			}
			
			System.out.println("start baseline hazard function transmission");			
			Matrix BaseSurFn = new Matrix(n,1,0);
			

		    url = new URL(root_property + "coxserverservlet");
			servletConnection = url.openConnection();			
			servletConnection.setDoInput(true);
			servletConnection.setDoOutput(true);			
			servletConnection.setUseCaches(false);
			servletConnection.setDefaultUseCaches(false);				
			servletConnection.setRequestProperty ("Content-Type", "application/x-java-serialized-object");	
			
			out = servletConnection.getOutputStream();				
			DataIn2 BHF = new DataIn2(theta, null, null, null, DI, taskName, "fn");
			oos = new ObjectOutputStream(out);
			oos.writeObject(BHF);
			oos.flush();
			oos.close();
			in = servletConnection.getInputStream();
			ois = new ObjectInputStream(in);
			result = (DataOut2) ois.readObject();
			ois.close();
			if(result.getType().equals("fn")){
				BaseSurFn = result.getMatrix();
			}
			else{
				System.out.println("fn type needed");
			}		
		    outputString = outputString + "\r\nBaseline hazard Function:\r\n";
			for(i=0;i<n;i++){
				outputString = outputString + BaseSurFn.get(i,0) + "\r\n";
			}
			//save result
			//String OutputString = "test";
			Frame f=new Frame();
			FileDialog fd = new FileDialog(f, "Save Train Result", FileDialog.SAVE);
			fd.setVisible(true);
			try
			{
				 File f1 = new File(fd.getDirectory(), fd.getFile());
				 BufferedWriter bufferout = new BufferedWriter(new FileWriter(f1));
//				 FileOutputStream out = new FileOutputStream(f1);
				 bufferout.write(outputString);
				 bufferout.close();
				 outputString = "";
			}
			catch (IOException e2)
			{
				e2.printStackTrace();
			}
			System.out.println("Baseline Hazard Function:");
			BaseSurFn.print(8,8);
			
			System.out.println("start end message transmission");
			

		    url = new URL(root_property + "coxserverservlet");
			servletConnection = url.openConnection();			
			servletConnection.setDoInput(true);
			servletConnection.setDoOutput(true);			
			servletConnection.setUseCaches(false);
			servletConnection.setDefaultUseCaches(false);				
			servletConnection.setRequestProperty ("Content-Type", "application/x-java-serialized-object");	
			
			out = servletConnection.getOutputStream();							
			DataIn2 endTask = new DataIn2(new Matrix(1,1,1), null, null, null, null, taskName, "end");
			oos = new ObjectOutputStream(out);
			oos.writeObject(endTask);
			oos.flush();
			oos.close();
			
//		    // read end from server
			in = servletConnection.getInputStream();
			ois = new ObjectInputStream(in);
			result = (DataOut2) ois.readObject();
			ois.close();
			
			System.out.println("receive from end: " + result.getType());
		    //using js to store text			
			

		}
		catch (Exception e) {
//		    System.out.println(e);
			e.printStackTrace();
//		    System.exit(-1);
		}
	}

	/**
	 * @param args
	 */
	 public static void main(String args[]) {
		 String file_name;
		 try{
		    // used for reading the data file
		    FileInputStream file_stream;
		    DataInputStream file_in;
		    BufferedReader file_br;
		    String file_line;
		    String[] line_tokens;   
		    String outputString = "";
		   // String taskName = "t1";

		    // data structures used to hold the client data read in from files
			Vector<Double> ZTDrow;
			Vector< Vector<Double> > ZTDv = new Vector< Vector<Double> >();
			double[][] ZTDa;
			double[][][] ZZa;
			int[] index;
			Matrix ZTD, Z, T, Delta, DI, sumZ, sumZ_line, ZZ_line, ZZ;
			Matrix beta0, beta1, ZB, thetaZ, theta, thetaZtmp, thetatmp, Gv, G;
			Matrix TZTZ, TZTZ_line, TZTZtmp, neghessian;
			Matrix Tuniq;
			Vector< Matrix > ZZv, ZZtmp, thetaZZ, TZTZv;
			int m=-1, full_n=0, n, i, j, iter=0, k, ki;

//------------------ //check of filename will be in a different way jwc 10.9 -----------------------------------
//		    // missing filename as argument
		    file_name = "/Users/challen/Documents/workspace/cox/seer_test.txt";	//This variable will tansmit from upper  jwc 10.9		
		    //file_name = dataPath;
		    System.out.println("Using data file '" + file_name + "'.");
//---------------------------------------------------------------------------------------------------------------------------------------

			// access the file
			file_stream = new FileInputStream(file_name);
			file_in = new DataInputStream(file_stream);
			file_br = new BufferedReader(new InputStreamReader(file_in));
			while ((file_line = file_br.readLine()) != null) {
				full_n = full_n + 1;
				line_tokens = file_line.split("\t");
				if (m == -1) {
					m = line_tokens.length - 2;
				}else if (m != line_tokens.length - 2) {
					System.out.println("ERROR: data file dimensions don't " +
									   "match on line " + full_n + ".");
					System.exit(-1);
				}
				ZTDrow = new Vector<Double>();	
				for (i = 0; i < line_tokens.length; i++) {
					ZTDrow.add(new Double(line_tokens[i]));					
				}
				ZTDv.add(ZTDrow);
		    }
			file_in.close();

		    beta0 = new Matrix(m, 1, -1.0);
		    beta1 = new Matrix(m, 1, 0.0);

			//sort original data ZTD
			ZTDa = two_dim_vec_to_arr(ZTDv);
			ArrayComparator comparator = new ArrayComparator(ZTDa);
			Arrays.sort(ZTDa, comparator);
			ZTD = new Matrix(ZTDa);
			Z = ZTD.getMatrix(0,full_n-1,0,m-1);
			T = ZTD.getMatrix(0,full_n-1,m,m);
			Delta = ZTD.getMatrix(0,full_n-1,m+1,m+1); 	
		    
		    //print Z, jwc 11.1
		    Z.print(1, 1);
		 }catch(Exception e){
			 e.printStackTrace();
		 }

	}
		public static void exp(double[][] A) {
			int i,j;
			for (i = 0; i < A.length; i++) {
				for (j = 0; j < A[i].length; j++) {
					A[i][j] = Math.exp(A[i][j]);
				}
			}
		}
		public static double max_abs(double[][] matrix) {
			int i,j;
			boolean set = false;
			double max = 0;			
			// iterate through matrix
			for (i = 0; i < matrix.length; i++) {
				for (j = 0; j < matrix[i].length; j++) {
					// maintain absolute max number found
					if (!set) {
						max = Math.abs(matrix[i][j]);
						set = true;
					}else if (Math.abs(matrix[i][j]) > max) {
						max = Math.abs(matrix[i][j]);
					}
				}
			}
			return max;
		}
		/* Convert a 2D vector of Doubles into a 2D array of doubles. */
		public static double[][] two_dim_vec_to_arr(Vector< Vector<Double> >V) {
			// allocate part of the array
			double[][] A = new double[V.size()][];
			int i;
			
			// allocate and convert rows of the vector
			for (i = 0; i < V.size(); i++) {
				A[i] = one_dim_vec_to_arr(V.get(i));
			}
			
			// return 2D array
			return A;
		}	
		/* Convert a Vector of Doubles into an array of doubles. */
		public static double[] one_dim_vec_to_arr(Vector<Double> V) {
			int size = V.size();
			int i;
			double[] A = new double[size];
			
			for (i = 0; i < size; i++) {
				A[i] = (V.get(i)).doubleValue();
			}
			
			return A;
		}

	}

class ArrayComparator implements Comparator<double[]>
{
	private final double[][] array;	
	public ArrayComparator(double[][] array){
		this.array = array;
	}
	@Override
	public int compare(double[] a, double[] b) {
		if(a[array[0].length-2] > b[array[0].length-2]) return 1;
		else return 0;
	}
}