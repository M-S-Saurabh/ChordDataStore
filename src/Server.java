/*******************************************************************************
 * Authors: ------------
 * Saurabh Mylavaram (mylav008@umn.edu)
 * Edwin Nellickal (nelli053@umn.edu)
 ******************************************************************************/
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server implements Node {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) throws AlreadyBoundException, SecurityException, IOException {
		if (args.length != 1) {
			throw new RuntimeException("Syntax: Server <server ID>");
		}
		int serverId = Integer.parseInt(args[0]);

		System.out.println(String.format("Hostname is %s", InetAddress.getLocalHost().getHostName()));

		// This block configure the logger with handler and formatter FileHandler fh
		FileHandler fh = new FileHandler(String.format("./logs/Server-%d.log", serverId));
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", Constants.LOG_FORMAT); 
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);

		// setting the security policy
		System.setProperty("java.security.policy", "file:./security.policy");
		System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostName());
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		// the stub that is exposed via the RMI registry
		String serverName = String.format(Constants.RMI_SERVER_NAME, serverId);
		int RMIRegPort = Constants.RMI_PORT;
		Node dataStore = (Node) UnicastRemoteObject.toStub(new Server(serverId, serverName)); 
		logger.info(String.format("Using the supplied RMI registry port: %d", RMIRegPort)); 
		Registry localRegistry = LocateRegistry.getRegistry(RMIRegPort);

		localRegistry.bind(serverName, dataStore); // setting up
	}

	public Server(int serverId, String serverName) {
		super();
		int nodeHash = FNV1aHash.hash32( serverName );
		logger.info("Hash for this serverName is: "+nodeHash);
	}

	@Override
	public String findSuccessor(int key, boolean traceFlag) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findPredecessor(int key) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String closestPrecedingFinger(int key) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String successor() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String predecessor() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean join(String nodeURL) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinFinished(String nodeURL) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insert(String word, String definition) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lookup(String word) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printFingerTable() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String printDictionary() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
