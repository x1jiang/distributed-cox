package cox;
//061213

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Math;


import Jama.Matrix;

public class ServerThread {

	  // socket connection and thread id
    private int m_thread_id;
    private String taskName = null;
    private boolean beta_comp_finish;
    private boolean sur_comp_finish;
    private boolean fn_comp_finish;
	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	private String outAddress = null;
    
	// number of participating clients
    static int num_clients;
    // memory used for storing client data
    static Vector<Matrix> Tclient;
	static Matrix sur_sort;
	static int Tdim_full=0, Tdim_sort;
	static double[] Tuniq;
    static Vector<Matrix> sumZ;
    static Vector<Matrix> DI;
    static Vector<Matrix>theta; 
    static Vector<Matrix> thetaZ; 
    static Vector<Vector<Matrix>> thetaZZ; 
    static Vector<Matrix>thetaFn; 
    static Vector<Matrix> DIFn;
	static Semaphore T_lock;
	static Semaphore T_comp_lock;
	static Semaphore Fn_lock;
	static Semaphore Fn_comp_lock;
	static Semaphore data_lock;
	static Semaphore beta_lock;
    // number of features in the data
    static int m, n;
    double epsilon;
	static Matrix beta0, beta1, BaseSurFn;
    // count the number of iterations
    static int iter;
	static int maxIteration;

    /**
     * calculate beta
     * @author Challen
     *
     */
    private class SurvivalCompare implements Runnable{
		public void run() {
			// TODO Auto-generated method stub
			try{
				int i,j;
				// wait for clients to send survival time
				for (i = 0; i < num_clients; i++) {
					T_lock.acquire();
				}
				System.out.println("SurvivalCompare start");
				// compare survival time
				int flag = 0;
				double[] Ta = new double[Tdim_full];
				Set<Double> TSet = new HashSet<Double>();				
				for(i=0; i< Tclient.size(); i++){
					for(j=0;j<Tclient.get(i).getRowDimension();j++){
						Ta[flag]=Tclient.get(i).get(j,0);
						flag+=1;
					}
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
				sur_sort=new Matrix(Tuniq,Tuniq.length);
				n = Tuniq.length;
				//System.out.println("release T_comp_lock");
				for (i = 0; i < num_clients; i++) {
					T_comp_lock.release();
				}
				
				System.out.println("Survival comparison thread exiting.");
				//set finish flag
				sur_comp_finish = true;
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
    private class BaseSurFnComputation implements Runnable {
    	public void run() {
    		try{   			
    			int i;
    			Matrix temp_a, temp_b, temp_c;
    		    for (i = 0; i < num_clients; i++) {
    				Fn_lock.acquire();
    			}
    		    System.out.println("enter BaseSurFn");
    			temp_a = row_sums(thetaFn);
    			temp_b = row_sums(DIFn);
    			temp_c = new Matrix(n,1,0);
    			for(i=0;i<n;i++){
    				temp_c.set(i, 0,temp_b.get(i,0)*(1/temp_a.get(i, 0)));
    			}
    			BaseSurFn = new Matrix(n,1,0);
    			BaseSurFn.set(0,0,temp_c.get(0,0));
    			for(i=1;i<n;i++){
    				BaseSurFn.set(i, 0,(BaseSurFn.get(i-1, 0)+temp_c.get(i,0)));
    			}
    			//write beta to DB
    			String betaString = "";
    			if(beta1.getRowDimension() > 0){
    				for(i=0; i<beta1.getRowDimension() - 1; i++){
    					betaString = betaString + beta1.get(i, 0) + "#";
    				}
    				betaString = betaString + beta1.get(beta1.getRowDimension() - 1, 0);
    			}
    			System.out.println("betastring ("+ beta1.getRowDimension() +") is "+ betaString);
    			//write sur to DB
    			String surString = "";
    			if(sur_sort.getRowDimension() > 0){
    				for(i=0; i<sur_sort.getRowDimension() - 1; i++){
    					surString = surString + sur_sort.get(i, 0) + "#";
    				}
    				surString = surString + sur_sort.get(sur_sort.getRowDimension() - 1, 0);
    			}
    			System.out.println("surString ("+ sur_sort.getRowDimension() +") is "+ surString);
    			//write Baseline hazard Fn to DB
    			String fnString = "";
    			if(BaseSurFn.getRowDimension() > 0){
    				for(i=0; i<BaseSurFn.getRowDimension() - 1; i++){
    					fnString = fnString + BaseSurFn.get(i, 0) + "#";
    				}
    				fnString = fnString + BaseSurFn.get(BaseSurFn.getRowDimension() - 1, 0);
    			}
    			System.out.println("fnString ("+ BaseSurFn.getRowDimension()  +") is "+ fnString);
    		    Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
    		    Statement stat = conn.createStatement();
    		    String sql = "insert into tempresult (taskname, beta, sur, fn) values ('" + taskName +  "', '" +betaString +  "', '" +surString +  "', '" + fnString + "')" + ";";
    		    stat.execute(sql);
    		    
    		    for (i = 0; i < num_clients; i++) {
    				Fn_comp_lock.release();
    			}
    		    System.out.println("Baseline Hazard Function thread exiting.");
    		    fn_comp_finish = true;
    		}catch (Exception e) {
    		    e.printStackTrace();
    		}
    	}
    	private Matrix row_sums(Vector<Matrix> D){
    	    Matrix sums;
    	    sums = new Matrix(D.get(0).getRowDimension(), D.get(0).getColumnDimension(), 0);
    	    for(int i=0; i<D.size(); i++){
    	    	sums = sums.plus(D.get(i));
    	    }
    	    return sums;
    	}
    }
    private class BetaComputation implements Runnable {
	public void run() {
	    try {
	    int i,j, k;
		Matrix temp_a, temp_b, temp_c, temp_d, temp_e, temp_f;
		Vector<Matrix> temp_g, TZTZv;
		Matrix G, neghessian;
		double[][][] TZTZa, thetaZZsum;
		Matrix TZTZ_line, TZTZtmp, thetaZZtmp;
		// iteratively update beta1
		System.out.println("Computation started");
		while (max_abs((beta1.minus(beta0)).getArray()) > epsilon) {
		    // if (iter == 2)
		    // 	break;
		     if (iter == maxIteration)//added by lph
		     {
		    	 System.out.println("Has reached the maximum iteration number!");
		    	 break; 
		     }	
//		    System.out.println("value: " + max_abs((beta1.minus(beta0))
//							   .getArray()));
		    
		    // wait for all clients to write data
		    for (i = 0; i < num_clients; i++) {
			data_lock.acquire();
//			System.out.println("Comp: One data lock acquired");
		    }
//		    System.out.println("comp: client data available for " +
//				       "iter " + iter);

		    System.out.println("Iteration " + iter);

		    beta0 = beta1.copy();
		    /*
		    System.out.println("dimension for all: ");
		    System.out.println("sumZ: "+sumZ.get(0).getRowDimension()+" by "+sumZ.get(0).getColumnDimension());
		    System.out.println("thetaZ: "+thetaZ.get(0).getRowDimension()+" by "+thetaZ.get(0).getColumnDimension());
		    System.out.println("theta: "+theta.get(0).getRowDimension()+" by "+theta.get(0).getColumnDimension());
		    System.out.println("DI: "+DI.get(0).getRowDimension()+" by "+DI.get(0).getColumnDimension());
		    System.out.println("thetaZZ: "+thetaZZ.get(0).get(0).getRowDimension()+" by "+thetaZZ.get(0).get(0).getColumnDimension());
		    */
		    //calculate gradient
			temp_a = row_sums(sumZ);
			temp_b = row_sums(thetaZ);
			temp_c = row_sums(theta);
			temp_d = row_sums(DI);
			temp_e = new Matrix(n,m,0);
			temp_f = new Matrix(n,m,0);
			temp_g = new Vector<Matrix>();
			/*
			System.out.println("dimension for all temp: ");
			System.out.println("sumZ: "+temp_a.getRowDimension()+" by "+temp_a.getColumnDimension());
		    System.out.println("thetaZ: "+temp_b.getRowDimension()+" by "+temp_b.getColumnDimension());
		    System.out.println("theta: "+temp_c.getRowDimension()+" by "+temp_c.getColumnDimension());
		    System.out.println("DI: "+temp_d.getRowDimension()+" by "+temp_d.getColumnDimension());
			System.out.println("n = " + n + ", m = " + m);
		    */
		    for(i=0;i<n;i++){
				temp_e.setMatrix(i,i,0,m-1,(temp_b.getMatrix(i,i,0,m-1).times(1/temp_c.get(i,0))));
			}
			for(i=0;i<n;i++){
				temp_f.setMatrix(i,i,0,m-1,(temp_e.getMatrix(i,i,0,m-1).times(temp_d.get(i,0))));
			}					
			G = row_sums(temp_a.minus(temp_f));
			//calculate hessian
			thetaZZsum=row_sums(thetaZZ);
			System.out.println("thetaZZ: "+thetaZZsum[0].length+" by "+thetaZZsum[0][0].length);
			for(i=0;i<n;i++){
				thetaZZtmp = new Matrix(thetaZZsum[i]);
				thetaZZtmp = thetaZZtmp.times(1/temp_c.get(i,0)).copy();
				temp_g.add(thetaZZtmp);
			}		
			System.out.println("error4");
			TZTZa = new double[n][m][m];
			for (i=0; i<m; i++) {
				for (j=0; j<m; j++) {
					TZTZ_line = temp_e.getMatrix(0,n-1,i,i).arrayTimes(temp_e.getMatrix(0,n-1,j,j)).copy();
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
				neghessian.plusEquals(temp_g.get(i).minus(TZTZv.get(i)).times(temp_d.get(i,0)));
			}
			neghessian.plusEquals(Matrix.identity(m,m).times(epsilon));
			beta1=beta0.plus(neghessian.inverse().times(G.transpose())).copy();
		    
		    System.out.println("beta1 is :");
		    beta1.print(10,12);
		     
//		    System.out.println("comp: releasing beta1 lock for iter " +
//				       iter);
		    // indicate to threads that beta1 is available
		    for (i = 0; i < num_clients; i++) {
			beta_lock.release();
		    }
		    iter = iter + 1;
		}
		
//		System.out.println("value on exit: " + max_abs((beta1
//		        .minus(beta0)).getArray()));
		//Computation finish, set some variable to initial state, set finish flag
//		System.out.println("Beta Computation thread exiting.");
		beta_comp_finish = true;
		
		//write beta and time to taskName_varOutput.txt jwc 11.9
	    File varOutput = new File(outAddress + taskName + "_varOutput.txt");
//		System.out.println("The addresss of cox server servlet is : " + (new File("")).getAbsolutePath());
		FileWriter fw = new FileWriter(varOutput, true);
		fw.write("\r\n The beta value is: \r\n");
		for(i=0; i<beta1.getRowDimension() -1; i++){
			fw.write(beta1.get(i, 0) + "\t");
		}
		fw.write(beta1.get( (beta1.getRowDimension() -1), 0) +"");
		fw.write("\r\n");
		fw.write("\r\n The Time is: \r\n");
		for(i=0; i<sur_sort.getRowDimension() -1; i++){
			fw.write(sur_sort.get(i, 0) + ", ");
		}
		fw.write(sur_sort.get( (sur_sort.getRowDimension() -1), 0) +"");
		fw.write("\r\n");
		fw.close();
		
	    }
	    catch (Exception e) {
//		System.out.println(e);
	    e.printStackTrace();
//		System.exit(-1);
	    }
	}
	
	/* Return a one dimensional array that is the sum of the E.length
	   one dimensional vectors. */
	private Matrix row_sums(Vector<Matrix> D){
	    Matrix sums;
	    sums = new Matrix(D.get(0).getRowDimension(), D.get(0).getColumnDimension(), 0);
	    for(int i=0; i<D.size(); i++){
	    	sums = sums.plus(D.get(i));
	    }
	    return sums;
	}
	private Matrix row_sums(Matrix E) {			
		int i, j;
		double [][] sums;
		Matrix sum;
		sums = new double[1][m];
		// init sums
		for (i = 0; i < m; i++) {
			sums[0][i] = 0.0;
		}
		// for each client, add its contribution to sums
		for (i = 0; i < n; i++) {
			for (j = 0; j < m; j++) {
				sums[0][j] = sums[0][j] + E.get(i,j);
			}
		}
		sum = new Matrix(sums);
		return sum;
	}
	private double[][][] row_sums(Vector<Vector<Matrix>> D) {
		int i,j,k,l;
		double[][][] sums;
		sums = new double[n][m][m];
		// init sums
		for (i = 0; i < n; i++) {
			for (j = 0; j < m; j++) {
				for (k = 0; k < m; k++) {
					sums[i][j][k] = 0;
				}
			}
		}
		// for each client, add its contribution to sums
		for (i = 0; i < num_clients; i++) {
			for (j = 0; j < n; j++) {
				for (k = 0; k < m; k++) {
					for (l = 0; l < m; l++) {
						sums[j][k][l] = sums[j][k][l] + D.get(i).get(j).get(k, l);
					}
				}
			}
		}
		return sums;
	}
    }
    public void addFn(DataIn2 dataIn, HttpServletResponse res){
    	try{
    		System.out.println("enter addFn");
    		OutputStream out = res.getOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(out);
    		thetaFn.add(dataIn.getD());
    		DIFn.add(dataIn.getH());
    	    // signal the computation thread: this client's data has arrived
    	    Fn_lock.release();  	
    	    // wait for computation thread to finish computing survival function
    	    Fn_comp_lock.acquire();
    	    // send survival time to client
    	    oos.writeObject(new DataOut2(BaseSurFn, "fn", dataIn.getTaskName()));
    	    oos.flush();
    	    oos.close();
    	    thetaFn.clear();
    	    DIFn.clear();
    	    System.out.println("leave addFn");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public void addSurvival(DataIn2 dataIn, HttpServletResponse res){
    	try{
    		OutputStream out = res.getOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(out);
    		Matrix buffer=dataIn.getD();
    		Tdim_full+=buffer.getRowDimension();
    		Tclient.add(buffer);
    	    // signal the computation thread: this client's data has arrived
    	    T_lock.release();  	
    	    // wait for computation thread to finish computing survival time
    	    T_comp_lock.acquire();
    	    // send survival time to client
    	    oos.writeObject(new DataOut2(sur_sort, "sur", dataIn.getTaskName()));
    	    oos.flush();
    	    oos.close();
    	    Tclient.clear();
    	    Tdim_full=0;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
 
    public void addBetaData(DataIn2 dataIn, HttpServletResponse res){

    	try {
 //-------------define the output to clients jwc 10.17-----------------------
    		OutputStream out = res.getOutputStream();
    		ObjectOutputStream oos = new ObjectOutputStream(out);
    	    
    	    // read data from clients and send beta to clients
//    	    while (max_abs((beta1.minus(beta0)).getArray()) > epsilon) {
    		if (max_abs((beta1.minus(beta0)).getArray()) > epsilon && iter<maxIteration) {		//jwc 10.18
	    		// if (iter == 2)
	    		//     break;
        	    theta.add(dataIn.getD());
        	    thetaZ.add(dataIn.getE());
        	    thetaZZ.add(dataIn.getF());
        	    sumZ.add(dataIn.getG());
        	    DI.add(dataIn.getH()); 
        	    
	//-----------------------------------------------------------------------------
	    		// release lock, indicating data is ready from this thread
	    		System.out.println(m_thread_id + ": releasing " +
	    				   "data lock for iter " + iter);
	    		data_lock.release();
	    		// wait for computation thread to finish computing beta1
	    		beta_lock.acquire();
	    		System.out.println(m_thread_id + ": sending beta1 " +
	    				   "for iter " + iter);
	
//--------------------------send by object and in type dataOut2 jwc 10.17-----------	
//	    		// send beta1 to clients
//	    		for (i = 0; i < m; i++) {
//	    		    m_out.writeDouble(beta1.get(i,0));
//	    		}
	    		
	    		oos.writeObject(new DataOut2(beta1, "beta", dataIn.getTaskName()));
	    		oos.flush();
	    		oos.close();
//----------------------------------------------------------------------------------
//	    		iter = iter + 1;
    	    }
    	    
    	    //D must be cleared after beta computation jwc 10.17
    		theta.clear();
    		thetaZ.clear();
    		thetaZZ.clear();
    		sumZ.clear();
    		DI.clear();
    	    System.out.println("Beta transmission finish iteration " + iter);
//    	    System.out.println("Thread " + m_thread_id + " exiting.");
    	}
    	catch (Exception e) {
//    	    System.out.println(e);
//    	    System.exit(-1);
    		e.printStackTrace();
    	}
    }
    public static void main(String[] args) {
    }

    ServerThread(DataIn2 dataIn, int numClient, int maxIteration, double epsilon, int featureNum, Properties confProperties){
    	this.taskName = dataIn.getTaskName();
    	this.num_clients = numClient;
    	this.epsilon= epsilon;
    	this.maxIteration=maxIteration;
    	
		dbconnection_property = confProperties.getProperty("dbconnection");
		dbusername_property = confProperties.getProperty("dbusername");
		dbpassword_property = confProperties.getProperty("dbpassword");
		root_property = confProperties.getProperty("root");
		this.outAddress = confProperties.getProperty("outAddress");

        beta_comp_finish = false;
        sur_comp_finish = false;
        fn_comp_finish = false;
    	iter = 0;
	    m = featureNum - 2;	//feature - (survival time and indicator)
    	try {
	    int i;
	    // allocate memory for the client's data
	    Tclient = new Vector<Matrix>();
	    sumZ = new Vector<Matrix>();
	    DI = new Vector<Matrix>();
	    theta = new Vector<Matrix>();
	    thetaZ = new Vector<Matrix>();
	    thetaZZ = new Vector<Vector<Matrix>>();
	    thetaFn = new Vector<Matrix>();
	    DIFn = new Vector<Matrix>();
		// init beta variable
		beta0 = new Matrix(m, 1, -1.0);
		beta1 = new Matrix(m, 1, 0.0);

		//initiate iteration
		iter = 0;

	    // init data semaphore used to ensure all client data has arrived
	    data_lock = new Semaphore(num_clients);
	    for (i = 0; i < num_clients; i++) {
		data_lock.acquire();
	    }

	    /* init beta semaphore to block threads from sending beta before
	       the computation thread finishes computing it */
	    beta_lock = new Semaphore(num_clients);
	    for (i = 0; i < num_clients; i++) {
		beta_lock.acquire();
	    }
	    
		T_lock = new Semaphore(num_clients);
		for (i = 0; i < num_clients; i++) {
			T_lock.acquire();
		}
		T_comp_lock = new Semaphore(num_clients);
		for (i = 0; i < num_clients; i++) {
			T_comp_lock.acquire();
		}
		Fn_lock = new Semaphore(num_clients);
		for (i = 0; i < num_clients; i++) {
			Fn_lock.acquire();
		}
		Fn_comp_lock = new Semaphore(num_clients);
		for (i = 0; i < num_clients; i++) {
			Fn_comp_lock.acquire();
		}
	    /*init task status semaphore to block threads from sending task end status  lph 11*15*/
/*	    task_end_status=new Semaphore(num_clients);
	    for(i=0;i<num_clients;i++){
	    	task_end_status.acquire();
	    }*/
	    
	    // spawn computational thread
	    (new Thread(new SurvivalCompare())).start();
	    (new Thread(new BetaComputation())).start();
	    (new Thread(new BaseSurFnComputation())).start();
	
	    System.out.println("The address of server thread is:" + new File("").getAbsolutePath());
	    System.out.println("Main thread exiting.");
	}
	catch(Exception e) {
//	    System.out.println(e);
//	    System.exit(-1);
		e.printStackTrace();
	}
    }
    public String getTaskName() {
		return taskName;
	}
    public boolean isBetaFinish(){
    	return beta_comp_finish;
    }
    public boolean isSurFinish(){
    	return sur_comp_finish;
    }
    public boolean isfnFinish(){
    	return fn_comp_finish;
    }

    /* Returns the absolute maximum of the elements in the two dimensional
       array matrix. */
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
		}
		else if (Math.abs(matrix[i][j]) > max) {
		    max = Math.abs(matrix[i][j]);
		}
	    }
	}

	return max;
    }
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