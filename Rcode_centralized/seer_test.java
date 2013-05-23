import Jama.Matrix;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
//centric version 
public class seer_test {
	public static void main( String[] args ) {
		try {
			//client variable
			String file_name;
			FileInputStream file_stream;
			DataInputStream file_in;
			BufferedReader file_br;
			String file_line;
			String[] line_tokens;				
			Vector<Double> ZTDrow;
			Vector< Vector<Double> > ZTDv = new Vector< Vector<Double> >();
			double[][] ZTDa;
			double[] DIa;
			double[][][] ZZa, TZTZa;
			int[] index;
			Matrix ZTD, Z, T, Delta, DI, sumZ, sumZ_line, ZZ_line, ZZ;
			Matrix beta0, beta1, ZB, thetaZ, theta, thetaZtmp, Gv, G;
			Matrix TZTZ, TZTZ_line, TZTZtmp, neghessian;
			Vector< Matrix > ZZv, ZZtmp, thetaZZ, TZTZv;
			int m=-1, full_n=0, n, i, j, iter=0, k, ki;
			double epsilon = Math.pow(10.0, -6.0);
			int maxIteration=20;
			
			//server variable
			Vector<Matrix> Tv = new Vector<Matrix>();
			double[] Ta, Tuniq;
			Set<Double> TSet = new HashSet<Double>();
			Matrix Tsort;
			
			//read file
			file_name = "seer_test.txt";
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
			
			//sort original data ZTD
			ZTDa = two_dim_vec_to_arr(ZTDv);
			ArrayComparator comparator = new ArrayComparator(ZTDa);
			Arrays.sort(ZTDa, comparator);
			ZTD = new Matrix(ZTDa);
			Z = ZTD.getMatrix(0,full_n-1,0,m-1);
			T = ZTD.getMatrix(0,full_n-1,m,m);
			Delta = ZTD.getMatrix(0,full_n-1,m+1,m+1); 	

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
			
			//sort T in server
			Tv.add(T);	//add T for each client
			Ta = new double[Tv.size()*Tv.get(0).getRowDimension()];	//covert Tv to 1 dim array
			for(i=0; i< Tv.size(); i++){
				System.arraycopy(Tv.get(i).getColumnPackedCopy(), 0, Ta, i*Tv.get(0).getRowDimension(), Tv.get(0).getRowDimension());
			}
			for (i = 0; i < Ta.length; i++){
				TSet.add(Ta[i]);
			}
			Tuniq = new double[TSet.size()];
			Object[] tempArray = TSet.toArray();
			for (i = 0; i < tempArray.length; i++) {
				Tuniq[i] = (Double) tempArray[i];
			}
			Arrays.sort(Tuniq);
			Tsort = new Matrix(Tuniq,Tuniq.length);	
			//send Tsort back
			Tuniq = Tsort.getColumnPackedCopy();
			n = Tuniq.length;
			
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
			
			//calculate DI, sumZ, index		
			sumZ_line = new Matrix(1,m,0);
			sumZ = new Matrix(n,m,0);
			DIa = new double[n];
			index = new int[n];
			k=0;ki=0;
			index[0]=1;
			for (i=0; i<full_n; i++) {
				if(T.get(i,0)==Tuniq[k]){
					if (Delta.get(i,0)==1){
						DIa[k]+=1;
						sumZ_line.plusEquals(Z.getMatrix(i,i,0,m-1));
					}
					ki+=1;
				}else {
					sumZ.setMatrix(k,k,0,m-1,sumZ_line);
					sumZ_line = new Matrix(1,m,0);
					k+=1;i-=1;index[k]=ki;
				}
			}
			DI = new Matrix(DIa,DIa.length);
			
			beta0 = new Matrix(m, 1, -1.0);
		    beta1 = new Matrix(m, 1, 0.0);
			iter=0;
			while (max_abs((beta1.minus(beta0)).getArray())>epsilon && iter<maxIteration) {	
				beta0 = beta1.copy();
				//calculate theta
				ZB = Z.times(beta0);
				exp(ZB.getArray());
				theta = new Matrix(full_n,1,0);
				theta.set(full_n-1,0,ZB.get(full_n-1,0));
				for(i=full_n-2;i>=0;i--){
					theta.set(i,0,(ZB.get(i,0)+theta.get(i+1,0)));
				}
				//calculate thetaZ
				thetaZtmp = new Matrix(full_n,m,0);
				for(i=0;i<full_n;i++){
					thetaZtmp.setMatrix(i,i,0,m-1,Z.getMatrix(i,i,0,m-1).times(ZB.get(i,0)));
				}
				for(i=full_n-2;i>=0;i--){
					thetaZtmp.setMatrix(i,i,0,m-1,thetaZtmp.getMatrix(i+1,i+1,0,m-1).plus(thetaZtmp.getMatrix(i,i,0,m-1)));
				}
				for(i=0;i<full_n;i++){
					thetaZtmp.setMatrix(i,i,0,m-1,thetaZtmp.getMatrix(i,i,0,m-1).times(1/theta.get(i,0)));
				}
				thetaZ= new Matrix(n,m,0);
				for(i=0;i<n;i++){
					thetaZ.setMatrix(i,i,0,m-1,thetaZtmp.getMatrix(index[i],index[i],0,m-1));
				}
				TZTZ=thetaZ.copy();
				for(i=0;i<n;i++){
					thetaZ.setMatrix(i,i,0,m-1,thetaZ.getMatrix(i,i,0,m-1).times(DI.get(i,0)));
				}
				//caculate gradient
				Gv = sumZ.minus(thetaZ);
				G = new Matrix(1,m,0);
				for(i=0;i<n;i++){
					G.plusEquals(Gv.getMatrix(i,i,0,m-1));
				}
				//caculate thetaZZ
				ZZtmp = new Vector< Matrix >();
				for(i=0;i<full_n;i++){
					ZZtmp.add(ZZv.get(i).times(ZB.get(i,0)));
				}
				for(i=full_n-2;i>=0;i--){
					ZZtmp.set(i,ZZtmp.get(i).plus(ZZtmp.get(i+1)));
				}
				for(i=0;i<full_n;i++){
					ZZtmp.set(i,ZZtmp.get(i).times(1/theta.get(i,0)));
				}
				thetaZZ = new Vector< Matrix >();
				for(i=0;i<n;i++){					
					thetaZZ.add(ZZtmp.get(index[i]));
				}				

				TZTZa = new double[n][m][m];
				for (i=0; i<m; i++) {
					for (j=0; j<m; j++) {
						TZTZ_line = TZTZ.getMatrix(0,n-1,i,i).arrayTimes(TZTZ.getMatrix(0,n-1,j,j)).copy();
						for(k=0;k<n;k++){
							TZTZa[k][i][j]=TZTZ_line.get(k,0);
						}
					}
				}
				TZTZv = new Vector< Matrix >();
				for(k=0;k<n;k++){
					TZTZtmp=new Matrix(TZTZa[k]);
					TZTZv.add(TZTZtmp);
				}
				neghessian = new Matrix(m,m,0);
				for(i=0;i<n;i++){
					neghessian.plusEquals(thetaZZ.get(i).minus(TZTZv.get(i)).times(DI.get(i,0)));
				}
				neghessian.plusEquals(Matrix.identity(m,m).times(epsilon));
				beta1=beta0.plus(neghessian.inverse().times(G.transpose())).copy();
				iter+=1;
			}
		}
		catch(FileNotFoundException fnfe){			
			System.out.println(fnfe.getMessage());			
		}
		
		catch(IOException ioe){
			System.out.println(ioe.getMessage());			
		}
	}
	/* Set each element of the 2D double array to e^a where a is the value of
	 an element. */
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
