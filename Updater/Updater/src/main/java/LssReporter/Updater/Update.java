package LssReporter.Updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Update {
	private Message message;
	private String subject;
	private int updateNumber;
	private String description_en;
	private String description_fr;
	private List<File> attachments;
	private List<UpdateInstruction> instructions;

	public static enum Type {
		ADD, DELETE, REPLACE
	};

	/**
	 * this update is dependent on the message passed in. the properties should
	 * not be set outside this class.
	 * 
	 * @param message
	 *            the message with the update instructions.
	 * @throws MessagingException
	 *             if unable to read the message subject
	 * @throws NumberFormatException
	 *             if the message subject is in the wrong format.
	 */
	public Update(Message message) throws MessagingException, NumberFormatException {
		this.message = message;
		this.subject = message.getSubject();
		this.updateNumber = Integer.valueOf(subject.trim().substring(subject.indexOf(' '), subject.length()).trim());

	}

	public String getSubject() {
		return subject;
	}

	public int getUpdateNumber() {
		return updateNumber;
	}

	public String getDescription(Language lang) {
		switch (lang) {
		case EN_CA:
			return description_en;
		case FR_CA:
			if (description_fr.equals("")) {
				return description_en;
			}
			return description_fr;
		default:
			return description_en;
		}
	}

	/**
	 * applies this update. initUpdate() must to be called first to download
	 * necessary data from message.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void applyUpdate() throws IOException, URISyntaxException {
		if (instructions == null) {
			throw new NullPointerException("Update not initialized properly. instructions missing.");
		}
		for (UpdateInstruction instruction : instructions) {
			switch (instruction.type) {
			case ADD:
				addFile(instruction.path, instruction.fileName);
				break;
			case DELETE:
				deleteFile(instruction.path, instruction.fileName);
				break;
			case REPLACE:
				addFile(instruction.path, instruction.fileName);
			default:
				break;
			}
		}
		// delete temp files.
		for (File file : attachments) {
			file.delete();
		}
	}

	private void deleteFile(String path, String fileName) throws FileNotFoundException, URISyntaxException {
		File file = new File(Update.class.getResource("/").toURI().getSchemeSpecificPart() + path + fileName);
		if (file.exists()) {
			file.delete();
		} else {
			throw new FileNotFoundException("the file \"" + fileName + "\" could not be found.");
		}

	}

	private void addFile(String path, String fileName) throws IOException, URISyntaxException {
		File file = new File(Update.class.getResource("/").toURI().getSchemeSpecificPart() + path + fileName);
		System.out.println("file: " + file.getAbsolutePath());
		file.getParentFile().mkdirs();
		for (File file2 : attachments) {
			if (file2.getName().trim().toLowerCase().equals(fileName.trim().toLowerCase())) {
				Files.copy(file2.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

			}
		}
	}

	/**
	 * initializes this object from the message. only call this method if the
	 * update is going to happen.calling this method on all potential updates
	 * will waste a lot of time.
	 * 
	 * @throws MessagingException
	 *             if the message cannot be read.
	 * @throws IOException
	 *             if attachments cannot be downloaded.
	 * @throws URISyntaxException
	 */

	public void initUpdate() throws MessagingException, IOException, URISyntaxException {
		String inst = getText(message);
		if (textIsHtml) {
			System.out.println("text is html :(");
		}
		attachments = getAttachments(message);
		parseInstructions(inst);
	}

	private void parseInstructions(String inst) {
		instructions = new ArrayList<Update.UpdateInstruction>();
		JSONObject object = new JSONObject(inst);
		JSONArray array = object.getJSONArray("instructions");
		description_en = object.optString("description-en");
		description_fr = object.optString("description-fr");
		if (description_en.equals("") && description_fr.equals("")) {
			description_en = object.optString("description");
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject instruction = array.getJSONObject(i);
			instructions.add(new UpdateInstruction(instruction));
		}
	}

	private List<File> getAttachments(Message message) throws IOException, MessagingException, URISyntaxException {
		List<File> attachments = new ArrayList<File>();
		Multipart multipart = (Multipart) message.getContent();
		// System.out.println(multipart.getCount());

		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
					&& !StringUtils.isNotBlank(bodyPart.getFileName())) {
				continue; // dealing with attachments only
			}
			InputStream is = bodyPart.getInputStream();
			File f = new File(
					Update.class.getResource("/").toURI().getSchemeSpecificPart() + "/tmp/" + bodyPart.getFileName());
			System.out.println("file: " + f.getAbsolutePath());
			f.getParentFile().mkdirs();
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			byte[] buf = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buf)) != -1) {
				fos.write(buf, 0, bytesRead);
			}
			fos.close();

			attachments.add(f);
		}
		return attachments;
	}

	private boolean textIsHtml = false;

	/**
	 * Return the primary text content of the message.
	 */
	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			textIsHtml = p.isMimeType("text/html");
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null) {
						text = getText(bp);
						return text;
					}

				} else if (bp.isMimeType("text/html")) {
					continue;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}

	private class UpdateInstruction {
		public UpdateInstruction(JSONObject instruction) {
			String type = instruction.getString("operation");
			switch (type.trim().toLowerCase()) {
			case "add":
				this.type = Type.ADD;
				break;
			case "delete":
				this.type = Type.DELETE;
				break;
			case "replace":
				this.type = Type.REPLACE;
				break;
			default:
				break;
			}

			this.path = instruction.getString("path");
			this.fileName = instruction.getString("file");
		}

		protected Type type;
		protected String path;
		protected String fileName;

	}
}
