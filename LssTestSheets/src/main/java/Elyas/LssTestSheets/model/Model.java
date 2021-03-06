package Elyas.LssTestSheets.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.Templates;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.json.JSONObject;

import Elyas.LssTestSheets.factory.CourseFactory;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class Model {
	private static Model instance;

	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	private Model() {
		changes = new HashSet<>();
	}

	private Course course;
	private boolean isChanged;
	private boolean externalCourse;
	private Set<String> changes;
	public void setCourse(Course c) {
		this.course = c;
		c.setOnChange((String change) -> {
			instance.setChanged(change);
		});
	}

	public Course getCourse() {
		return this.course;
	}

	/**
	 * saves the course into a JSON file named "json/courses/courseName" in the
	 * resources directory
	 */
	public boolean save() {
		String course_path = course.getFilePath();

		File file;
		if (course_path == null) {
			URL url = this.getClass().getResource("/json/");
			File f;

			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}
			String simple_path = f.getAbsolutePath();

			simple_path += "courses/";
			file = new File(simple_path);
			file.mkdirs();

			simple_path += course.getName();

			file = new File(simple_path);

			int i = 0;
			while (file.exists()) {
				String path = new String(simple_path);

				path += "-";

				path += i;
				file = new File(path);
				i++;
			}
			try {
				if (file.createNewFile()) {
					course.setFilePath(file.getAbsolutePath()
							.substring(file.getAbsolutePath().lastIndexOf(System.getProperty("file.separator")) + 1));
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {

			File f;
			if (!externalCourse) {
				URL url = this.getClass().getResource("/jsoncourses/");
				try {
					f = new File(url.toURI());
				} catch (URISyntaxException e) {
					f = new File(url.getPath());
				}
			} else {

				f = new File(course_path);

			}

			String path = f.getAbsolutePath() + System.getProperty("file.separator") + course_path;
			file = new File(path);
		}

		try {

			FileWriter writer = new FileWriter(file, false);
			writer.write(course.toJSON().toString(4));
			writer.flush();
			writer.close();
			isChanged = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean isChanged() {
		return isChanged;
	}

	public interface ChangeHandler {
		public void onChange(String change);
	}

	public void addClient(Client c) {
		course.addClient(c);
	}

	public List<Client> getClients() {
		return course.getClients();
	}

	public void removeClient(Client c) {
		course.removeClient(c);
	}

	public void updateClient(Client c) {
		course.updateClient(c);
	}

	public void setFacility(Facility f) {
		course.setFacility(f);
	}

	public void setBarcodeOne(String text) {
		course.setBarcodeOne(text);
	}

	public void setBarcodeTwo(String text) {
		course.setBarcodeTwo(text);

	}

	public void setExam(Exam exam) {
		for (Qualification qualification : course.getQualifications()) {
			qualification.setExam(exam);
		}

	}

	public void setExam(Qualification qualification, Exam exam) {
		for (Qualification q : course.getQualifications()) {
			if (q.equals(qualification)) {
				q.setExam(exam);
			}
		}
	}

	public String getCourseName() {
		return course.getName();
	}

	public void setQualifications(Collection<Qualification> c) {
		if (course == null) {
			course = new Course(c);
			course.setOnChange((String change) -> {
				isChanged = true;
			});
		} else
			course.setQualifications(c);

	}

	public Collection<String> getQualificationNames() {
		List<String> names = new ArrayList<>();
		for (Qualification qualification : course.getQualifications()) {
			names.add(qualification.getName());
		}
		return names;
	}

	public Report getReport() {
		return course.getReport();
	}

	public List<Qualification> getQualifications() {
		return course.getQualifications();
	}

	public Facility getFacility() {
		return course.getFacility();
	}

	public String getBarcodeOne() {
		return course.getBarcode1();
	}

	public String getBarcodeTwo() {
		return course.getBarcode2();
	}

	public List<Employee> getInstructors(String qual) {
		return course.getInstructors(qual);
	}

	public List<Employee> getExaminers(String qual) {

		return course.getExaminers(qual);
	}

	/**
	 * sends the course information to the given email address.
	 * 
	 * @param sendCourse
	 *            whether or not to send the course file
	 * @param sendTestSheet
	 *            whether or not to send the generated test sheets
	 * @param name
	 *            of the client using the application
	 * @param email
	 *            of the receiver, receiving the message.
	 */
	public void sendInfo(boolean sendCourse, boolean sendTestSheet, Properties properties, String name, String email,
			ThreadCompleteListener onFinish) throws AddressException, MessagingException, UnsupportedEncodingException {
		
		
		NotifyingThread thread = new NotifyingThread() {

			@Override
			public void doRun() {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp-mail.outlook.com");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

				Authenticator auth = new SMTPAuthenticator();
				Session session = Session.getDefaultInstance(props, auth);

				MimeMessage msg = new MimeMessage(session);
				try {

					InputStream is = Model.class.getResourceAsStream("/html/email.html");
					String template;

					template = IOUtils.toString(is);

					msg.setFrom(new InternetAddress("no-reply-reporter@outlook.com", name));
					msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
					msg.setSubject("Advanced Course From " + name);
					msg.setSentDate(new Date());
					template = template.replace("%name%", name);
					String content = "They sent you ";
					if (sendCourse && sendTestSheet) {
						content += "a course file as well as test sheets.";
					} else if (sendCourse) {
						content += "a course file.";
					} else {
						content += "the test sheets.";
					}
					template = template.replace("%content%", content);
					msg.setText(template, "utf-8", "html");

					MimeBodyPart body = new MimeBodyPart();
					body.setText(template, "utf-8", "html");

					MimeMultipart multipart = new MimeMultipart();
					multipart.addBodyPart(body);

					if (sendCourse) {
						MimeBodyPart attachmentPart = new MimeBodyPart();
						DataSource source = new ByteArrayDataSource(course.toJSON().toString(4).getBytes("UTF-8"),
								"application/octet-stream");
						attachmentPart.setDataHandler(new DataHandler(source));
						attachmentPart.setFileName("course");
						multipart.addBodyPart(attachmentPart);
					}
					if (sendTestSheet) {
						List<PDDocument> documents = CourseFactory.generateTestSheets(course, properties);
						int i = 1;
						PDStream pdStream;
						for (PDDocument document : documents) {
							
							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							document.save(byteArrayOutputStream);
							document.close();
							InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
							
							MimeBodyPart attachmentPart = new MimeBodyPart();
							DataSource source = new ByteArrayDataSource(inputStream,
									"application/pdf");
							
							attachmentPart.setDataHandler(new DataHandler(source));
							if(documents.size() > 1){
								attachmentPart.setFileName("test sheet " + (i++));
							}else{
								attachmentPart.setFileName("test sheet");
							}
							multipart.addBodyPart(attachmentPart);
						}
					}
					msg.setContent(multipart);

					msg.saveChanges();
					Transport.send(msg);

				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
		};
		thread.addListener(onFinish);
		thread.start();

	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			InputStream is = Model.class.getResourceAsStream("/json/secret.json");
			if(is == null){
				return null;
			}
			String jsonTxt;
			try {
				jsonTxt = IOUtils.toString(is);
				JSONObject obj = new JSONObject(jsonTxt);
				return new PasswordAuthentication(obj.optString("user"), obj.optString("password"));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}	
		}
	}

	public void setExternalCourse(boolean b) {
		externalCourse = b;

	}

	public void setChanged(String change) {
		changes.add(change+"\n");
		this.isChanged = true;
	}

	public ArrayList<Client> getClientsAsCopy() {
		return new ArrayList<>(course.getClients());
	}

	public void setChanged(boolean b, String change) {
		if(change != null){
			changes.add(change+"\n");
		}
		
		if(!b){
			changes.clear();
		}
		this.isChanged = b;
	}

	public String getChangesAsString() {
		String str = "";
		for(String change : changes){
			str += change;
		}
		return str;
	}
}
