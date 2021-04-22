RESULTS: 
----------
The Finger tables, and Dictionary Tables of each node are given in "RESULTS.txt" file.
Diagram is attached as png file: "ChordDiagram.png"

-----------------------------------------------------------------------------------------------------------------------
HOW TO RUN:
-------------------------------------------
USING PYTHON SCRIPT:
---------------------
You can download the code onto any CSE labs machine and run the python script supplied:
	
	before running the python script, to compile the java class files:
		make
		
	The following commands can be run in the same terminal in succession:
	
	To run server processes: 
		python3 run.py SRV <port-number> <number-of-nodes> 

	***port number to be used in our case is 1099 for the RMI registry***
	
	To load the dictionary:
		 python3 run.py DICT <port-number>
		 
	To run client processes:
		python3 run.py CLNT <port-number> <number-of-nodes>
		
PS: 
in the case you wish to retry the code, the rmi registry needs to be restarted for which you should follow:
	user@csel-vole-04:/home/user/GIT/ChordDataStore $ ps -ef | grep rmiregistry
	user 4180230 4130018  1 20:14 pts/4    00:00:00 rmiregistry 1099
	user 4181149 4133288  0 20:15 pts/4    00:00:00 grep rmiregistry
	user@csel-vole-04:/home/user/GIT/ChordDataStore $ kill 4180230

        	

WITHOUT USING THE PYTHON SCRIPT:
----------------------------
- Change directory into the project folder

- To compile all the relevant java classes, enter:
	make

- To clean all the class files, enter:
	make clean

- To start rmiregistry, from the same terminal enter:
		rmiregistry <port-number>
	***for our testing we used the port number 1099.***

- To start the server process, open a new terminal at the project directory and enter:
		java Server <instance number>

- perform the above operation for instance number 0 through 7, on different terminal instances.

- monitor the terminal running server0 to check for a message that says "node07 is releasing the join lock"
  this message indicates that the cluster has formed.

- To execute the dictionary loader, open a new terminal and execute:
		java DictionaryLoader localhost:<port-number>/node00 ./sample-dictionary-file.txt

- Wait for the DictionaryLoader to complete, start the client process:
		java Client localhost:<port-number>/node00 <number-of-nodes>
		
PS: 
in the case you wish to retry the code, the rmi registry needs to be restarted for which you should follow:
	user@csel-vole-04:/home/user/GIT/ChordDataStore $ ps -ef | grep rmiregistry
	user 4180230 4130018  1 20:14 pts/4    00:00:00 rmiregistry 1099
	user 4181149 4133288  0 20:15 pts/4    00:00:00 grep rmiregistry
	user@csel-vole-04:/home/user/GIT/ChordDataStore $ kill 4180230
	

---------------------------------------------------------------------------------------------------
CLIENT INTERACTION EXAMPLE:
---------------------------

LOOKUP EXAMPLE
---------------
Enter 1 to lookup, 2 to exit, 3 to print tables
Enter your choice: 
1
Enter a word to query: 
litre
Result: a liquid measure equivalent to 1000 CC.

PRINT DICTIONARY
----------------
Enter 1 to lookup, 2 to exit, 3 to print tables
Enter your choice: 
3
Enter a node-id to query: 
0
Dictionary table:
  jump:a sudden movement
fascinate:attract, enchant
disaffection:dislike, hostility, disgust
inquisitive:curious to know
outing:a pleasure trip
ebb:decay, flow back
clamp:a device to hold things together
incurious:uninteresting, careless
bail:one who gives security 
papyrus:a seed once used to make paper
ebony:a kind of hard black wood
aegis:shield, protection
Number of entries is: 12 

EXIT EXAMPLE
--------------
Enter 1 to lookup, 2 to exit, 3 to print tables
Enter your choice: 
2
Exiting...

-----------------------------------------------------------------------------------------------------------------------
LOG FILES NAMING
----------------

Server with id=i has log file named as "Server-i.log". 
	E.g. Log file of 'node00' is named 'Server-0.log'

Client log is named: "Client.log"

DictionaryLoader log is named: "DictionaryLoader.log"

-----------------------------------------------------------------------------------------------------------------------
KNOWN BUGS:
------------------

- All features are implemented. Everyting works as expected. (No known bugs)

- For each chord-id, we are NOT using the hash of its URL. 
  Instead, we are generating a random number less than max integer value.
  We are doing this to get a uniform load ditribution of words across nodes.
-----------------------------------------------------------------------------------------------------------------------

