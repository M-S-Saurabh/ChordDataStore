import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*******************************************************************************
 * Authors: ------------
 * Saurabh Mylavaram (mylav008@umn.edu)
 * Edwin Nellickal (nelli053@umn.edu)
 ******************************************************************************/
public class Client {
	
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Node node;

	public static void main(String[] args) throws SecurityException, IOException {
		if(args.length != 2) {
			throw new RuntimeException("Syntax: java Client <Node URL> <NumNodes>");
		}
		// Setting up logger.
		configureLogger();
		
		Client client = new Client(args[0], Integer.parseInt(args[1]));
		
		boolean exitLoop = false;
		while(!exitLoop) {
			Scanner scan = new Scanner(System.in);
		    System.out.println("\nEnter 1 to lookup, 2 to exit, 3 to print tables");
		    System.out.println("Enter your choice: ");

		    int option = Integer.parseInt(scan.nextLine());
		    if(option == 2) {
		    	System.out.println("Exiting...");
		    	exitLoop = true;
		    	scan.close();
		    } else if(option == 1) {
		    	System.out.println("Enter a word to query: ");
		    	String queryWord = scan.nextLine();
		    	String result = client.wordLookup(queryWord); 
		    	System.out.println(String.format("Result: %s", result));
		    } else if (option == 3) {
		    	System.out.println("Enter a node-id to query: ");
		    	int queryWord = Integer.parseInt(scan.nextLine());
		    	String result = client.printDictionary(queryWord); 
		    	System.out.println(String.format("Dictionary table:\n  %s ", result));
		    }
		}
	}
	
	private String printDictionary(int queryWord) throws RemoteException, UnknownHostException {
		String rmiUrlFormat = String.format("%s:%s/%s", 
				InetAddress.getLocalHost().getHostName(),
				Constants.RMI_PORT, 
				Constants.RMI_SERVER_NAME);
		String nodeURL = String.format(rmiUrlFormat, queryWord);
		Node node = initConnection(nodeURL);
		return node.printDictionary();
	}

	private static void configureLogger() throws SecurityException, IOException {
		FileHandler fh = new FileHandler("./logs/Client.log");
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", Constants.LOG_FORMAT); 
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}
	
	public Client(String nodeURL, int numNodes) throws UnknownHostException, RemoteException {
		super();
		System.setProperty("java.security.policy","file:./security.policy");
		System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostName());
        System.setSecurityManager(new SecurityManager());
		this.node = initConnection(nodeURL);
		
		printTables( numNodes);
	}

	private Node initConnection(String nodeURL) {
		
		try {
			// Getting Server stub from RMI registry.
			Node chordServer = (Node) Naming.lookup(String.format("rmi://%s", nodeURL));
			return chordServer;
			
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			logger.severe("Couldn't establish RMI registry connection.");
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private String wordLookup(String queryWord) throws RemoteException {
		String result = this.node.lookup(queryWord);
		if(result != null) {
			return result;
		}
		return Constants.NOT_FOUND;
	}
	
	private void printTables(int numNodes) throws UnknownHostException, RemoteException {
		String rmiUrlFormat = String.format("%s:%s/%s", 
				InetAddress.getLocalHost().getHostName(),
				Constants.RMI_PORT, 
				Constants.RMI_SERVER_NAME);
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(int i=0; i<numNodes; i++) {
			String nodeURL = String.format(rmiUrlFormat, i);
			nodes.add(initConnection(nodeURL));
		}
		
		logger.info("Printing Finger Tables...");
		for (int i=0; i<numNodes; i++) {
			logger.info(String.format("---Node %d:---", i));
			logger.info(nodes.get(i).printFingerTable());
		}
		
		logger.info("Printing Dictionary Tables...");
		for (int i=0; i<numNodes; i++) {
			logger.info(String.format("---Node %d:---", i));
			logger.info(nodes.get(i).printDictionary());
		}
	}
}
