/**
 *  This file is part of Email Forensics Tool.
    Email Forensics Tool. is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Email Forensics Tool. is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Email Forensics Tool.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Parses an xml file in the following format: file name: ln-ca.xml where "ln"
 * is the language.
 * <messages> holds all possible messages used in this application.
 * <class name="example"> describes the collection of messages inside that tag.
 * for example, name="services"
 * <message type="string" name="name">value</message> where name is the name
 * used to access the message. . . .
 * </class>
 * .
 * .
 * .
 * </messages>
 */
package LssReporter.Updater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * 
 * @author Kenan El-Gaouny
 * @author Elyas Syoufi
 * @date 21-12-2015
 * @version 1.0 
 */
public class Dictionary {

    private static Dictionary instance = new Dictionary();

    private Map<String, Map<String, String>> dictionary;
    private Language language = null;
    private String fileName = null;
    private Document dom;

    private Dictionary() {
        dictionary = new HashMap<String, Map<String, String>>();
    }
    /**
     * 
     * @param l
     * @return instance with the selected language
     */
    public Dictionary getInstance(Language l) {
        if (instance == null) {
            instance = new Dictionary();
        }
        setLanguage(l);
        return instance;
    }
    /**
     * 
     * @return instance of Dictionary
     */
    public static Dictionary getInstance() {
        if (instance == null) {
            instance = new Dictionary();
        }
        if (instance.language == null) {
            setLanguage(Language.EN_CA);
        } else {
            setLanguage(instance.language);
        }
        return instance;
    }
    /**
     * sets the language to the provided value
     * @param l 
     */
    public static void setLanguage(Language l) {
        instance.language = l;
        instance.fileName = l.toFileName() + ".xml";
        instance.parseXmlFile();

    }
    /**
     * method to pars xml files
     */
    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = db.parse(Dictionary.class.getResourceAsStream("/resources/lang" + fileName));

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        parseDocument();
    }
    /**
     * helper method for parsing xml files
     */
    private void parseDocument() {
        // get the root element
        Element docEle = dom.getDocumentElement();

        // get a nodelist of elements
        NodeList nl = docEle.getElementsByTagName("class");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                // get the class element
                Element el = (Element) nl.item(i);
                getMessages(el);
            }
        }

    }
    /**
     * helper method for parsing XML files
     */
    private void getMessages(Element el) {
        String className = el.getAttribute("name");
        if (!instance.dictionary.containsKey(className)) {
            instance.dictionary.put(className, new HashMap<String, String>());
        }

        NodeList nl = el.getElementsByTagName("message");
        if (nl != null && nl.getLength() > 0) {
            Element msg;
            for (int i = 0; i < nl.getLength(); i++) {
                msg = (Element) nl.item(i);
                String name = msg.getAttribute("name");
                String value = msg.getFirstChild().getNodeValue().trim();
                instance.dictionary.get(className).put(name, value);
            }
        }
    }
    /**
     * returns corresponding value from the xml file
     * @param path 
     * @return 
     */
    public static String get(String path) {
        if (!path.contains(".")) {
            return null;
        }
        int index = path.indexOf('.');

        String className = path.substring(0, index);
        String name = path.substring(index + 1);

        return instance.dictionary.get(className).get(name);
    }

    
    /**
     * 
     * @return the language currently selected
     */
    public Language getLanguage() {
        return instance.language;
    }
    /**
     * initializes the dictionary
     */
    public static void init() {
        getInstance();
    }
	public static Language currentLang() {
		return instance.language;
	}

}
