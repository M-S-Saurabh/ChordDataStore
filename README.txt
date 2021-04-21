-----------------------------------------------------------------------------------------------------------------------
HOW TO RUN:
-------------------------------------------
USING PYTHON SCRIPT:
---------------------
You can download the code onto any CSE labs machine and run the python script supplied:
	To run server processes: 
		python3 run.py SRV <numClients>
	(Run this line on all the server machines which are listed in config file.)

	To run client processes:
		python3 run.py CLNT <client-id> <num-threads>

This script will compile required java programs and start rmiregistry on the port specified by config file.
It will also run all the server processes which are needed on that machine according to 'configFile.txt'.

WITHOUT USING THE PYTHON SCRIPT:
----------------------------
- Change directory into the ChorDataStore folder

- To compile all the relevant java classes, enter:
	make

- To clean all the class files, enter:
	make clean

- To start rmiregistry, from the same terminal enter:
		rmiregistry <port-number>
	for our testing we used the port number 1099.

- To start the server process, open a new terminal at the same directory */ChordDataStore/* and enter:
		java Server <instance number>

- perform the above operation for instance number 0 through 7, on different terminal instances.

- monitor the terminal running server0 to check for a message that says "node07 is releasing the join lock"
  this message indicates that the cluster has formed.

- To execute the dictionary loader, open a new terminal and execute:
		java DictionaryLoader localhost:<port-number>/node00 ./sample-dictionary-file.tx

- Wait for the DictionaryLoader to complete, start the client process:
		java Client localhost:<port-number>/node00

-----------------------------------------------------------------------------------------------------------------------
LOG FILES
------------
- All log files are stored in the sub-directory ./logs

- Server log files are named according to their id. 
	For example: Server with id=0 has log file named 'Server-0.log'

- Similarly client log files are named as: 'Client-<id>.log'
	For example: 'Client-0.log'
-----------------------------------------------------------------------------------------------------------------------
KNOWN BUGS:
------------------
- The python script does not exit even after all the java processes have exited. 
	Just do a Ctrl-C to exit after all processes are done

- However, all Java processes exit gracefully.

- If rmiregistry is already running on the machine,
  python script may throw an error briefly but it will still continue all operations normally.

- Before running client process, we have to wait for server processes to complete connection forming phase.
  Wait for a message from all server processes, which says:
	"---- Server Initialization complete. Ready for client messages. ----"

-----------------------------------------------------------------------------------------------------------------------
Results of Performance measurement experiment:
-----------------------------------------------
Server machines: csel-kh1260-01 to csel-kh1260-05
Client machine: csel-kh1260-20

5 servers, 1 client : 
----------------------
Server avg.: 0.1486s 0.1424s 0.1735s 0.1434s 0.1502s | Overall server avg.: 0.1516s
Client avg. time: 0.1528s

3 servers, 1 client:
---------------------
Server avg.: 0.1472s 0.1490s 0.1690s | Overall server avg.: 0.1550s
Client avg.: 0.1562s

1 servers, 1 client:
---------------------
Server avg.: 0.0217s 
Client avg.: 0.0224s

