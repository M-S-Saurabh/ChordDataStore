#!/usr/bin/env python3

import os
import sys
import socket
import subprocess
import time

def runServer(portNum, numOfNodes):
    subprocess.Popen("rmiregistry "+portNum, shell=True)
    for i in range(0, int(numOfNodes)):
        subprocess.Popen("java Server "+str(i), shell=True)
        time.sleep(1) #adding sleep so that the order is maintained

def runDictionary(portNum):
    os.system("java DictionaryLoader localhost:"+portNum+"/node00 ./sample-dictionary-file.txt")
    
def runClient(portNum, numOfNodes):
    os.system("java Client localhost:"+portNum+"/node00 "+numOfNodes)

if __name__ == '__main__':

    if len(sys.argv) < 3:
        raise ValueError("Correct usage is: 'python3 run.py SRV <port-number> <num-of-nodes>', 'python 3 run.py DICT <port-number>' or 'python3 run.py CLNT <port-number> <num-of-nodes>")
 
            
    if sys.argv[1] == 'SRV':
        if len(sys.argv) != 4:
            raise ValueError("Correct usage is: 'python3 run.py SRV <port-number> <num-of-nodes>'")
        portNum = sys.argv[2]
        numOfNodes = sys.argv[3]
        runServer(portNum, numOfNodes)

    elif sys.argv[1] == 'DICT': 
        if len(sys.argv) != 3:
            raise ValueError("Correct usage is: 'python 3 run.py DICT <port-number>'")
        portNum = sys.argv[2]
        runDictionary(portNum)

    elif sys.argv[1] == 'CLNT':
        if len(sys.argv) != 4:
            raise ValueError("Correct usage is: 'python3 run.py CLNT <port-number> <num-of-nodes>'")
        portNum = sys.argv[2]
        numOfNodes = sys.argv[3]
        runClient(portNum, numOfNodes)
    else:
        raise ValueError("Correct usage is: 'python3 run.py SRV <port-number> <num-of-nodes>', 'python 3 run.py DICT <port-number>' or 'python3 run.py CLNT <port-number> <num-of-nodes>")
