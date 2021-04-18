/*******************************************************************************
 * Authors: ------------
 * Saurabh Mylavaram (mylav008@umn.edu)
 * Edwin Nellickal (nelli053@umn.edu)
 ******************************************************************************/
import java.io.IOException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends UnicastRemoteObject implements Node {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private String nodeURL;
	
	private String joiningURL;

	private String rmiUrlFormat;

	public static void main(String[] args) throws AlreadyBoundException, SecurityException, IOException, InterruptedException {
		if (args.length != 1) {
			throw new RuntimeException("Syntax: Server <server ID>");
		}
		int serverId = Integer.parseInt(args[0]);

		setupLogger(serverId);

		bindToLocalRMI(serverId);
	}

	private static void bindToLocalRMI(int serverId) throws UnknownHostException, NoSuchObjectException,
			RemoteException, AlreadyBoundException, AccessException, InterruptedException {
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

	// This function configure the logger with handler and formatter FileHandler fh
	private static void setupLogger(int serverId) throws IOException {
		FileHandler fh = new FileHandler(String.format("./logs/Server-%d.log", serverId));
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", Constants.LOG_FORMAT); 
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}

	public Server(int serverId, String serverName) throws UnknownHostException, RemoteException, InterruptedException {
		super();
		this.rmiUrlFormat = String.format("rmi://%s:%s/%s", 
											InetAddress.getLocalHost().getHostName(),
											Constants.RMI_PORT, 
											Constants.RMI_SERVER_NAME);
		this.nodeURL = String.format(this.rmiUrlFormat, serverId);
		logger.info("nodeURL is: "+nodeURL);
		this.joiningURL = "";
		
		int nodeHash = FNV1aHash.hash32( serverName );
		logger.info("Hash for this serverName is: "+nodeHash);
		if(serverId > 0) {
			this.connectToCluster();
		}
	}
	
	private void connectToCluster() throws UnknownHostException, InterruptedException {
		System.setProperty("java.security.policy","file:./security.policy");
        System.setSecurityManager(new SecurityManager());
        
		try {
			// Getting Node-0 stub from RMI registry.
			String rmiURL = String.format(this.rmiUrlFormat, 0);
			Node node0 = (Node) Naming.lookup(rmiURL);
			node0.join(this.nodeURL);
			
			node0.joinFinished(this.nodeURL);
			
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			logger.severe("Couldn't establish RMI registry connection.");
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
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
	public synchronized boolean join(String nodeURL) throws RemoteException, InterruptedException {
		while(this.joiningURL.length() != 0) {
			wait();
		}
		this.joiningURL = nodeURL;
		notify();
		return true;
	}

	@Override
	public synchronized boolean joinFinished(String nodeURL) throws RemoteException, InterruptedException {
		while(!this.joiningURL.equals(nodeURL)) {
			wait();
		}
		this.joiningURL = "";
		notify();
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
