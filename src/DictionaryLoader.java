import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Stream;

public class DictionaryLoader {
	
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			throw new RuntimeException(
					"Syntax: java DictionaryLoader <Server URL> <Dictionary Filename>");
		}
		// Setting up logger.
		configureLogger();

		// Parse arguments
		String nodeURL = args[0];
		String dictionaryFilename = args[1];

		System.setProperty("java.security.policy","file:./security.policy");
        System.setSecurityManager(new SecurityManager());
		// Connect to cluster.
		Node serverNode = connectToServer(nodeURL);

		// Insert data into cluster
		logger.info("Inserting word-definitions into the DHT...");
		try (Stream<String> lines = Files.lines(Paths.get(dictionaryFilename), Charset.defaultCharset())) {
			lines.forEachOrdered(line -> {
				try {
					insertWord(line, serverNode);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			});
		}
		logger.info("Data Insertion: Complete.");
	}

	private static void configureLogger() throws SecurityException, IOException {
		FileHandler fh = new FileHandler("./logs/DictionaryLoader.log");
		logger.addHandler(fh);
		System.setProperty("java.util.logging.SimpleFormatter.format", Constants.LOG_FORMAT); 
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}

	private static Node connectToServer(String nodeURL) {
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

	public static void insertWord(String line, Node serverNode) throws RemoteException {
		String[] splitArray = line.split(" : ", 2);
		String node = serverNode.insert(splitArray[0], splitArray[1]);
		System.out.println("Node:"+node+ " Word:"+ splitArray[0]);
	}

}
