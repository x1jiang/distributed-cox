package cox;
import java.sql.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
//061213


/**
 * This class is similar to ClientServlet.java in ppsvm
 * This servlet receive username and passwd as imput, compare with items in table registeduser, return DataOut as response.
 * @author Wenchao
 *
 */
public class GetCollaborator extends HttpServlet{

	private String dbconnection_property = null;
	private String dbusername_property = null;
	private String dbpassword_property = null;
	private String root_property = null;
	/**
	 * read parameters from file config.properties to initialize db: 
	 * dbconnection;
	 * dbusername;
	 * dbpassword;
	 */
	public void init(ServletConfig conf) throws ServletException {
		Properties properties = new Properties();
		try{
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties");
			properties.load(is);
			properties.list(System.out);
			dbconnection_property = properties.getProperty("dbconnection");
			dbusername_property = properties.getProperty("dbusername");
			dbpassword_property = properties.getProperty("dbpassword");
			root_property = properties.getProperty("root");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** Compare .
	 * @param
	 * req DataIn as input class, which includes name, password
	 * @param
	 * res DataOut as output class, which is the state of  
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		Vector<Collaborator> collaboratorList = new Vector<Collaborator>();
		String sql = "" ;
		String err = "";
		String jsonStr = "";
		String currentUserEmail = (String)req.getSession().getAttribute("email");
		System.out.println("in the get collaborator servlet");
		if(currentUserEmail.equals("anonymous")){
			
		}
		else{
			try{
				Class.forName("com.mysql.jdbc.Driver");					
				Connection conn = DriverManager.getConnection(dbconnection_property, dbusername_property, dbpassword_property);
				Statement stat = conn.createStatement();
				String prevEmail = "";	//previous email
				String presEmail;		//present email
				
				/*get collaborator's email, task_id and task name*/
				sql = "select A.email, A.task_id, C.name from user A, user B, gtask C where A.task_id=B.task_id and A.task_id=C.id and " +
						"A.email!=B.email and B.email='" + currentUserEmail + "' order by A.email;";
				ResultSet rs = stat.executeQuery(sql);
				while(rs.next()){
					presEmail = rs.getString("email");
					if(presEmail.equals(prevEmail)){
						collaboratorList.get(collaboratorList.size() - 1).addTask(rs.getString("name"));
					}
					else{
						collaboratorList.add(new Collaborator(rs.getString("email"), rs.getString("name")));
						prevEmail = presEmail;
					}
				}
				if(prevEmail.equals("")){
					throw new NoCollaboratorException();
				}
				
				/*for each email, get name in registed user table. If not regist, remain "Unknown"*/
				for(int i=0; i<collaboratorList.size(); i++){
					String email = collaboratorList.get(i).getEmail();
					sql = "select name from registeduser where email='" + email + "';";
					rs = stat.executeQuery(sql);
					if(rs.next()){
						//set collaborator's name
						String[] ss = rs.getString("name").split("#");
						collaboratorList.get(i).setName(ss[0] + " " + ss[1]);
					}
				}
				
				//close rs and connection
				if(stat!=null){
					stat.close();
				}
				if(rs!=null){
					rs.close();
				}
				if(conn!=null){
					conn.close();
				}
				//change task number to percentage
				double sum = 0;
				for(int i=0; i<collaboratorList.size(); i++){
					sum += collaboratorList.get(i).getTaskNumber();
				}
				//set percentage
				for(int i=0; i<collaboratorList.size(); i++){
					collaboratorList.get(i).percent = collaboratorList.get(i).getTaskNumber()/sum;
				}
				//sort collaborators by percentage
				for(int i=0; i<collaboratorList.size(); i++){
					for(int j=i; j<collaboratorList.size(); j++){
						if(collaboratorList.get(i).percent < collaboratorList.get(j).percent){
//							Collaborator tmp = new Collaborator(collaboratorList.get(i));
							Collaborator tmp = collaboratorList.get(i);
							collaboratorList.set(i, collaboratorList.get(j));
							collaboratorList.set(j, tmp);
						}
					}
				}
				
				//construct json series
				jsonStr = "[";
				for(int i=0; i< collaboratorList.size(); i++){
					jsonStr += "{\"name\":\"" + collaboratorList.get(i).getName() + "\", \"email\":\"" +
					collaboratorList.get(i).getEmail() + "\", \"taskNumber\":" + collaboratorList.get(i).getTaskNumber() 
					+ ", \"percent\":" + collaboratorList.get(i).percent + ", \"taskNameList\":[";
					for(int j=0; j<collaboratorList.get(i).taskList.size(); j++){
						jsonStr += "\"" + collaboratorList.get(i).taskList.get(j) + "\", ";
					}
					jsonStr = jsonStr.substring(0, jsonStr.length() - 2) ;
					jsonStr += "]}, ";
				}
				jsonStr = jsonStr.substring(0, jsonStr.length() - 2) ;
				jsonStr += "]" ;

			}catch(SQLException e){
				e.printStackTrace();
				err = e.getMessage();
//				req.getSession().setAttribute("error", err);
			}catch(ClassNotFoundException e){
				err = e.getMessage();
//				req.getSession().setAttribute("error", err);
			}catch(NoCollaboratorException e){
				err += "No collaborator";
			}finally{
				//return information in json form
				res.setContentType("text/html");
				res.getWriter().write(jsonStr);
			}
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		doGet(req, res);
	}

}

class Collaborator{
	String name;
	String email;
	int taskNumber;
	public Vector<String> taskList;
	public double percent;
	public Collaborator(){
		this.name = "Unknown";
		this.email = "";
		this.taskList = new Vector<String>();
		this.taskNumber = 0;
		this.percent = 0;
	}
	public Collaborator(String email, String taskName){
		this.name = "Unknown";
		this.email = email;
		this.taskList = new Vector<String>();
		this.taskNumber = 0;
		this.percent = 0;
		this.taskList.add(taskName);
		this.taskNumber ++;
	}
	public Collaborator(Collaborator C){
		this.name = C.name;
		this.email = C.email;
		this.taskNumber = C.taskNumber;
		this.percent = C.percent;
		this.taskList = new Vector<String>();
		for(int i = 0; i<C.taskList.size(); i++){
			this.taskList.add(C.taskList.get(i));
		}
	}
	public void addTask(String taskName){
		this.taskList.add(taskName);
		this.taskNumber ++;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getEmail(){
		return this.email;
	}
	public String getName(){
		return this.name;
	}
	public int getTaskNumber(){
		return this.taskNumber;
	}
	
}
