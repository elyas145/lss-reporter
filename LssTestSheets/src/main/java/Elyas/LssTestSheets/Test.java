package Elyas.LssTestSheets;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Test {
	public static void main(String args[]) {
		Properties props = new Properties();
		// props.put("mail.smtp.user", "no-reply-reporter@outlook.com");
		props.put("mail.smtp.host", "smtp-mail.outlook.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");

		// props.put("mail.smtp.debug", "true");
		// props.put("mail.smtp.socketFactory.port", "587");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// props.put("mail.smtp.socketFactory.fallback", "false");

		// SecurityManager security = System.getSecurityManager();
		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(props, auth);

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("no-reply-reporter@outlook.com", "Elyas Syoufi"));
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("elyas.syoufi@gmail.com"));
			msg.setSubject("test email");
			msg.setSentDate(new Date());


			/*
			 * BodyPart body = new MimeBodyPart(); body.setContent("hello!",
			 * "text/html");
			 * 
			 * Multipart multipart = new MimeMultipart();
			 * multipart.addBodyPart(body); msg.setContent(multipart);
			 */
			msg.setText("helloooooo");
			msg.saveChanges();
			Transport.send(msg);
			System.out.println("done.");
		} catch (Exception mex) {
			mex.printStackTrace();
		}
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication("no-reply-reporter@outlook.com", "WalterBaker2016");
		}
	}
}
