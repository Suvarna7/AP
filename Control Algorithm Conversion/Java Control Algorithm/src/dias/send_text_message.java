/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author User
 */
public class send_text_message {
    
    
    	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
        private static String address;
        private static String text;
        private static String address2;
        private static String subject;
    /**
     *
     * @throws javax.mail.internet.AddressException
     * @throws MessagingException
     */
        public send_text_message(String address,String address2,String subject ,String message){
            this.address=address;
            this.address2=address2;
            this.text=message;
            this.subject=subject ;
        }
        
        
    public void generateAndSendEmail() throws AddressException, MessagingException {
 
		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out.println("Mail Server Properties have been setup successfully..");
 
		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
		generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(address2));
		generateMailMessage.setSubject(subject);
		String emailBody = text;
		generateMailMessage.setContent(emailBody, "text/html");
		//System.out.println("Mail Session has been created successfully..");
 
		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");
 
		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		transport.connect("smtp.gmail.com", DIAS.privateMails[0], "18071991");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
    
}
