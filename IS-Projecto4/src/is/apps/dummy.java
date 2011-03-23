/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package is.apps;

import java.io.IOException;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author lopes
 */
public class dummy {

	public static void main(String []args) throws NamingException, JMSException, IOException{


		Hashtable ht = new Hashtable();
		ht.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
        ht.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.appserv.naming.S1ASCtxFactory");
		Context ctx = new InitialContext(ht);

		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup("testeMQCF");

		Queue q = (Queue) ctx.lookup("IS/UserOutbox");

		QueueConnection qc = qcf.createQueueConnection();
		QueueSession qs = qc.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

		MessageConsumer mc = qs.createConsumer(q);

		TextMessage msg;

		qc.start();

		msg = (TextMessage) mc.receive();
		System.out.println("Recreived: "+msg.getText());
		qs.commit();

		mc.close();
		qs.close();
		qc.close();
		System.out.println("done");
		System.exit(0);
	}

}
