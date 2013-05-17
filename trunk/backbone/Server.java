import java.net.*;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.Vector;
import java.util.*;
import java.io.*;
import Jama.Matrix;
import java.lang.*;

//cox server
class Server implements Runnable {

    // socket connection and thread id
    private Socket m_socket;
    private int m_thread_id;
    private DataOutputStream m_out;
    private DataInputStream m_in;

    // number of participating clients
    static int num_clients;
    // memory used for storing client data
	static int[] Tdim;
	static int Tdim_full=0, Tdim_sort;
	static Vector<Vector<Double>> Tclient;
	static double[] Tuniq;
	static double[][][] sumZ, thetaZ;
	static double[][] DI, theta;
	static double[][][][] thetaZZ;
	static Semaphore T_lock;
	static Semaphore T_comp_lock;
	static Semaphore Data_lock;
	static Semaphore Beta_lock;
    // number of features in the data
    static int m, n;
    static double epsilon = Math.pow(10.0, -6.0);
	static Matrix beta0, beta1;
    // count the number of iterations
    static int iter;
	static int maxIteration=20;

    private static class Computation implements Runnable {
		public void run() {
			try {
				int i,j, k;
				Matrix temp_a, temp_b, temp_c, temp_d, temp_e, temp_f;
				Vector<Matrix> temp_g, TZTZv;
				Matrix G, neghessian;
				double[][][] TZTZa, thetaZZsum;
				Matrix TZTZ_line, TZTZtmp, thetaZZtmp;
				//sort T
				//System.out.println("get T from client");
				for (i = 0; i < num_clients; i++) {
					T_lock.acquire();
				}				
				System.out.println("T available for sort:"+ Tdim_full);
				int flag = 0;
				double[] Ta = new double[Tdim_full];
				Set<Double> TSet = new HashSet<Double>();				
				for(i=0; i< Tclient.size(); i++){
					for(j=0;j<Tclient.get(i).size();j++){
						Ta[flag]=Tclient.get(i).get(j);
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
				n = Tuniq.length;
				//System.out.println("release T_comp_lock");
				for (i = 0; i < num_clients; i++) {
					T_comp_lock.release();
				}
				// init beta vectors
				beta0 = new Matrix(m, 1, -1.0);
				beta1 = new Matrix(m, 1, 0.0);
				iter = 0;
				while (max_abs((beta1.minus(beta0)).getArray()) > epsilon && iter<maxIteration) {
					//System.out.println("get Data from client");
					for (i = 0; i < num_clients; i++) {
						Data_lock.acquire();
					}
					beta0 = beta1.copy();
					System.out.println("Beta available for compute:"+iter);
					//caculate gradient
					temp_a = new Matrix(row_sums(sumZ));					
					temp_b = new Matrix(row_sums(thetaZ));
					temp_c = new Matrix(row_sums(theta));
					temp_d = new Matrix(row_sums(DI));
					temp_e = new Matrix(n,m,0);
					temp_f = new Matrix(n,m,0);
					temp_g = new Vector<Matrix>();
					for(i=0;i<n;i++){
						temp_e.setMatrix(i,i,0,m-1,(temp_b.getMatrix(i,i,0,m-1).times(1/temp_c.get(i,0))));
					}
					for(i=0;i<n;i++){
						temp_f.setMatrix(i,i,0,m-1,(temp_e.getMatrix(i,i,0,m-1).times(temp_d.get(i,0))));
					}
					G = new Matrix(row_sums(temp_a.minus(temp_f)));
					//caculate hessian
					thetaZZsum=row_sums(thetaZZ);
					for(i=0;i<n;i++){
						thetaZZtmp = new Matrix(thetaZZsum[i]);
						thetaZZtmp = thetaZZtmp.times(1/temp_c.get(i,0)).copy();
						temp_g.add(thetaZZtmp);
					}					
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
					//System.out.println("beta at:"+iter);
					//beta1.print(8,8);
					//System.out.println("release Beta_lock");
					for (i = 0; i < num_clients; i++) {
						Beta_lock.release();
					}
					iter = iter + 1;
				}
				beta1.print(8,8);
				System.out.println("Computation thread exiting.");
			}catch (Exception e) {
				System.out.println(e);
				System.exit(-1);
			}
		}
		//return sums as one dimention vector
		private double[][] row_sums(double[][] E) {			
			int i, j;
			double [][] sums;
			sums = new double[n][1];
			// init sums
			for (i = 0; i < n; i++) {
				sums[i][0] = 0.0;
			}
			// for each client, add its contribution to sums
			for (i = 0; i < num_clients; i++) {
				for (j = 0; j < n; j++) {
					sums[j][0] = sums[j][0] + E[i][j];
				}
			}
			return sums;
		}
		//return sums as two dimention vector
		private double[][] row_sums(double[][][] D) {
			int i,j,k;
			double[][] sums;
			sums = new double[n][m];
			// init sums
			for (i = 0; i < n; i++) {
				for (j = 0; j < m; j++) {
					sums[i][j] = 0;
				}
			}
			// for each client, add its contribution to sums
			for (i = 0; i < num_clients; i++) {
				for (j = 0; j < n; j++) {
					for (k = 0; k < m; k++) {
						sums[j][k] = sums[j][k] + D[i][j][k];
					}
				}
			}
			return sums;
		}
		private double[][][] row_sums(double[][][][] D) {
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
							sums[j][k][l] = sums[j][k][l] + D[i][j][k][l];
						}
					}
				}
			}
			return sums;
		}
		private double[][] row_sums(Matrix E) {			
			int i, j;
			double [][] sums;
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
			return sums;
		}
    }
	public static void main(String[] args) {
		try {
			int i;
			ServerSocket server_socket;
			Socket connection = null;

			// configuration file and accessing data structures
			FileInputStream config_stream;
			DataInputStream config_in;
			BufferedReader config_br;
			String config_line;
			String[] line_contents;
			HashMap<String, String> config;
			String config_key, config_value;

			// read configuration info. from file client_config
			config_stream = new FileInputStream("server_config");
			config_in = new DataInputStream(config_stream);
			config_br = new BufferedReader(new InputStreamReader(config_in));

			// read configuration information and populate hash map config
			config = new HashMap<String, String>();
			while ((config_line = config_br.readLine()) != null) {
				line_contents = config_line.split("=");
				config_key = line_contents[0].trim();
				config_value = line_contents[1].trim();
				config.put(config_key, config_value);
			}

			// set the number of participating clients
			num_clients = Integer.parseInt(config.get("clients"));
			// set number of features in the data
			m = Integer.parseInt(config.get("features"));
			// set number of timeslot in the data
			n = Integer.parseInt(config.get("time"));
			
			// allocate memory for the client's data
			Tdim = new int[num_clients];
			Tclient = new Vector<Vector<Double>>();
			sumZ = new double[num_clients][n][m];
			DI = new double[num_clients][n];
			thetaZ = new double[num_clients][n][m];
			theta = new double[num_clients][n];
			thetaZZ = new double[num_clients][n][m][m];
			
			T_lock = new Semaphore(num_clients);
			for (i = 0; i < num_clients; i++) {
				T_lock.acquire();
			}
			T_comp_lock = new Semaphore(num_clients);
			for (i = 0; i < num_clients; i++) {
				T_comp_lock.acquire();
			}
			Data_lock = new Semaphore(num_clients);
			for (i = 0; i < num_clients; i++) {
				Data_lock.acquire();
			}
			Beta_lock = new Semaphore(num_clients);
			for (i = 0; i < num_clients; i++) {
				Beta_lock.acquire();
			}
			// spawn computational thread
			(new Thread(new Computation())).start();
	    
			// connect each client, then spawn a thread for each client
			server_socket = new 
			ServerSocket(Integer.parseInt(config.get("socket")));
			for (i = 0; i < num_clients; i++) {
				connection = server_socket.accept();
				(new Thread(new Server(connection, i))).start();
			}
			System.out.println("Main thread exiting.");
		}catch(Exception e) {
			System.out.println(e);
			System.exit(-1);
		}
    }

    Server(Socket socket, int thread_id) {
		try {
			// seat socket connection and thread id
			m_socket = socket;
			m_thread_id = thread_id;
			m_out = new DataOutputStream(m_socket.getOutputStream());
			m_in = new DataInputStream(m_socket.getInputStream());
		}catch (Exception e) {
			System.out.println("ERROR: Exception occured in Server's " +
			       "constructor.");
			System.exit(-1);
		}
    }

    public void run() {
		try {
			int i, j, k;
			int iter = 0;
			Tdim[m_thread_id]=m_in.readInt();
			Tdim_full+=Tdim[m_thread_id];
			Vector<Double> buffer = new Vector<Double>();
			for(i=0;i<Tdim[m_thread_id];i++){
				buffer.add(m_in.readDouble());
			}
			Tclient.add(m_thread_id,buffer);
			//read T from client and sent sortT from server
			//System.out.println(m_thread_id + ": recieve T");
			T_lock.release();
			//wait for computation thread to finish sorting T
			T_comp_lock.acquire();
			System.out.println(m_thread_id + ": send sortT");
			m_out.writeInt(Tuniq.length);
			for(i=0;i<Tuniq.length;i++){
				m_out.writeDouble(Tuniq[i]);
			}
			//read sumZ from client
			//System.out.println(m_thread_id + ": recieve sumZ");
			for(i=0;i<n;i++){
				for(j=0;j<m;j++){
					sumZ[m_thread_id][i][j] = m_in.readDouble();
				}
			}
			//System.out.println(m_thread_id + ": recieve DI");
			for(i=0;i<n;i++){
				DI[m_thread_id][i] = m_in.readDouble();
			}
			while (max_abs((beta1.minus(beta0)).getArray()) > epsilon && iter<maxIteration) {
				//read Data from client and sent Beta from server
				//System.out.println(m_thread_id + ": receive Data");
				for(i=0;i<n;i++){
					theta[m_thread_id][i] = m_in.readDouble();
				}
				for(i=0;i<n;i++){
					for(j=0;j<m;j++){
						thetaZ[m_thread_id][i][j] = m_in.readDouble();
					}
				}
				for(i=0;i<n;i++){
					for(j=0;j<m;j++){
						for(k=0;k<m;k++){
							thetaZZ[m_thread_id][i][j][k] = m_in.readDouble();
						}
					}
				}				
				Data_lock.release();
				//System.out.println(m_thread_id + ": Data_lock release");
				//wait for computation thread to finish computing Beta
				Beta_lock.acquire();
				System.out.println(m_thread_id + ": send Beta");
				for(i=0;i<m;i++){
					m_out.writeDouble(beta1.get(i,0));
				}
				iter = iter + 1;
			}
			System.out.println("Thread " + m_thread_id + " exiting.");
		}catch (Exception e) {
			System.out.println(e);
			System.exit(-1);
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
				}
				else if (Math.abs(matrix[i][j]) > max) {
					max = Math.abs(matrix[i][j]);
				}
			}
		}		
		return max;
    }
}
