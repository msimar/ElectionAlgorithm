package com.mps.pearl.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.mps.pearl.util.Constant;
import com.mps.pearl.util.Logger;

/**
 * This class represents Trait of the Node as Server or Client. The  
 * abstract class implements roles of Server or Client. 
 * 
 * The Server ( or Coordinator ) trait enable handling of Client messages. 
 * The Server can handle multiple clients at the same time. The Server respond  
 * to each client using its state as message. If the Server is not responded, 
 * then the Server is terminated and new Server need to elected from the 
 * set of other nodes in a System.
 * 
 * The Client trait enable ping coordination to the current active Server Node.
 * The Client can perform election, if the client able to locate Server state 
 * as terminated.  
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public abstract class TraitClientServer {
	
	/**
	 * Logger tag constant
	 */
	private final String TAG = TraitClientServer.class.getSimpleName();
	
	/**
	 * String IP Address of the Node
	 */
	protected String IPAddress;

	/**
	 * String Port Address of the Node
	 */
	protected String port;
	
	/**
	 * Boolean to hold the listening state of the Server 
	 */
	protected boolean isServerListening = true;
	
	/**
	 * Returns the IP Address of the Node
	 * @return the String IP Address
	 */
	public String getIPAddress() {
		return IPAddress;
	}

	/**
	 * Set the IP Address of the Node
	 * @param ipAddress the String IP Address
	 */
	public void setIPAddress(String ipAddress) {
		IPAddress = ipAddress;
	}

	/**
	 * Returns the Port Address of the Node
	 * @return the String Port Address
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Set the Port Address of the Node
	 * @param port the String Port Address
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * Returns the state of the Server Node
	 * @return the Boolean State of the Server Node
	 */
	public boolean isServerListening() {
		return isServerListening;
	}

	/**
	 * Set the server listening state of the Node
	 * @param isServerListening the Boolean state of server
	 */
	public void setServerListening(boolean isServerListening) {
		this.isServerListening = isServerListening;
	}
	
	/**
	 * This method perform ping operation to the Server Node
	 */
	protected void pingServerNode() {
		if(Logger.DEBUG)Logger.d(TAG, "pingServerNode()");
		
		int portNumber = Integer.parseInt(NodeManager.getInstance().getServerNode().getPort());

		// Open Socket to Ping ServerNode
		if(Logger.DEBUG)Logger.d(TAG, "pingServerNode() : Opening Client Socket to Ping Server [" + NodeManager.getInstance().getServerNode().getIPAddress() + ":" + portNumber + "]" );
		try  {
				Socket clientSocket  = new Socket(NodeManager.getInstance().getServerNode().getIPAddress(), portNumber);
			  

			// Create Thread for Message Exchange between Server and Client
			new MessageClientThread(clientSocket).start();  
			
		} catch (UnknownHostException e) {
			if(Logger.DEBUG)Logger.e(TAG, "Don't know about host " + NodeManager.getInstance().getServerNode());
			System.exit(1);
		} catch (IOException e) {
			if(Logger.DEBUG)Logger.e(TAG, "Couldn't get I/O for the connection to " + NodeManager.getInstance().getServerNode());
			if(Logger.DEBUG)
				e.printStackTrace();
		}
	}
	
	/**
	 * This class represents a Message Client. Each Client act as 
	 * a message tunnel to communicate and perform a link between 
	 * Server and Client. Message Client is organize as a Thread.
	 * 
	 * @author msingh
	 * @version 1.0 20 Jan, 2014
	 * 
	 */
    class  MessageClientThread extends Thread {
    	/**
    	 * Logger tag constant
    	 */
    	private final String TAG = MessageClientThread.class.getSimpleName();
    	
    	/**
    	 * Socket instance to open channel 
    	 */
    	private Socket clientSocket = null;
    	
    	/**
    	 * DataInputStream instance to read console
    	 */
    	private DataInputStream  console   = null;
    	
    	/**
    	 * DataOutputStream instance to write to stream
    	 */
    	private DataOutputStream streamOut = null;

    	/**
    	 * String message to ping Server Node
    	 */
    	private final String PING_MESSAGE = "Is Active?" ;

    	/**
    	 * Creates an MessageClientThread object.
    	 * @param socket the Client Socket which should be bind to the Thread
    	 * @throws IOException 
    	 */
	    public MessageClientThread(Socket socket) throws IOException{
	        super("MessageClientThread");
	        this.clientSocket = socket;
	        if(Logger.DEBUG)Logger.d(TAG, "MessageClientThread() : ClientSocket : " + clientSocket);
	    }
	    
	    /**
	     * This method as entry point for MessageClientThread class. It perform Server 
	     * communication while reading and writing to the stream.
	     */
        public void run() {
        	if(Logger.DEBUG)Logger.d(TAG, "MessageClientThread.run() : isConnected() : " + clientSocket.isConnected()  + ", isClosed() : " + clientSocket.isClosed()); 
        	
        	if(clientSocket.isConnected() && !clientSocket.isClosed()){
        		
        		try {
        			// open input and output stream
        			openStream();
        			
                	String inputLine;
                	
                	// Send message to the Server
                	streamOut.writeUTF(PING_MESSAGE);
                    streamOut.flush();
                    
                    // Reader to read Server message
     	            while ((inputLine = console.readUTF()) != null) {
    	            	if(Logger.DEBUG)Logger.d(TAG, "Message from Server: " + inputLine);
    	            	break;
    				}
     	            // close streams and established connection
     	            closeConn();
                } catch (IOException e) {
                	// java.net.SocketException: Socket is closed
    	            if(Logger.DEBUG)
    	            	e.printStackTrace();
    	        } 
        	}
        }
        
        /**
         * This method open an DataInputStream and DataOutputStream
         * 
         * @throws IOException
         */
        public void openStream() throws IOException
        {  console   = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
           streamOut = new DataOutputStream(clientSocket.getOutputStream());
        }
        
        /**
         * This method close the open connections and the streams used for 
         * communication.
         */
        public void closeConn()
        {  try
           {  if (console   != null)  console.close();
              if (streamOut != null)  streamOut.close();
              if (clientSocket    != null)  clientSocket.close();
           }
           catch(IOException ioe)
           {  System.out.println("Error closing ...");
           }
        }
    }
     
    /**
     * Vector<Thread> to hold communicate accepted Thread Sockets by Server
     */
    private Vector<Thread> serverSocketThreadList;
    
    /**
     * Returns the List of Server Socket Thread List
     * @return the Vector<Thread> list 
     */
    protected Vector<Thread> getServerSocketThreadList(){
    	return serverSocketThreadList;
    }
	
    /**
     * This method represents a Server Handler to execute Server operations. 
     * It opens the ServerSocket and communicate with the Clients by accepts 
     * requests from the Clients.
     */
	public void serverHandler() {
		
		// create a new empty list
		serverSocketThreadList = new  Vector<Thread>();
		
		int portNumber = Integer.parseInt(port);
		
		if(Logger.DEBUG)Logger.d(TAG, "serverHandler() : Opening ServerSocket [" + NodeManager.getInstance().getServerNode().getIPAddress() + ":" + portNumber + "]" );

		try {  
				ServerSocket serverSocket = new ServerSocket(portNumber, Constant.SERVER_BACKLOG, 
						InetAddress.getByName(NodeManager.getInstance().getServerNode().getIPAddress()));
				
				//if(TestStub.DEBUG)
//			ServerSocket serverSocket = new ServerSocket(portNumber, Constant.SERVER_BACKLOG, InetAddress.getByName(null));
				
			 
			if(Logger.DEBUG)Logger.d(TAG, "serverHandler() : ServerSocket started [" + NodeManager.getInstance().getServerNode().getIPAddress() + ":" + portNumber + "] : isBounded() : " + serverSocket.isBound() );

			while (isServerListening) {
				// Connect Different Clients to Server
				Thread sThread = new MultiClientHandlerThread(serverSocket.accept());
				serverSocketThreadList.add(sThread);
				sThread.start();
	        }
		} catch (IOException e) {
			if(Logger.DEBUG)Logger.e(TAG, "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			if(Logger.DEBUG)Logger.d(TAG, e.getMessage());
		}
	}
	
	/**
	 * This class enable Server to listen to the multiple clients 
	 * at the same time. The Server Socket accepts the Client Sockets
	 * and each Client Socket execute in MultiClientHandlerThread class.
	 * The Server communicate with the Clients by using input and output 
	 * streams.
	 * 
	 * @author msingh
	 * @version 1.0 20 Jan, 2014
	 *
	 */
	class MultiClientHandlerThread extends Thread {
		
		/**
		 * Logger tag constant
		 */
		private final String TAG = MultiClientHandlerThread.class.getSimpleName();
		
		/**
		 * Server bound Client Socket instance
		 */
	    private Socket serverBoundSocket = null;
	    
	    /**
    	 * DataInputStream instance to read console
    	 */
	    private DataInputStream  console   = null;
	    
	    /**
    	 * DataOutputStream instance to write to stream
    	 */
    	private DataOutputStream streamOut = null;
    	
//    	private volatile Thread blinker;

    	/**
    	 * Creates an MultiClientHandlerThread object.
    	 * 
    	 * @param socket the Client Socket which should be bind to the Thread
    	 * @throws IOException
    	 */
	    public MultiClientHandlerThread(Socket socket) throws IOException{
	        super("MultiServerThread");
	        this.serverBoundSocket = socket;
	        if(Logger.DEBUG)Logger.d(TAG, "MultiClientHandlerThread : Client accepted : " + serverBoundSocket);
	    }
	    
	    /**
	     * This method as entry point for MultiClientHandlerThread class. It perform Client 
	     * communication while reading and writing to the stream.
	     */
	    public void run() {
	    	
	    	// way to stop the thread
//	    	Thread thisThread = Thread.currentThread();
//	    	while (blinker == thisThread) {
//	    		// do processing
//	    	}
	    	
	    	if (Thread.currentThread().isInterrupted()) {
	    		  // cleanup and stop execution
	    		if(Logger.DEBUG)Logger.d(TAG, "MultiClientHandlerThread.run() : Thread Interrupted");
	    		// close the connection
	    		closeConn();
	    		return;
	    	}
	    	
	    	if(Logger.DEBUG)Logger.d(TAG, "MultiClientHandlerThread.run() ");
	    	
	        try { 
	        	// open input and output stream
    			openStream();
    			
	            String inputLine;

                // Reader to read Client message
	            while ((inputLine = console.readUTF()) != "") {
	            	if(Logger.DEBUG)Logger.d(TAG, "Client Says : " + inputLine  );  
	            	break;
				}
	            
	            // Send message to the Client
	            streamOut.writeUTF("SERVER: ACTIVE " + "[" + IPAddress + ":" + port + "]"  );
                streamOut.flush();
                
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
         * This method open an DataInputStream and DataOutputStream
         * 
         * @throws IOException
         */
	    public void openStream() throws IOException
        {  console   = new DataInputStream(new BufferedInputStream(serverBoundSocket.getInputStream()));
           streamOut = new DataOutputStream(serverBoundSocket.getOutputStream());
        }
        
	    /**
         * This method close the open connections and the streams used for 
         * communication.
         */
        public void closeConn()
        {  try
           {  if (console   != null)  console.close();
              if (streamOut != null)  streamOut.close();
              if (serverBoundSocket    != null)  serverBoundSocket.close();
              
//              blinker = null;
           }
           catch(IOException ioe)
           {  System.out.println("Error closing ...");
           }
        }
	}
}
