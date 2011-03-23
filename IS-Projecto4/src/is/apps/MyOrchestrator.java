/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.apps;

import java.io.IOException;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.*;
import is.conf.*;
import is.util.resultObject;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

/**
 *
 * @author lopes
 */

public class MyOrchestrator {

	private static void UserReader(Context context, QueueConnection queueConnection) throws JMSException, NamingException
	{

		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
		Queue userOutboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_OUTBOX);
		Queue cameraSearchInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SEARCH_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(userOutboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(cameraSearchInboxQueue);

		TextMessage msg = null, msg2;
		int counter = 1;

		System.out.println("UserReader READY");

		while(true)
		{
			msg = (TextMessage) msgConsumer.receive();
			System.out.println("[UserRead] Sending ["+counter+"] "+msg.getText()+" to cameraSearchXML");

			msg2 = queueSession.createTextMessage();
			msg2.setIntProperty("id", counter++);
			msg2.setText(msg.getText());

			msgProducer.send(msg2);
			queueSession.commit();
		}

	}
	
	public static void main(String []args) throws NamingException, JMSException, IOException{


		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context context = new InitialContext(ht);

		Hashtable<Integer, String> hashTable = new Hashtable<Integer, String>();

/*
		NamingEnumeration<NameClassPair> ne = context.list("");
		while(ne.hasMore())
			 System.out.println(ne.next().getName());
*/
		
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(LPCOJMSConf.CONNECTION_FACTORY);

		QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
		queueConnection.start();

		//CREATE OTHER THREADS
		//TODO CREATE THE OTHER THREADS
		new SearchReader(context, queueConnection);
		new SumaryReader(context, queueConnection, hashTable);
		new BeautifierReader(context, queueConnection, hashTable);

		//READ FROM USER
		UserReader(context, queueConnection);


		queueConnection.close();
		context.close();
		System.out.println("Done");
		System.exit(0);
	}
}


class SearchReader extends Thread{
	
	Context context;
	QueueConnection queueConnection;

	public SearchReader(Context context, QueueConnection queueConnection) throws JMSException, NamingException {
		this.context = context;
		this.queueConnection = queueConnection;
		this.start();
	}

	@Override
	public void run() {
		try {
		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

		Queue userInboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_INBOX);
		Queue searchOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SEARCH_OUTBOX);
		Queue cameraSumaryInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SUMARY_INBOX);
		Queue cameraBeautifierInboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_BEAUTIFIER_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(searchOutboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(cameraSumaryInboxQueue);
		MessageProducer msgProducer2 = queueSession.createProducer(cameraBeautifierInboxQueue);
		MessageProducer msgProducer3 = queueSession.createProducer(userInboxQueue);

		TextMessage msg;

		System.out.println("SearchReader READY");

		while(true)
		{
			msg = (TextMessage) msgConsumer.receive();
			if (msg.propertyExists("error"))
			{
				System.out.println("[SearchReader] Sending ERROR back to user ["+msg.getIntProperty("id")+"]");
				msgProducer3.send(msg);
			}
			else
			{
				System.out.println("[SearchReader] Sending ["+msg.getIntProperty("id")+"] XML to Summary and Beautifier");
				msgProducer.send(msg);
				msgProducer2.send(msg);
			}
			queueSession.commit();
		}
		}catch(JMSException e) {
			e.printStackTrace();
		}catch(NamingException ex){
			ex.printStackTrace();
		}
		System.out.println("SearchReader EXITING");
	}
}

class SumaryReader extends Thread{

	Context context;
	QueueConnection queueConnection;
	Hashtable<Integer, String> hashTable;

	public SumaryReader(Context context, QueueConnection queueConnection, Hashtable<Integer, String> hashTable) throws JMSException, NamingException {
		this.context = context;
		this.queueConnection = queueConnection;
		this.hashTable = hashTable;
		this.start();
	}

	@Override
	public void run() {
		try {
		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

		Queue sumaryOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_SUMARY_OUTBOX);
		Queue userInboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(sumaryOutboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(userInboxQueue);

		TextMessage msg;
		ObjectMessage resultMsg;

		System.out.println("SumaryReader READY");

		while(true)
		{
			msg = (TextMessage) msgConsumer.receive();
			synchronized(hashTable){
				if (this.hashTable.containsKey(msg.getIntProperty("id")))
				{
					System.out.println("[SumaryReader] Sending ["+msg.getIntProperty("id")+"] HTML and XML to User");
					resultMsg = queueSession.createObjectMessage();
					resultMsg.setIntProperty("id", msg.getIntProperty("id"));
					resultMsg.setObject(new resultObject(msg.getText(), hashTable.get(msg.getIntProperty("id"))));
					hashTable.remove(msg.getIntProperty("id"));
					msgProducer.send(resultMsg);
				}
				else
				{
					System.out.println("[SumaryReader] Puting ["+msg.getIntProperty("id")+"] in the hashtable and waiting for HTML");
					hashTable.put(msg.getIntProperty("id"), msg.getText());
				}
			}
			queueSession.commit();
		}
		}catch(JMSException e) {
			e.printStackTrace();
		}catch(NamingException ex){
			ex.printStackTrace();
		}
		System.out.println("SumaryReader EXITING");
	}

}

class BeautifierReader extends Thread{

	Context context;
	QueueConnection queueConnection;
	Hashtable<Integer, String> hashTable;

	public BeautifierReader(Context context, QueueConnection queueConnection, Hashtable<Integer, String> hashtable) throws JMSException, NamingException {
		this.context = context;
		this.queueConnection = queueConnection;
		this.hashTable = hashtable;
		this.start();
	}

	@Override
	public void run() {
		try {
		QueueSession queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

		Queue beautifierOutboxQueue = (Queue) context.lookup(LPCOJMSConf.CAMERA_BEAUTIFIER_OUTBOX);
		Queue userInboxQueue = (Queue) context.lookup(LPCOJMSConf.USER_INBOX);

		MessageConsumer msgConsumer = queueSession.createConsumer(beautifierOutboxQueue);
		MessageProducer msgProducer = queueSession.createProducer(userInboxQueue);

		TextMessage msg;
		ObjectMessage resultMsg;

		System.out.println("BeautifierReader READY");

		while(true)
		{

			msg = (TextMessage) msgConsumer.receive();
			synchronized(hashTable){
			if (this.hashTable.containsKey(msg.getIntProperty("id")))
			{
				System.out.println("[BeautifierReader] Sending ["+msg.getIntProperty("id")+"] HTML and XML to User");
				resultMsg = queueSession.createObjectMessage();
				resultMsg.setIntProperty("id", msg.getIntProperty("id"));
				resultMsg.setObject(new resultObject( hashTable.get(msg.getIntProperty("id")),msg.getText()));
				hashTable.remove(msg.getIntProperty("id"));
				msgProducer.send(resultMsg);
			}
			else
			{
				System.out.println("[BeautifierReader] Puting ["+msg.getIntProperty("id")+"] in the hashtable and waiting for XML");
				hashTable.put(msg.getIntProperty("id"), msg.getText());
			}
			}
			queueSession.commit();

		}
		}catch(JMSException e) {
			e.printStackTrace();
		}catch(NamingException ex){
			ex.printStackTrace();
		}
		System.out.println("BeautifierReader EXITING");
	}

}