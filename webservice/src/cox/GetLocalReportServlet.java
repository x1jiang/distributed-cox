package cox;
import java.sql.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.net.URLConnection;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import Jama.Matrix;
//061213
public class GetLocalReportServlet extends HttpServlet{
	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	private String outAddress = null;
	
	public void init(ServletConfig conf) throws ServletException {
		try
		{
			Properties properties = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
			properties.load(is);
			
			dbconnection_property = properties.getProperty("dbconnection");
			dbusername_property = properties.getProperty("dbusername");
			dbpassword_property = properties.getProperty("dbpassword");
			root_property = properties.getProperty("root");
			outAddress = properties.getProperty("outAddress");
		}
		catch(IOException e)
		{
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		//----------------added by lph--------------------------------//
		String dataPath = null;
		String param=null;//added by lph
		String email = null;
		String taskName = null;
		int numClient = 0;
		if (req.getParameter("email") != null)
		{
			email = req.getParameter("email");
			req.getSession().setAttribute("email", email);
		}
		else if (req.getSession().getAttribute("email") != null)
		{
		     email = (String)req.getSession().getAttribute("email");
		
		}
		
	    if (req.getParameter("taskName") != null)
	    {
			taskName = req.getParameter("taskName");
			req.getSession().setAttribute("taskName", taskName);
	    }
	    else if (req.getSession().getAttribute("taskName") != null)
	    {
	         taskName = (String)req.getSession().getAttribute("taskName");
		
	    }
	    
	    Connection con = null;
		try {
			con = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    Statement stat = null;
		try {
			stat = con.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	  String sql = "select u.datapath, g.parameters, g.taskStatus, g.property from user u, gtask g where u.task_id=g.id and g.name='" + taskName + "' and u.email='" + email + "';";
	  ResultSet rs = null;
	try {
		rs = stat.executeQuery(sql);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  try {
		if(rs.first())
		  {
		 	 dataPath=rs.getString(1);
		 	 param=rs.getString(2);//added by lph 
		  }
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  String[] paramArray = param.split("#");//added by lph
	  int maxIteration=Integer.parseInt(paramArray[1]);
	  double epsilon= Double.parseDouble(paramArray[3]);
	  try {
		int taskStatus = rs.getInt(3);
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  String[] vars = null;
	try {
		vars = rs.getString(4).split("\t");
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}	  
	  int featureNum = vars.length;
	  int fm = featureNum;
		try {
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			stat.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			 String file_name;
	    // used for reading the data file
			 FileInputStream file_stream;
			 DataInputStream file_in;
			 BufferedReader file_br;
			 String file_line;
			 String[] line_tokens;
			  Vector< Vector<Double> > Xv = new Vector< Vector<Double> >();
			    Vector<Double> Yv = new Vector<Double>();
			    Vector<Double> xrow;

			    // data structures used to hold client data for matrix constructors
			    double[][] Xa;
			    double[] Ya;

			    // number of columns and rows in data file
			    int m = -1;
			    int n;

			    //double epsilon = Math.pow(10.0, -6.0);
			    double []row;
			    Matrix beta0;
			    Matrix beta1;
			    Matrix hat_beta;
			    Matrix cov_matrix, SD, aucValue;
			    Matrix X, Y;
			    Matrix P, W, D, E;
			    Matrix temp_a, temp_b, temp_c;

			    int i, j;

			    // count the number of iterations
			    int iter = 0;

	//------------------ //check of filename will be in a different way jwc 10.9 -----------------------------------
//			    // missing filename as argument
			    //file_name = "C:/Users/Wenchao/workspace/GLORE_Develop/ca_part2";	//This variable will tansmit from upper  jwc 10.9		
			    file_name = dataPath;
			    System.out.println("Using data file '" + file_name + "'.");
	//---------------------------------------------------------------------------------------------------------------------------------------

			    // access the file
			    file_stream = new FileInputStream(file_name);
			    file_in = new DataInputStream(file_stream);
			    file_br = new BufferedReader(new InputStreamReader(file_in));

			    // read file and populate X and Y matrices
			    n = 0;
			    //ignore the first line  jwc 10.26
			    file_br.readLine();
			    while ((file_line = file_br.readLine()) != null) {
				// update number of rows
				n = n + 1;
				line_tokens = file_line.split("\t");

				// detect number of columns in data file
				if (m == -1) {
				    m = line_tokens.length;
				}
				// line in data file does not match dimensions
				else if (m != line_tokens.length) {
				    System.out.println("ERROR: data file dimensions don't " +
						       "match on line " + n + ".");
				    System.exit(-1);
				}

				// populate data structures with data
				xrow = new Vector<Double>();
				xrow.add(1.0);
				for (i = 0; i < line_tokens.length - 1; i++) {
				    xrow.add(new Double(line_tokens[i]));
				}
				Xv.add(xrow);
				Yv.add(new Double(line_tokens[line_tokens.length-1]));
			    }

			    // close input stream
			    file_in.close();

			    beta0 = new Matrix(m, 1, -1.0);
			    beta1 = new Matrix(m, 1, 0.0);
			    cov_matrix = new Matrix(m, m);
			    aucValue = new Matrix(1,1,0);

			    // convert data into arrays to be passed to Matrix's constructor
			    Xa = two_dim_vec_to_arr(Xv);
			    Ya = one_dim_vec_to_arr(Yv);

			    // create X and Y matrices
			    X = new Matrix(Xa);
			    Y = new Matrix(Ya, Ya.length);
			    
			    //print X, Y, jwc 11.1
//			    X.print(1, 1);
//			    Y.print(1, 1);
				
		    	System.out.println("maximum Iteration:"+maxIteration);
			    // iteratively update beta1
			    while (max_abs((beta1.minus(beta0)).getArray()) > epsilon) {
//					 if (iter == 20)
//					     break;
			    	 if (iter == maxIteration)//added by lph
				     {
				    	 System.out.println("Has reached the maximum iteration number!");
				    	 break; 
				     }
//					URL url = new URL(root_property + "gloreserverservlet");
//					URLConnection servletConnection = url.openConnection();			
//					servletConnection.setDoInput(true);
//					servletConnection.setDoOutput(true);			
//					servletConnection.setUseCaches(false);
//					servletConnection.setDefaultUseCaches(false);				
//					servletConnection.setRequestProperty ("Content-Type", "application/x-java-serialized-object");	
					
					System.out.println("value: " + max_abs((beta1
				            .minus(beta0)).getArray()));

					System.out.println("Iteration " + iter);

					beta0 = beta1.copy();

					// P <- 1 + exp(-x%*%beta0)
					P = (X.times(-1)).times(beta0);
					exp(P.getArray());
					add_one(P.getArray());
					div_one(P.getArray());

					// w = diag(c(p*(1-p)))
					W = P.copy();
					W.timesEquals(-1.0);
					add_one(W.getArray());
					W.arrayTimesEquals(P);
					W = W.transpose();
					W = diag(W.getArray()[0]);

					// d <- t(x)%*%w%*%x
					D = ((X.transpose()).times(W)).times(X);
					// e <- t(x)%*%(y-p)
					E = (X.transpose()).times(Y.plus(P.uminus()));

	//--------------//send D E and receive beta from Server jwc 10.10------------------------------------------------------------
//					// send D and E to server
//					DataIn2 DE = new DataIn2(D, E, taskName, "beta");
//				    OutputStream out = servletConnection.getOutputStream();				
//					ObjectOutputStream oos = new ObjectOutputStream(out);
//					oos.writeObject(DE);
//					oos.flush();
//					oos.close();
////					// receive beta1 from server
//					InputStream in = servletConnection.getInputStream();
//					ObjectInputStream ois;
//					ois = new ObjectInputStream(in);
//					DataOut2 result = (DataOut2) ois.readObject();
//					ois.close();
//
//					if(result.getType().equals("beta")){
//						beta1 = result.getMatrix();
//					}
//					else{
//						System.out.println("beta type needed");
//					}
					 temp_a = E;
					 temp_b = D;
					 temp_c = diag(0.0000001, fm);

					temp_b = temp_b.plus(temp_c);
					temp_b = temp_b.inverse();
					temp_b = temp_b.times(temp_a);
					beta1 = beta0.plus(temp_b);
	//---------------------------------------------------------------------------------------------------------------------
					// print beta1 for this iteration
					beta1.print(8, 8);
					iter = iter + 1;
			    }
			    
			    // compute variance-covariance-covariance matrix
			    hat_beta = beta1.copy();

			    P = (X.times(-1)).times(hat_beta);
			    exp(P.getArray());
			    add_one(P.getArray());
			    div_one(P.getArray());

			    W = P.copy();
			    W.timesEquals(-1.0);
			    add_one(W.getArray());
			    W.arrayTimesEquals(P);
			    W = W.transpose();
			    W = diag(W.getArray()[0]);

			    D = ((X.transpose()).times(W)).times(X);
			    
				temp_b = D;	
				temp_c = diag(0.0000001, m);
	
				temp_b = temp_b.plus(temp_c);
				cov_matrix = temp_b.inverse();
				//-----------------------------------------------to calculate AUC-----------------------------------------------
			    Matrix scoreMatrix = X.times(beta1);
			    double[][] combine = new double[n][2];
			    for(i = 0; i< n; i++){
			    	combine[i][0] = (1/ (1 + Math.pow(Math.E, -1 * scoreMatrix.get(i, 0))));
			    	combine[i][1] = Y.get(i, 0);
			    }
			    Matrix aucData = new Matrix(combine);
			    Vector<double[]> aucDataCombine = new Vector<double[]>();
					for(j=0; j < aucData.getRowDimension(); j++){
						row = new double[2];
						row[0] = aucData.get(j, 0);
						row[1] = aucData.get(j, 1);
						aucDataCombine.add(row);
					}
				//sort data according to the first column: X * beta
				//Collections.sort(aucDataCombine, new ScoreComparator());
				sort(aucDataCombine);
				
				
				String BOUNDARY = "---------WebKitFormBoundaryL1WMwaoHvOv9WaJT"; // 
//				URL url = new URL("http://dbmi-engine.ucsd.edu/webcalibsis/upload3.php?taskName=" + taskName + "&email=" + email);
				URL url = new URL("http://dbmi-engine.ucsd.edu/webcalibsis/upload3.php?taskName=" + taskName + "_" + email);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				// Ã¥ï¿½â€˜Ã©â‚¬ï¿½POSTÃ¨Â¯Â·Ã¦Â±â€šÃ¥Â¿â€¦Ã©Â¡Â»Ã¨Â®Â¾Ã§Â½Â®Ã¥Â¦â€šÃ¤Â¸â€¹Ã¤Â¸Â¤Ã¨Â¡Å’
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				conn.setRequestProperty("Charsert", "UTF-8"); 
				//conn.setRequestProperty("Content-Type", "text/plain");
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
				
				OutputStream out = new DataOutputStream(conn.getOutputStream());
				byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();//
				StringBuilder sb = new StringBuilder();  
				sb.append("--");  
				sb.append(BOUNDARY);  
				sb.append("\r\n");  
				sb.append("Content-Disposition: form-data;name=\"predictionFile\";filename=\"try1\"\r\n"); 
				sb.append("Content-Type:text/plain\r\n\r\n");  
				byte[] data = sb.toString().getBytes();
				out.write(data);
				for(i=0; i< aucDataCombine.size(); i++){
					System.out.println(aucDataCombine.get(i)[0] + "\t" +  aucDataCombine.get(i)[1]);
					out.write((aucDataCombine.get(i)[0] + "\t" +  aucDataCombine.get(i)[1]).getBytes());
					out.write("\n".getBytes());
				}
				out.write("\r\n".getBytes());
				sb = new StringBuilder();  
				sb.append("--");  
				sb.append(BOUNDARY);  
				sb.append("\r\n");  
				// take care that the 'name' must meet the definition in the php file
					sb.append("Content-Disposition: form-data;name=\"modelFile\";filename=\"try2\"\r\n"); 
				sb.append("Content-Type:text/plain\r\n\r\n");  
				data = sb.toString().getBytes();
				out.write(data);
				
				for(i=0; i<vars.length -1; i++){
					out.write((vars[i] + ", ").getBytes());
				}
				out.write(vars[vars.length -1].getBytes());
				out.write("\n".getBytes());
				
				for(i=0; i<beta1.getRowDimension() -1; i++){
					out.write((beta1.get( i, 0) + ", ").getBytes());
				}
				out.write((beta1.get( (beta1.getRowDimension() -1), 0) +"").getBytes());
				out.write("\n".getBytes());
				
				for(i=0; i<cov_matrix.getRowDimension(); i++){
					for(j=0; j<cov_matrix.getColumnDimension() -1; j++){
						out.write((cov_matrix.get(i, j) + ", ").getBytes());
					}
					out.write((cov_matrix.get(i, (cov_matrix.getColumnDimension() -1)) +"").getBytes());
					out.write("\n".getBytes());
				}
				
				out.write(end_data);
				out.flush();  
				out.close();  
				
				PrintWriter pw=res.getWriter();
				//If not get InputStream, the URL will not change to the report file
				conn.getInputStream();
				// Ã¥Â®Å¡Ã¤Â¹â€°BufferedReaderÃ¨Â¾â€œÃ¥â€¦Â¥Ã¦Âµï¿½Ã¦ï¿½Â¥Ã¨Â¯Â»Ã¥ï¿½â€“URLÃ§Å¡â€žÃ¥â€œï¿½Ã¥Âºâ€�
//				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//				String line = null;
//				while ((line = reader.readLine()) != null) {
//					pw.println(line);
//				}
				pw.println(conn.getURL());
				pw.flush();
				pw.close();
			///	res.sendRedirect(conn.getURL().toString());
		}
		catch(Exception e){
			 e.printStackTrace();
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

		    /* Set each element of the 2D double array to 1 + a where a is the value of
		       an element. */
		    public static void add_one(double[][] A) {
			int i,j;
			for (i = 0; i < A.length; i++) {
			    for (j = 0; j < A[i].length; j++) {
				A[i][j] = 1 + A[i][j];
			    }
			}
		    }

		    /* Set each element of the 2D double array to 1/a where a is the value of
		       an element. */
		    public static void div_one(double[][] A) {
			int i,j;
			for (i = 0; i < A.length; i++) {
			    for (j = 0; j < A[i].length; j++) {
				A[i][j] = 1.0 / A[i][j];
			    }
			}
		    }

		    /* Given an array of length n, returns an n by n matrix M where
		       M[i][j] = A[i] if i = j and 0 otherwise. */
		    public static Matrix diag(double[] A) {
			int n = A.length;
			int i;

			Matrix M = new Matrix(n, n, 0.0);
			for (i = 0; i < n; i++) {
			    M.set(i,i,A[i]);
			}
			return M;
		    }

			/* Returns an n by n matrix where the diagonal entries are v and the
			   other entries are 0 */
			public static Matrix diag(double v, int n) {
			    int i;
			    double[][] A = new double[n][n];
			    for (i = 0; i < n; i++) {
				A[i][i] = v;
			    }
			    return new Matrix(A);
			}
		    public void sort(Vector<double[]> v){
		    	for(int i=0; i<v.size(); i++){
		    		for(int j=i; j< v.size(); j++){
		    			if(v.get(i)[0] > v.get(j)[0]){
		    				//change
		    				double temp1 = v.get(i)[0];
		    				double temp2 = v.get(i)[1];
		    				v.get(i)[0] = v.get(j)[0];
		    				v.get(i)[1] = v.get(j)[1];
		    				v.get(j)[0] = temp1;
		    				v.get(j)[1] = temp2;
		    			}
		    		}
		    	}
		    }
}
