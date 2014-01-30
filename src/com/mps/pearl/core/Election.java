package com.mps.pearl.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class implements an Election. Election is a process to select 
 * a Coordinator Node ( Server Node ) among set of available Nodes in 
 * a System.  
 *  
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */
public class Election {

	/**
	 * A Logger tag
	 */
	@SuppressWarnings("unused")
	private final String TAG = Election.class.getSimpleName();
	
	/**
	 * An instance of Election class
	 */
	private static Election INSTANCE;
	
	/**
	 * Boolean to indicates Election state 
	 */
	private boolean isElectionFlag;
	
	/**
	 * LinkedHashMap<Integer,Node> with Id as Key and Node as value 
	 * for the System 
	 */
	private LinkedHashMap<Integer,Node> mElectionNodeList;
	
	/**
	 * Object used as lock to provide mutual exclusion for the 
	 * running threads in a System
	 */
	public static final Object lock = new Object();
	
	/**
	 * Returns the unique Election object associated with this System, if any.
	 * @return instance the Election instance
	 */
	public static synchronized Election getInstance(){
		if(INSTANCE == null)
			INSTANCE = new Election();
		return INSTANCE;
	}

	/**
	 * Creates an Election object.
	 */
	private Election() {
		// TODO Auto-generated constructor stub
		setElectionFlag(false);
		mElectionNodeList = new LinkedHashMap<Integer,Node>();
	}

	/**
	 * Returns the List of Nodes which are involved in the Election process in a System
	 * @return the list which is bound to the Election, or null if the list is unbound.
	 */
	public LinkedHashMap<Integer,Node> getElectionNodeList() {
		return mElectionNodeList;
	}

	/**
	 * Begin Election process in a System
	 */
	public void beginElection(){
		// set election state
		setElectionFlag(true);
	}
	
	/**
	 * Returns the valid state of the Election in a System. The Election is valid, 
	 * if the Id of Node which initiated the Election is greater than all the other nodes
	 * 
	 * @param indexId the Id of the Node who wants to vaildate the state of election
	 * @return True, if the election is valid election, False, 
	 * If election is not a valid election
	 */
	public boolean isValidElection(int indexId){
		if(mElectionNodeList.size() == 0 ) return true;
		return mElectionNodeList.entrySet().iterator().next().getKey() >= indexId ;
	}
	
	/**
	 * Add the current Node to the Election List
	 * @param node The Node to be added in a Election Node List
	 */
	public void addElectionNode(Node node){
		// add the node to the list
		mElectionNodeList.put(node.getId(), node);
	}
	
	/**
	 * Abort Election process in a System
	 */
	public void abortElection(){
		// set election state 
		setElectionFlag(false);
		// clear all node list from election
		mElectionNodeList.clear();
	}
	
	/**
	 * Returns the Election message which contains vector of Nodes 
	 * who are involved in an Election 
	 * 
	 * @return Message A String of the vector Nodes of the Election
	 */
	public synchronized String getElectionMessage(){
		StringBuilder message = new StringBuilder();
		
		message.append("e ").append("[");;
			
		int size = mElectionNodeList.size();
		
		Iterator<Entry<Integer, Node>> iter = mElectionNodeList.entrySet().iterator();
	      // Display elements
	      while(iter.hasNext()) {
	         Map.Entry<Integer,Node> iterMap = (Map.Entry<Integer,Node>)iter.next();
	         message.append(iterMap.getKey());
	         
	         if( size - 1 >= 1 ){
	        	 message.append(", ");
	        	 size--;
	         }
	      }
		
		message.append("]");;
		
		return message.toString();
	}

	/**
	 * Close the Election and declare the new coordinator
	 * 
	 * @return Node The Node declared as new coordinator
	 */
	public Node endElection(){
		// end election 
		String message = "c " + mElectionNodeList.entrySet().iterator().next().getKey() ;
		// print to view
		System.out.println(message);
		
		// Get the new Coordinator
		Node newCoordinator = mElectionNodeList.entrySet().iterator().next().getValue();
		
		// set election state 
		setElectionFlag(false);
				
		// clear all node list from election
		mElectionNodeList.clear();
		
		// return the Node as a NEW  COORDINATOR
		return newCoordinator ;
	}

	/**
	 * Returns the state of the Election
	 * 
	 * @return True, if Election is running, 
	 * False, if Election is not running
	 */
	public boolean isElectionFlag() {
		return isElectionFlag;
	}

	/**
	 * Set the Election State of the System
	 * 
	 * @param isElectionFlag True, if the Election is running, False, if Election not running
	 */
	public void setElectionFlag(boolean isElectionFlag) {
		this.isElectionFlag = isElectionFlag;
	}
}
