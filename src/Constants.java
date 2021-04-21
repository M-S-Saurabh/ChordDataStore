/*******************************************************************************
 * Authors:
 * ---------
 * Saurabh Mylavaram (mylav008@umn.edu)
 * Edwin Nellickal (nelli053@umn.edu)
 ******************************************************************************/



public final class Constants {
	// Sever init constants
	public static final int RMI_PORT = 1099;
	
	private Constants() {
		// restrict instantiation
	}
	
	// Logging related
	public static final String LOG_FORMAT = "%5$s%6$s%n";
	
	public static final String SERVER_MSG_LOG = "Server-%d %s %s [%d, %d] %s %s";
	public static final String CLIENT_REQ = "CLIENT-REQ";
	public static final String SERVER_REQ = "SRV-REQ";
	public static final String REQ_PROCESSING = "REQ_PROCESSING";
	
	public static final String CLIENT_REQ_LOG = "CLNT-%d SRV-%d REQ %s %s %s";
	public static final String CLIENT_RSP_LOG = "CLNT-%d SRV-%d RSP %s %s";
	
	// RMI stub name used on registry.
	public static final String RMI_SERVER_NAME = "node0%d";

	public static final int KEY_BITS = 31;

	public static final String NOT_FOUND = "NOT FOUND";
	
	public static final long RANDOM_SEED = 49;

	public static final String FUNC_LOG = "Node:%s - Function:%s - Arguments:%s";
	
//	public static enum MessageType {
//		CREATE, 
//		DEPOSTI,
//		CHECK, 
//		TRANSFER,
//		ACK
//	};
}
