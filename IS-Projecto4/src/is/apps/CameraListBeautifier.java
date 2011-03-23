/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.apps;

import is.conf.LPCOJMSConf;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Hashtable;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
/**
 *
 * @author lopes
 */
public class CameraListBeautifier {


	public static void main(String[] args) throws NamingException, JMSException {
		Transformer transformer = null;
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer(new StreamSource("xml/CameraListBeautifier.xsl"));
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		

		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context context = new InitialContext(ht);

		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(LPCOJMSConf.CONNECTION_FACTORY);

		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		queueConnection.start();

		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		Queue myOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_BEAUTIFIER_OUTBOX);
		Queue myInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_BEAUTIFIER_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(myInboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(myOutboxQueue);

		TextMessage msg = null;
		String xml = null;
		int id;
		String result = null;
		while(true){
			msg = (TextMessage) msgConsumer.receive();
			xml = msg.getText();
			id = msg.getIntProperty("id");
			System.out.println("Received ["+id+"]");

			try{
				StringWriter sw = new StringWriter();
				StreamResult sr = new StreamResult(sw);
				transformer.transform(new StreamSource(new StringReader(xml)), sr);
				result = sw.getBuffer().toString();
				//System.out.println(result);
			} catch (Throwable t) {
				t.printStackTrace();
			}

			msg.clearBody();
			msg.setText(result);
			msgProducer.send(msg);
			System.out.println("Sending -> "+id);
			queueSession.commit();
		}
	}
}
