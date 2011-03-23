package is.conf;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lopes
 */
public class LPCOJMSConf {

	public final static String CONNECTION_FACTORY = "IS/QueueConnectionFactory";

	public final static String USER_OUTBOX = "IS/UserOutbox";
	public final static String USER_INBOX = "IS/UserInbox";

	public final static String CAMERA_SEARCH_OUTBOX = "IS/CameraSearchOutbox";
	public final static String CAMERA_SEARCH_INBOX = "IS/CameraSearchInbox";

	public final static String CAMERA_SUMARY_OUTBOX = "IS/CameraSumaryOutbox";
	public final static String CAMERA_SUMARY_INBOX = "IS/CameraSumaryInbox";

	public final static String CAMERA_BEAUTIFIER_OUTBOX = "IS/CameraBeautifierOutbox";
	public final static String CAMERA_BEAUTIFIER_INBOX = "IS/CameraBeautifierInbox";

}
