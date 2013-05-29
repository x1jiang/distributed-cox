package cox;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
public class TestUpload extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	           throws ServletException, IOException {
	       //获取输入流，是HTTP协议中的实体内容
	       ServletInputStream  in=request.getInputStream();
	       //缓冲区
	       byte buffer[]=new byte[1024];
	       FileOutputStream out=new FileOutputStream("c:\\test.log");
	       int len=in.read(buffer, 0, 1024);
	       //把流里的信息循环读入到file.log文件中
	       while( len!=-1 ){
	           out.write(buffer, 0, len);
	           len=in.readLine(buffer, 0, 1024);

	       }
	       out.close();
	       in.close();
	    }
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}
