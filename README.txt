-----------------------------------------------------------------------------------------------------------------------
HOW TO RUN:
-------------------------------------------
USING PYTHON SCRIPT:
---------------------
You can download the code onto any CSE labs machine and run the python script supplied:
	before running the python script, please to compile the java class files:
		make
		
	To run server processes: 
		python3 run.py SRV <instance-number> 
	instance number can be between 0,....7 (for 8 instances)
	(Run this line for instance number 0 through 7 on a new terminal each time)
	
	To load the dictionary:
		 python3 run.py DICT <port-number>
		 
	To run client processes:
		python3 run.py CLNT <port-number> <number-of-nodes>
	(port number is the RMI registry port and number of nodes is 8)

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
		java DictionaryLoader localhost:<port-number>/node00 ./sample-dictionary-file.txt

- Wait for the DictionaryLoader to complete, start the client process:
		java Client localhost:<port-number>/node00 <number-of-nodes>

-----------------------------------------------------------------------------------------------------------------------
LOG FILES
------------

-----------------------------------------------------------------------------------------------------------------------
KNOWN BUGS:
------------------

-----------------------------------------------------------------------------------------------------------------------
Results of Performance measurement experiment:
-----------------------------------------------

---------------------
Server avg.: 0.0217s 
Client avg.: 0.0224s

