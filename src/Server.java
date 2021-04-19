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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends UnicastRemoteObject implements Node {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private String nodeURL;
	
	private String joiningURL;

	private String rmiUrlFormat;

	private Hashtable<String, String> dictionary;

	int nodeId;

	private ArrayList<Finger> finger;

	private Node predecessorNode;

	private int m;

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
		
		this.m = Constants.KEY_BITS;
		
		this.nodeId = FNV1aHash.hash32( serverName );
		logger.info("Hash for this serverName is: "+this.nodeId);
		
		this.createFingerTable();
		
		this.dictionary = new Hashtable<String, String>();
		
		if(serverId > 0) {
			this.connectToCluster();
		}
	}
	
	// This is not the same as init_finger_table in the paper,
	// Please see updateFingerTable().
	private void createFingerTable() {
		finger = new ArrayList<Finger>();
		finger.add(null);
		for(int i=1; i<=m; i++) {
			finger.add(new Finger(nodeId, i));
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
	public Node findSuccessor(int key, boolean traceFlag) throws RemoteException {
		Node n1 = this.findPredecessor(key);
		return n1.successor();
	}

	@Override
	public Node findPredecessor(int key) throws RemoteException {
		Node n1 = this;
		while(key <= n1.getNodeId() || key > n1.successor().getNodeId()) {
			n1 = n1.closestPrecedingFinger(key);
		}
		return n1;
	}

	@Override
	public Node closestPrecedingFinger(int key) throws RemoteException {
		for(int i=m; i>0; i--) {
			if(this.nodeId < this.finger.get(i).node.getNodeId() 
					&& this.finger.get(i).node.getNodeId() < key) {
				return this.finger.get(i).node;
			}
		}
		return this;
	}

	@Override
	public Node successor() throws RemoteException {
		return this.finger.get(1).node;
	}

	@Override
	public Node predecessor() throws RemoteException {
		return this.predecessorNode;
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
	
	private void joinProcess(Node n1) throws RemoteException {
		if (n1 != null) {
			initFingerTable(n1);
			updateOthers();
		}else {
			for(int i=1; i<=m; i++) {
				this.finger.get(i).node = this;
			}
			this.predecessorNode = this;
		}
	}

	// This function is originally called init_finger_table in the paper.
	private void initFingerTable(Node n1) throws RemoteException {
		boolean traceFlag = false;
		this.finger.get(1).node = n1.findSuccessor(finger.get(1).start, traceFlag);
		this.predecessorNode = this.successor().predecessor();
		this.successor().setPredecessor(this);
		for(int i=1; i<m; i++) {
			if(this.nodeId < finger.get(i+1).start 
					&& finger.get(i+1).start < finger.get(i+1).node.getNodeId()) {
				finger.get(i+1).node = finger.get(i).node;
			}else {
				finger.get(i+1).node = n1.findSuccessor(finger.get(i+1).start, traceFlag);
			}
		}
	}
	
	private void updateOthers() throws RemoteException {
		int n = this.getNodeId();
		for(int i=1; i<=m; i++) {
			Node p = findPredecessor(n - (1 << (i-1)));
			p.updateFingerTable(this, i);
		}
	}
	
	@Override
	public void updateFingerTable(Node s, int i) throws RemoteException {
		if( this.getNodeId() <= s.getNodeId() && s.getNodeId() < finger.get(i).node.getNodeId()) {
			finger.get(i).node = s;
			Node p = predecessor();
			p.updateFingerTable(s, i);
		}
	}

	@Override
	public boolean insert(String word, String definition) throws RemoteException {
		int key = FNV1aHash.hash32(word);
		Node p = this.findPredecessor(key).successor();
		p.insertHere(word, definition);
		return false;
	}
	
	@Override
	public void insertHere(String word, String definition) throws RemoteException {
		this.dictionary.put(word, definition);
	}

	@Override
	public String lookup(String word) throws RemoteException {
		int key = FNV1aHash.hash32(word);
		Node p = this.findPredecessor(key).successor();
		return p.lookupHere(word);
	}
	
	@Override
	public String lookupHere(String word) throws RemoteException {
		return this.dictionary.get(word);
	}

	@Override
	public String printFingerTable() throws RemoteException {
		StringBuilder b = new StringBuilder();
		this.finger.forEach((fgr)->{
			try {
				b.append("start:"); b.append(fgr.start);
				Node node = fgr.node;
				b.append(" node-key:"); b.append(node.getNodeId());
				b.append(" node-url:"); b.append(node.getNodeURL());
				b.append(System.lineSeparator());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		String tableString = b.toString();
				
		logger.info("------Printing Local Finger Table----");
		logger.info(tableString);
		return tableString;
	}

	@Override
	public String printDictionary() throws RemoteException {
		StringBuilder b = new StringBuilder();
		this.dictionary.forEach((key, value)->{
			b.append(key); b.append(':'); b.append(value); b.append(System.lineSeparator());
		});
		String dictString = b.toString();
				
		logger.info("------Printing Local Dictionary----");
		logger.info(dictString);
		return dictString;
	}

	@Override
	public int getNodeId() throws RemoteException {
		return this.nodeId;
	}
	
	@Override
	public String getNodeURL() throws RemoteException {
		return this.nodeURL;
	}

	@Override
	public void setPredecessor(Node node) throws RemoteException {
		this.predecessorNode = node;
	}

}
