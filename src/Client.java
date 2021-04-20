import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
		if(args.length != 1) {
			throw new RuntimeException("Syntax: java Client <Node URL>");
		}
		// Setting up logger.
		configureLogger();
		
		Client client = new Client(args[0]);
		
		boolean exitLoop = false;
		while(!exitLoop) {
			Scanner scan = new Scanner(System.in);
		    System.out.println("Enter 1 to lookup, 2 to exit");
		    System.out.println("Enter your choice: ");

		    int option = Integer.parseInt(scan.nextLine());
		    if(option == 2) {
		    	System.out.println("Exiting...");
		    	exitLoop = true;
		    	scan.close();
		    }
		    else if(option == 1) {
		    	System.out.println("Enter a word to query: ");
		    	String queryWord = scan.nextLine();
		    	String result = "NOT FOUND";//client.wordLookup(queryWord); 
		    	System.out.println(String.format("Result: %s", result));
		    }
		}
	}

	private static void configureLogger() throws SecurityException, IOException {
		FileHandler fh = new FileHandler("./logs/Client.log");
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", Constants.LOG_FORMAT); 
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}
	
	public Client(String nodeURL) {
		super();
		this.node = initConnection(nodeURL);
	}

	private Node initConnection(String nodeURL) {
		System.setProperty("java.security.policy","file:./security.policy");
        System.setSecurityManager(new SecurityManager());
        
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
}
