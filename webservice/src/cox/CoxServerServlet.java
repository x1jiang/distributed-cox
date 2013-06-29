package cox;
//061213
import java.sql.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

import javax.servlet.*;
import javax.servlet.http.*;
import Jama.Matrix;

public class CoxServerServlet extends HttpServlet{

	/**
	 * @param args
	 */
	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	private String outAddress = null;
	private String createReportAddress=null;
	private Properties confProperties;
	
    // socket connection and thread id
    private int m_thread_id;
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
	static Semaphore data_lock;
	static Semaphore beta_lock;
	static Semaphore Fn_lock;
	static Semaphore Fn_comp_lock;
    // number of features in the data
    static int m, n;
    double epsilon;
	static Matrix beta0, beta1, BaseSurFn;
    // count the number of iterations
    static int iter;
	static int maxIteration;
    static Vector<ServerThread> taskList = null;


	public void init(ServletConfig conf) throws ServletException {
		try
		{
			confProperties = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
			confProperties.load(is);
			
			dbconnection_property = confProperties.getProperty("dbconnection");
			dbusername_property = confProperties.getProperty("dbusername");
			dbpassword_property = confProperties.getProperty("dbpassword");
			root_property = confProperties.getProperty("root");
			outAddress = confProperties.getProperty("outAddress");
			createReportAddress = confProperties.getProperty("createReportAddress");
			
			
			taskList = new Vector<ServerThread>();
		}
		catch(IOException e)
		{
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			res.setContentType("application/x-java-serialized-object");
			InputStream in = req.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(in);
			DataIn2 dataIn = (DataIn2) ois.readObject();
			ois.close();
			System.out.println("receive dataIn "+ dataIn.getTaskName() + " type: " + dataIn.getType());
			if(dataIn.getD()!=null){
				System.out.println("theta: ");
//				dataIn.getD().print(1, 1);				
			}
			if(dataIn.getE()!=null){
				System.out.println("thetaZ: ");
//				dataIn.getE().print(1, 1);
			}
			if(dataIn.getF()!=null){
				System.out.println("thetaZZ: ");
//				dataIn.getE().print(1, 1);
			}
			if(dataIn.getG()!=null){
				System.out.println("sumZ: ");
//				dataIn.getE().print(1, 1);
			}
			if(dataIn.getH()!=null){
				System.out.println("DI: ");
//				dataIn.getH().print(1, 1);
			}

			
			boolean taskExist = false;
			
			//check task name and type (beta or cov) and add it into thread list
			for(int i =0; i< taskList.size(); i++){
				if(taskList.get(i).getTaskName().equals(dataIn.getTaskName())){
					//delete task that is all finished we just check this situation now jwc 10.18
//					if(taskList.get(i).isBetaFinish() & taskList.get(i).isCovFinish() & taskList.get(i).isAUCFinish()){
//						taskList.remove(i);
//						continue;
//					}
					System.out.println("The task "+taskList.get(i).getTaskName() + " already exists");
					System.out.println("dataIn type is " + dataIn.getType());
					if(dataIn.getType().equals("beta")){
						taskList.get(i).addBetaData(dataIn, res);
					}
					else if(dataIn.getType().equals("sur")){
						taskList.get(i).addSurvival(dataIn, res);
					}
					else if(dataIn.getType().equals("fn")){
						taskList.get(i).addFn(dataIn, res);
					}
					else if(dataIn.getType().equals("end")){
//						res.getOutputStream();
						//change the status in sql and delete from task list by jwc 11.13
						Class.forName("com.mysql.jdbc.Driver");	
					    Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
					    Statement stat = conn.createStatement();
					    String sql = "update gtask set taskstatus=2 where gtask.name='" + dataIn.getTaskName() + "';";
					    System.out.println("here to end the task");
					    stat.execute(sql);
					    
					    
					    //createReport(dataIn.getTaskName()); //challen
//					    res.sendRedirect("/getreportservlet?taskName=" + dataIn.getTaskName());
			    		OutputStream out = res.getOutputStream();
			    		ObjectOutputStream oos = new ObjectOutputStream(out);
			    	    oos.writeObject(new DataOut2(null, "end", dataIn.getTaskName()));
			    	    oos.flush();
			    	    oos.close();
			    	    System.out.println("end message finish");
//					    taskList.remove(i);
					}
					taskExist = true;
					break;
				}
			}
			if(!taskExist){
				//do not care "end" message jwc 11.13
				if(dataIn.getType().equals("end")){
					System.out.println("Task not Exist, but receive end message");
//					return;
				}
				//get the number of clients
				Class.forName("com.mysql.jdbc.Driver");	
			    Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
			    Statement stat = conn.createStatement();
			    String sql = "select NumClient, parameters from gtask where name='" + dataIn.getTaskName() + "';";
				 ResultSet rs = stat.executeQuery(sql);
				 rs.next();
				 int numClient = rs.getInt(1);
				 String param= rs.getString(2);
				 String[] paramArray = param.split("#");//added by lph
				 int maxIteration=Integer.parseInt(paramArray[1]);
				 double epsilon= Double.parseDouble(paramArray[3]);
				 
				//find the property of the task
				sql = "select property from gtask where name='" + dataIn.getTaskName() + "'";
				rs = stat.executeQuery(sql);
				rs.next();
				String[] vars = rs.getString(1).split("\t");
				  
				int featureNum = vars.length;
				ServerThread newTask = new ServerThread(dataIn, numClient, maxIteration, epsilon, featureNum, confProperties);
				taskList.add(newTask);
				System.out.println("The task "+dataIn.getTaskName() + " added");
				if(dataIn.getType().equals("beta")){
					newTask.addBetaData(dataIn, res);
				}
				else if(dataIn.getType().equals("sur")){
					newTask.addSurvival(dataIn, res);
				}
				else if(dataIn.getType().equals("fn")){
					newTask.addFn(dataIn, res);
				}
//				//find the property of the task
//				sql = "select property from gtask where name='" + dataIn.getTaskName() + "'";
//				rs = stat.executeQuery(sql);
//				rs.next();
//				String[] vars = rs.getString(1).split("\t");
				
				//close sql
				if(rs!=null){
					rs.close();
				}
				if(stat!=null){
					stat.close();
				}
				if(conn!=null){
					conn.close();
				}
				
				//Build a new file and write the properties to the file
				File varOutput = new File(outAddress + dataIn.getTaskName() + "_varOutput.txt");
				System.out.println("The addresss of cox server servlet is : " + (new File("")).getAbsolutePath());
				FileWriter fw = new FileWriter(varOutput);
				fw.write("\r\n The property of input file: \r\n");
				for(int i=0; i<vars.length -1; i++){
					fw.write(vars[i] + "\t");
				}
				fw.write(vars[vars.length -1]);
				fw.write("\r\n");
				fw.close();
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}			
	
	}
/*	
	private void createReport(String taskName) {
		// TODO Auto-generated method stub
		List<String> list  = new ArrayList<String>();  //要上传的文件名,如：d:\haha.doc.你要实现自己的业务。我这里就是一个空list.
		File f = new File(outAddress + taskName +"_Output.txt");
		if(f.exists()){
			list.add(outAddress + taskName +"_Output.txt");
		}
		f = new File(outAddress + taskName +"_varOutput.txt");
		if(f.exists()){
			list.add(outAddress + taskName +"_varOutput.txt");
		}
		System.out.println("list length is " + list.size() + " in ServerServlet");
		try {
			String BOUNDARY = "---------WebKitFormBoundaryL1WMwaoHvOv9WaJT"; // 定义数据分隔线
			//URL url = new URL("http://localhost:8080/glore/testupload"); 
			URL url = new URL(createReportAddress); //challen
			//URL url = new URL(createReportAddress + "upload3.php?taskName=" + taskName); //challen
//			URL url = new URL("http://dbmi-engine.ucsd.edu/webcalibsis/upload3.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
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
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			int leng = list.size();
			for(int i=0;i<leng;i++){
				String fname = list.get(i);
				File file = new File(fname);
				StringBuilder sb = new StringBuilder();  
				sb.append("--");  
				sb.append(BOUNDARY);  
				sb.append("\r\n");  
				// take care that the 'name' must meet the definition in the php file
				if(i == 0){
					sb.append("Content-Disposition: form-data;name=\"predictionFile\";filename=\""+ file.getName() + "\"\r\n"); 
				}
				else if(i==1){
					sb.append("Content-Disposition: form-data;name=\"modelFile\";filename=\""+ file.getName() + "\"\r\n"); 
				}
				System.out.println("filename: " + file.getName());
				sb.append("Content-Type:text/plain\r\n\r\n");  
				
				byte[] data = sb.toString().getBytes();
				out.write(data);
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				out.write("\r\n".getBytes()); //多个文件时，二个文件之间加入这个
				in.close();
			}
			out.write(end_data);
			out.flush();  
			out.close();  
			
//			PrintWriter pw=res.getWriter();
//			//If not get InputStream, the URL will not change to the report file
			conn.getInputStream();
			// 定义BufferedReader输入流来读取URL的响应
//			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		System.out.println("The address of cox server is:" + new File("").getAbsolutePath());
		doPost(req, res);
	}
	

}
