#!/usr/bin/env python3

import os
import sys
import socket
import subprocess

def runServer(numClients):
    os.system("javac RMIBankServerImpl.java")

    serverInfo = {}
    with open("./configFile.txt") as f:
        content = f.readlines()
    content = content[1:]
    for line in content:
        [hostname, sId, rmiPort] = line.split(" ")
        if hostname not in serverInfo:
            serverInfo[hostname] = []
        serverInfo[hostname].append({'sId': sId, 'rmiPort':rmiPort})

    currentName = socket.gethostname()
    once = True
    for server in serverInfo[currentName]:
        if once:
            # os.system()
            subprocess.Popen("rmiregistry {}".format(server['rmiPort']), shell=True)
            once = False
        # os.system("java RMIBankServerImpl {} configFile.txt {}".format(server['sId'], numClients))
        subprocess.Popen("java RMIBankServerImpl {} configFile.txt {}".format(server['sId'], numClients), shell=True)

def runClient(clientId, numThreads=24):
    os.system("javac RMIClient.java")
    subprocess.Popen("java RMIClient {} {} configFile.txt".format(clientId, numThreads), shell=True)

if __name__ == '__main__':
    if len(sys.argv) < 3:
        raise ValueError("Correct usage is: 'python3 run.py SRV <instance number>', 'python 3 run.py DICT' or 'python3 run.py CLNT localhost:<port-number>/node00 <numThreads>'")

    if sys.argv[1] == 'SRV':
        if len(sys.argv) != 3:
            raise ValueError("Correct usage is: 'python3 run.py SRV <numClients>'")
        numClients = sys.argv[2]
        runServer(numClients)

    if sys.argv[1] == 'DICT': 
        if len(sys.argv) != 3:
            raise ValueError("Correct usage is: 'python3 run.py DICT <numClients>'")
        numClients = sys.argv[2]
        runServer(numClients)

    elif sys.argv[1] == 'CLNT':
        if len(sys.argv) != 4:
            raise ValueError("Correct usage is: 'python3 run.py CLNT <clientId> <numThreads>'")
        clientId = sys.argv[2]
        numThreads = sys.argv[3]
        runClient(clientId, numThreads)
    else:
        raise ValueError("Correct usage is: 'python3 run.py SRV <numClients>' or 'python3 run.py CLNT <clientId> <numThreads>'")
