package com.mps.pearl.core;

import java.util.Collections;
import java.util.LinkedHashMap;

import com.mps.pearl.util.Logger;

/**
 * NodeManager class interact with Nodes of the System
 * and configure them to run as a Threads in a System.
 * 
 * It hold the reference to the Current Server Node in 
 * the System.
 * 
 * Server Node is an alias used for Coordinator of the System. 
 * Coordinator is a current Leader for the System consist of 
 * number of nodes.
 *   
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public class NodeManager {
	
	/**
	 * A Logger tag
	 */
	private final String TAG = NodeManager.class.getSimpleName();
	
	/*
	 * Largest ID, will be initial Coordinator (ServerNode)
	 * 
	 * Organize PING Event perform by nodes to the ServerNode Nodes ping to
	 * Server every 5 seconds
	 * 
	 * Kill the newly elected coordinator after 10 seconds
	 * 
	 * Messages::
	 * 
	 * Coordinator terminating: t i, where i is the ID of the coordinator.
	 * 
	 * Start of election: e [v], where v is a vector of IDs to which the
	 * election message is sent. This is logged by any node starting an
	 * election.
	 * 
	 * End of election: c i, where i is the ID of the new coordinator. This is
	 * logged by all nodes.
	 */

	/**
	 * A Node instance as a Server or Coordinator Node
	 */
	private Node mServerNode;
	
	/**
	 * An instance of NodeManager class
	 */
	private static NodeManager INSTANCE;
	
	/**
	 * Creates a NodeManager object
	 */
	private NodeManager(){
		// private to hold single instance
		mServerNode = null;
	}
	
	/**
	 * Returns the unique NodeManager object associated with this System, if any.
	 * @return instance the NodeManager instance
	 */
	public static synchronized NodeManager getInstance(){
		if(INSTANCE == null)
			INSTANCE = new NodeManager();
		return INSTANCE;
	}
	
	/**
	 * A LinkedHashMap<Integer, Thread> with Integer as Id of the ThreadedNode and Thread as ThreadedNode
	 */
	private LinkedHashMap<Integer, Thread> mNodeRunnableListMap;
	
	/**
	 * This method initialize the Nodes of the System and set Server Node or 
	 * the Coordinator Of the System. It perform task to ensure Nodes running in a System. 	
	 */
	public void init(){
		if(Logger.DEBUG)Logger.d(TAG, "init()");
		
		final int SYSTEM_NODES_SIZE = Resource.getInstance().getNodeList().size();
		
		// sort the list to find the highest index Node
		Collections.sort(Resource.getInstance().getNodeList());
		
		// create NodeRunnables 
		mNodeRunnableListMap = new LinkedHashMap<Integer, Thread>(SYSTEM_NODES_SIZE);
		
		for(int indx = 0 ; indx < Resource.getInstance().getNodeList().size() ; indx++ ){
			if(indx == 0 ){
				
				// add the thread to the collection
				Node node = Resource.getInstance().getNodeList().get(indx) ;
				mNodeRunnableListMap.put(node.getId(), new NodeRunnable(node));
				// 2. set Server Node
				setServerNode(Resource.getInstance().getNodeList().get(indx));
				
				// set the coordinator Flag for the Node
				this.mServerNode.setCoordinatorFlag(true);
				
				this.mServerNode.setServerListening(true);
				
				// bind server node
				bindServerNode();
				
			}else{
				// add the thread to the collection
				Node node = Resource.getInstance().getNodeList().get(indx) ;
				mNodeRunnableListMap.put(node.getId(), new NodeRunnable(node));
				// start the Runnable Node 
				mNodeRunnableListMap.get(node.getId()).start();
			}
		}
	}
	
	/**
	 * 
	 * This class represents Nodes as NodeRunnable. Each Node of the System 
	 * is represented as Thread. Node can either do ping action to Coordinator Node
	 * or can perform server task of listening to the other Nodes. 
	 * 
	 * @author msingh
	 * @version 1.0 20 Jan, 2014
	 *
	 */
	class NodeRunnable extends Thread{

		/**
		 * An instance of Node class
		 */
		private Node currentNode;
		
		/**
		 * Creates an NodeRunnable object.
		 * @param node The Node which should be bind to the Thread
		 */
		public NodeRunnable(Node node) {
			// TODO Auto-generated constructor stub
			this.currentNode = node;
		}
		
		/**
		 * This method is an entry point to run the Node in a Thread. It 
		 * perform either Server handling role or Ping coodination role.  
		 */
		@Override
		public void run() {
			if(Logger.DEBUG)Logger.d(TAG, "NodeRunnable.run() : " + currentNode + ", isCoordinatorFlag :" + currentNode.isCoordinatorFlag());
				
			if(currentNode.isCoordinatorFlag()){
				// terminate the SERVER NODE :: COORDINATOR NODE after time in seconds, Constant.SERVER_ABORT_EVERY_SECONDS
				currentNode.terminateNode();
				// play the Role as SERVER NODE :: COORDINATOR
				currentNode.serverHandler();
			}else{
				// play the Role as OTHER NODE
				// 1. ping to verify server is running
				currentNode.pingCoordinator();
			}
		}
	}
	
	/**
	 * This method bind the Node as Server Node ( or Coordinator Node) and 
	 * assign the role to the new Node as Coordinator. 
	 */
	public void  bindServerNode(){
		if(Logger.DEBUG)Logger.d(TAG, "bindServerNode() : " + mServerNode);
		if(mServerNode != null){
			Thread serverNodeThread = mNodeRunnableListMap.get(mServerNode.getId());
			if(Logger.DEBUG)Logger.d(TAG, "bindServerNode() : " + mServerNode + ", State : " + serverNodeThread.getState()  );
			
			if( serverNodeThread.getState() == Thread.State.TERMINATED ){
				// remove the mapping 
				mNodeRunnableListMap.remove(mServerNode.getId());
				// create the new thread
				serverNodeThread = new NodeRunnable(mServerNode);
				// add the new mapping
				mNodeRunnableListMap.put(mServerNode.getId(), serverNodeThread);
				// start the new thread
				serverNodeThread.start();
			}else if( serverNodeThread.getState() == Thread.State.NEW ){
				// start the new thread
				serverNodeThread.start();
			}
		}
	}
	
	/*
	 * ********************* GETTER & SETTER *************************
	 */
	
	/**
	 * Returns the current Coordinator or Server Node
	 * @return Node the Coordinator of System or Server Node
	 */
	public Node getServerNode() {
		return mServerNode;
	}

	/**
	 * Sets the Server Node or Coordinator Node of the System
	 * @param mServerNode the Server Node or Coordinator Node to bind 
	 */
	public void setServerNode(Node mServerNode) {
		if(Logger.DEBUG)Logger.d(TAG, "setServerNode() : "  + mServerNode);
		// set the value
		this.mServerNode = mServerNode;
	}
}	

