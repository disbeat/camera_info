package is.apps;


import is.conf.LPCOJMSConf;
import is.parsing.CameraListParser;
import is.objects.Camera;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;



/**
 *
 * @author lopes and msimoes
 */
public class CameraSearchXML {

    public static final String LINK = "http://www.dpreview.com/reviews/specs/";
    public static final String XSD_FILE = "camera_list.xsd";
    public static final String XSL_FILE = "CameraListBeautifier.xsl";
    public static final String NO_ARGS_ERROR = "No arguments provided. Cameras' brand name must be passed to the application as argument.";
    public static final String XML_WRITING_ERROR = "Error writing XML to file: ";
    public static final String URL_ERROR = "Error getting url. Check your Internet connection and try again.";


    //adds the xsl ref to the xml doc
    public static void addXSLtoDoc(Document doc, String fileName) {
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("type", "text/xsl");
        params.put("href", fileName);
        ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", params);
        doc.getContent().add(0, pi);
    }

    //adds the xsl ref to the xml doc
    public static void addXSDtoRootElement(Element rootElement, String filename){
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rootElement.addNamespaceDeclaration(xsi);
        rootElement.setAttribute("noNamespaceSchemaLocation", filename, xsi);

    }

    public static void main (String args[]) throws NamingException, JMSException{

		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());

		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context context = new InitialContext(ht);

		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(LPCOJMSConf.CONNECTION_FACTORY);

		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		queueConnection.start();

		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		Queue myOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SEARCH_OUTBOX);
		Queue myInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SEARCH_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(myInboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(myOutboxQueue);

		TextMessage msg = null, msg2 = null;
		String brand = null;
		int id;
		String result;
		ArrayList <Camera> list = new ArrayList<Camera>();
		while(true){
			msg = (TextMessage) msgConsumer.receive();
			brand = msg.getText();
			id = msg.getIntProperty("id");
			System.out.println("Received ["+id+"] -> "+brand);

          try{
              //Create instance of the parser
              CameraListParser clp = new CameraListParser(LINK + brand + "/");

              //Do the parsing
              clp.parse();

              //Get the result
			  list.clear();
              list = clp.getCameraList();

              Element  rootElement = new Element("cameras");
              addXSDtoRootElement(rootElement, XSD_FILE);

              //create root element of XML file
              rootElement.setAttribute("brand", brand);

              Document baseDoc = new Document(rootElement);
              addXSLtoDoc(baseDoc, XSL_FILE);



              for (Camera c: list){
                  rootElement.addContent(c.getXMLElement());
              }

			  result = outputer.outputString(baseDoc);

			  //System.out.println("XML = "+result);

			  msg.clearBody();
			  msg.setText(result);
			  msgProducer.send(msg);
			  System.out.println("Sending -> "+id);
			  queueSession.commit();
			  
/*              String path = "xml"+File.separator+brand+".xml";

              try{
                XMLFileIO.writeToFile(baseDoc, path);
              }catch(IOException ex){
                  Logger.getLogger(CameraSearchXML.class.getName()).log(Level.SEVERE, XML_WRITING_ERROR+path, ex);
              }
*/
          }catch(IOException ex){
              Logger.getLogger(CameraSearchXML.class.getName()).log(Level.SEVERE, URL_ERROR, ex);
			  queueSession.rollback();
			  msg = (TextMessage) msgConsumer.receive();
			  msg2=queueSession.createTextMessage();
			  msg2.setIntProperty("error", 1);
			  msg2.setIntProperty("id", msg.getIntProperty("id"));
			  msg2.setText("No cameras available for that brand");
			  msgProducer.send(msg2);
			  System.out.println("Sending ERROR -> "+id);
			  queueSession.commit();
          }
		}
    }

}
