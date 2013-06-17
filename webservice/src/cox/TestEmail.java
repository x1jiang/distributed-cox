package cox;

public class TestEmail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String to = "jwch46@163.com";
		String subject = "glore infomation";
		String text = "invitation";
		AuthEmailSender es = new AuthEmailSender("jiangwenchao88@gmail.com", to, subject, text);
		System.out.println(es.send()); 
	}

}
