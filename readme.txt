
################### Compile and Running the Application Notes ###################

Go to Project source folder, and perform following commands to 
compile the source code.

# Linux
$ find -name "*.java" > sources.txt
$ javac @sources.txt

Run the Application Code
$ java App configuration_file.txt


################### Configuration Notes ###################

This App implements Bully Election Algorithm.

App.java file is entry point for the App. Run the program using App.java file.
Before running the source code, compile all java files into bytecode.

Configure Nodes for the App in a  configuration_file.txt file.
Add the nodes as ::
<Index> <IP Address> <Port>

eg: 12345 192.168.123.234 4457


###################  Assumption ###############################

-- All the IP address mention in the configuration_file are unique.

-- Election initiated by only single node. When the Node send message 
to other nodes, Other Node who receive message whose id is greater than 
current Node, will bully the existing Election and start a fresh Election. 
In a system, only single election is going on at any moment.

-- Server Node is an alias used for Coordinator of the System. 
Coordinator is a current Leader for the System consist of number of nodes.

-- The system used predifined set of nodes, and other nodes in a system are known.

################### ADVANCE DEBUGGING ###################

Logger.java help to perform advance Debugging and see the calls proceeding between system.

Enable the DEBUG flag to perform advance debugging of the App

public static final boolean DEBUG = true; 