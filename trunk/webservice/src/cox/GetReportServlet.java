package cox;
//import java.sql.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
//061213

public class GetReportServlet extends HttpServlet{
	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	private String outAddress = null;
	private String createReportAddress=null;
	
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
			createReportAddress = properties.getProperty("createReportAddress");
					
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
		String taskName = req.getParameter("taskName");
		List<String> list  = new ArrayList<String>();  //瑕佷笂浼犵殑鏂囦欢鍚�濡傦細d:\haha.doc.浣犺瀹炵幇鑷繁鐨勪笟鍔°�鎴戣繖閲屽氨鏄竴涓┖list.
		File f = new File(outAddress + taskName +"_Output.txt");
		if(f.exists()){
			list.add(outAddress + taskName +"_Output.txt");
		}
		f = new File(outAddress + taskName +"_varOutput.txt");
		if(f.exists()){
			list.add(outAddress + taskName +"_varOutput.txt");
		}
		System.out.println("list length is " + list.size());
		try {
			String BOUNDARY = "---------WebKitFormBoundaryL1WMwaoHvOv9WaJT"; // 瀹氫箟鏁版嵁鍒嗛殧绾�
			//URL url = new URL("http://localhost:8080/glore/testupload");
			URL url = new URL(createReportAddress + "upload3.php?taskName=" + taskName);
			System.out.println("URL is :" + createReportAddress + "upload3.php?taskName=" + taskName);
//			URL url = new URL("http://dbmi-engine.ucsd.edu/webcalibsis/upload3.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 鍙戦�POST璇锋眰蹇呴』璁剧疆濡備笅涓よ
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
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 瀹氫箟鏈�悗鏁版嵁鍒嗛殧绾�
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
				out.write("\r\n".getBytes()); //澶氫釜鏂囦欢鏃讹紝浜屼釜鏂囦欢涔嬮棿鍔犲叆杩欎釜
				in.close();
			}
			out.write(end_data);
			out.flush();  
			out.close();  
			
			PrintWriter pw=res.getWriter();
			//If not get InputStream, the URL will not change to the report file
			conn.getInputStream();
			// 瀹氫箟BufferedReader杈撳叆娴佹潵璇诲彇URL鐨勫搷搴�
//			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				pw.println(line);
//			}
			pw.println(conn.getURL());
			pw.flush();
			pw.close();

		} catch (Exception e) {
//			System.out.println("Error in post request");
			e.printStackTrace();
		}
	}

}
