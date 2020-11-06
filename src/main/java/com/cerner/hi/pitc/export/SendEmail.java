package com.cerner.hi.pitc.export;


import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.cerner.hi.pitc.util.Constants;

public class SendEmail {
	

	public void sendEmail(String from,String to,String attachment)throws Exception
	{
		
		String attachmentPath = "results/"+attachment;

		Properties properties = new Properties();

		properties.put("mail.smtp.host", Constants.HOST);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", Constants.HOST);
		properties.put("mail.smtp.auth", "false");
		
		Session session = Session.getInstance(properties);
		
		try {

			//constructing email
			
		    MimeMessage message = new MimeMessage(session);

		    BodyPart messageBodyPart = new MimeBodyPart();
	    	message.setFrom(new InternetAddress(from));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    message.setSubject("JIRA-Process-IT-Compliance-Tool-Service Report");
		    
		    Multipart multipart = new MimeMultipart();            

		    messageBodyPart = new MimeBodyPart();
		    DataSource source = new FileDataSource(attachmentPath);
		    messageBodyPart.setDataHandler(new DataHandler(source));
		    messageBodyPart.setFileName(attachment);
		    multipart.addBodyPart(messageBodyPart);

		    message.setContent(multipart);
		    Transport.send(message);
		    
		    System.out.println("Email is Sent Successfully");
		    
		}catch (AddressException e) {
            throw new AddressException("Incorrect email address");

        }
		catch (MessagingException e) {
        	throw new MessagingException("File Not Found");

        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }   
		 
	}

}