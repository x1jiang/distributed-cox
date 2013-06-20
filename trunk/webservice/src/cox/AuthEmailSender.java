package cox;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;
//import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;
//061213

public class AuthEmailSender
{
	private String from_ = null;
	private String to_ = null;
	private String subject_ = null;
	private String body_ = null;
	
	public AuthEmailSender(String from, String to, String subject, String body)
	{
		from_ = from;
		to_ = to;
		subject_ = subject;
		body_ = body;
	}

	public String send()
	{
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
 
        Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("gloreatucsd","glore@ucsd");
                }
            });
 
        try {
 
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from_));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to_));
            message.setSubject(subject_);
            message.setText(body_);
 
            Transport.send(message);
 
            System.out.println("Done");
            return "Email send success!";
		} 
		catch (AddressException e) 
		{ 
			return "InternetAddressException " + e.toString(); 
		} 
		catch (MessagingException e)
		{
			return "MessagingException " + e.toString();
		}
	}
	
	public String sendWithAttachment(String fileAttachment)
	{
		try 
		{ 
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.ucsd.edu");
//			props.put("mail.smtp.host", "mail.163.com");
			InternetAddress fromAddress = new InternetAddress(from_);
			InternetAddress toAddress = new InternetAddress(to_);
			Session s = Session.getInstance(props, null);

			MimeMessage msg = new MimeMessage(s);
			msg.setFrom(fromAddress);
			msg.addRecipient(Message.RecipientType.TO, toAddress);
			
			msg.setSubject(subject_); 
			// body part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			
			messageBodyPart.setText(body_);
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileAttachment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			File attachment = new File(fileAttachment);
			messageBodyPart.setFileName(attachment.getName());
			multipart.addBodyPart(messageBodyPart);
			
			msg.setContent(multipart);
			Transport.send(msg); 
			
			return "Email sent to " + to_ + "successful."; 
		} 
		catch (AddressException e) 
		{ 
			return "InternetAddressException " + e.toString(); 
		} 
		catch (MessagingException e)
		{
			return "MessagingException " + e.toString();
		}
	}
}