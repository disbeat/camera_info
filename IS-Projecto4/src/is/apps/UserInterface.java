/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.apps;

import is.conf.LPCOJMSConf;
import is.util.resultObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
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

/**
 *
 * @author lopes
 */

class receiver extends Thread{

	QueueSession queueSession = null;
	Queue userInboxQueue = null;
	Queue cameraSearchInboxQueue = null;
	MessageConsumer msgConsumer = null;

	receiver(Context context, QueueConnection queueConnection) throws JMSException, NamingException
	{
		queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		userInboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_INBOX);
		msgConsumer = queueSession.createConsumer(userInboxQueue);
		queueConnection.start();
		this.start();
	}

	@Override
	public void run()
	{
		Message msg = null;
		System.out.println("Receiver READY!");
		String xml, html;
		resultObject result;
		int id;
		FileWriter file = null;
		BufferedWriter bw;
		while(true)
		{
			try {
				msg = msgConsumer.receive();
				if (msg instanceof TextMessage)
				{
					System.out.println("ERROR["+msg.getIntProperty("id")+"]: "+((TextMessage) msg).getText());
				}
				else
				{
					result = (resultObject) ((ObjectMessage) msg).getObject();
					id = msg.getIntProperty("id");

					System.out.println("\nGot msg["+id+"]");
					try {

						File f =  new File("results/htmlResult"+id+".html");
						file = new FileWriter(f, false);
						bw = new BufferedWriter(file);
						bw.write(result.getHtml());
						System.out.println("FILE CREATED: "+f.getAbsolutePath());
						bw.close();
						file.close();


						f =  new File("results/xmlResult"+id+".xml");
						file = new FileWriter(f, false);
						bw = new BufferedWriter(file);
						System.out.println("FILE CREATED: "+f.getAbsolutePath());
						bw.write(result.getXml());
						bw.close();
						file.close();
					} catch (IOException ex) {
						Logger.getLogger(receiver.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				queueSession.commit();
				
			} catch (JMSException ex) {
				Logger.getLogger(receiver.class.getName()).log(Level.SEVERE, null, ex);
				
			}
			
		}
	}
}


public class UserInterface {
	public static void main (String args[]) throws IOException, NamingException, JMSException{
		String brand;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context context = new InitialContext(ht);

		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(LPCOJMSConf.CONNECTION_FACTORY);

		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		
		Queue userOutboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_OUTBOX);

		MessageProducer msgProducer = queueSession.createProducer(userOutboxQueue);

		TextMessage msg = null;

		new receiver(context, queueConnection);

		while(true)
		{
			System.out.print("Search: ");
			brand = br.readLine().trim().toLowerCase();
			if (brand.length()<1)
				continue;
			msg = queueSession.createTextMessage();
			msg.setText(brand);
			msgProducer.send(msg);
			queueSession.commit();
			//TODO search brand
		}
	}
}
