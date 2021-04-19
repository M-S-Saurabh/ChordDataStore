/*******************************************************************************
 * Authors: ------------
 * Saurabh Mylavaram (mylav008@umn.edu)
 * Edwin Nellickal (nelli053@umn.edu)
 ******************************************************************************/
/* You may use and modify this interface file for your Assignment 7 */
/* You may add new methods or change any of the methods in this interface. */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
	public int getNodeId() throws RemoteException;
	public void setPredecessor(Node node) throws RemoteException;
	public Node  findSuccessor (int key, boolean traceFlag) throws RemoteException;
	public Node  findPredecessor (int key) throws RemoteException;
	public Node  closestPrecedingFinger (int key) throws RemoteException;
	public Node  successor () throws RemoteException;
	public Node  predecessor  () throws RemoteException;
	public boolean join (String nodeURL) throws RemoteException, InterruptedException;
	public boolean joinFinished (String nodeURL) throws RemoteException, InterruptedException;
	public boolean insert (String word, String definition) throws RemoteException;
	public String  lookup (String word) throws RemoteException;
	public String  printFingerTable() throws RemoteException;
	public String  printDictionary() throws RemoteException;
	public void updateFingerTable(Node s, int i) throws RemoteException;
	public void insertHere(String word, String definition) throws RemoteException;
	public String lookupHere(String word) throws RemoteException;
	public String getNodeURL() throws RemoteException;
}

