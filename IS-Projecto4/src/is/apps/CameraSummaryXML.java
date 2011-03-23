

package is.apps;

import is.conf.LPCOJMSConf;
import is.objects.Summary;
import is.xml.CameraListReader;
import is.xml.XMLFileIO;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author msimoes
 */
public class CameraSummaryXML {

    public static final String XSD_FILE = "summary.xsd";
    public static final String NO_ARGS_ERROR = "No arguments provided. XML file paths, created by CameraSearchXML, must be passed to the application as argument.";
    public static final String XML_WRITING_ERROR = "Error writing XML to file: ";
    public static final String XML_READING_ERROR = "Error reading XML to file: ";
    public static final String XML_FORMAT_ERROR = "Corrupt XML file: ";

    //adds the xsd ref to the xml
    public static void addXSDtoRootElement(Element rootElement, String filename){
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(xsi);
        rootElement.setAttribute("noNamespaceSchemaLocation", filename, xsi);

    }

    public static void main(String []args) throws NamingException, JMSException{

        CameraListReader clr = null;
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		
        


        Document d = null;
        Element e = null;

		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context context = new InitialContext(ht);

		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(LPCOJMSConf.CONNECTION_FACTORY);

		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		queueConnection.start();

		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		Queue myOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SUMARY_OUTBOX);
		Queue myInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SUMARY_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(myInboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(myOutboxQueue);

		TextMessage msg = null;
		String xml = null;
		int id;
		String result;
		Document baseDoc = null;
		while(true){
			msg = (TextMessage) msgConsumer.receive();
			xml = msg.getText();
			id = msg.getIntProperty("id");
			System.out.println("Received ["+id+"]");
			
            try {
                Element  rootElement = new Element("summary");

				addXSDtoRootElement(rootElement, XSD_FILE);

				baseDoc = new Document(rootElement);
				d = XMLFileIO.readFromString(xml);
                clr = new CameraListReader(d);
                Summary sm = clr.parse();
                sm.createXMLElement();
                e = sm.getXMLElement();
                rootElement.addContent(e);
            } catch (JDOMException ex) {
                Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, XML_FORMAT_ERROR+xml, ex);
            } catch (IOException ex) {
                Logger.getLogger(CameraSummaryXML.class.getName()).log(Level.SEVERE, XML_READING_ERROR+xml, ex);
            }

			result = outputer.outputString(baseDoc);

			//System.out.println(result);

			msg.clearBody();
			msg.setText(result);
			msgProducer.send(msg);
			System.out.println("Sending -> "+id);
			queueSession.commit();
		}

    }

}
