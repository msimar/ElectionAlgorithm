package com.mps.pearl.core;

import java.util.ArrayList;
import java.util.List;

import com.mps.pearl.util.Logger;

/**
 * This class act as Resource Pool for the system. It
 * configure the Nodes read from the text file. It hold List of 
 * Nodes present in the system.
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public class Resource {
	
	/**
	 * A Logger tag
	 */
	private final String TAG = Resource.class.getSimpleName();
	
	/**
	 * A List<Node to hold all the nodes in a System
	 */
	private List<Node> mNodeList;
	
	/**
	 * A List<Node to hold all the terminated nodes in a System
	 */
	private List<Node> mTerminatedNodeList;
	
	/**
	 * An Instance of Resource class 
	 */
	private static Resource INSTANCE;

	/**
	 * A Node with Highest Index Id
	 */
	private Node mHighNode;
	
	/**
	 * Creates a Resource object.
	 */
	private Resource(){
		// private to hold single instance
		mNodeList = new ArrayList<Node>(); 
		mTerminatedNodeList = new ArrayList<Node>();
		mHighNode = null;
	}
	
	/**
	 * Returns the unique Resource object associated with this System, if any.
	 * @return the instance of Resource
	 */
	public static synchronized Resource getInstance(){
		if(INSTANCE == null)
			INSTANCE = new Resource();
		return INSTANCE;
	}

	/**
	 * Returns the List of Nodes in a System  
	 * @return the List of Nodes
	 */
	public List<Node> getNodeList() {
		return mNodeList;
	}
	
	/**
	 * Returns the terminated List of Nodes in a System  
	 * @return the terminated List of Nodes
	 */
	public List<Node> getTerminatedNodeList() {
		return mNodeList;
	}
	
	/**
	 * Returns True, if the Node is successfully added to the terminated Node list. 
	 * False, if the Node is not able to added into terminated list.
	 *  
	 * @param terminatedNode the Node to be added into terminated Node list 
	 * @return the state of added Node into terminated list. 
	 */
	public boolean addTerminatedNode(Node terminatedNode){
		// remove the node from the main list
		boolean removedNodeStatus = mNodeList.remove(terminatedNode) ;
		if(Logger.DEBUG)Logger.d(TAG, "addTerminatedNode() Node removed from main list : [" + terminatedNode + "]"  + removedNodeStatus);
		if(Logger.DEBUG)Logger.d(TAG, "addTerminatedNode() : Size Node List " + mNodeList.size());
		
		return terminatedNode == null ? false : mTerminatedNodeList.add(terminatedNode);
	}
	
	/**
	 * Returns True, if the Node is successfully added, otherwise false.
	 * @param node the Node to add into Resource Node list
	 * @return True, if the Node is successfully added, otherwise false.
	 */
	public boolean add(Node node) {
		if(mHighNode == null)
			mHighNode = node;
		else{
			if( mHighNode.getId() < node.getId() ){
				mHighNode = node;
			}
		}
		return node == null ? false : mNodeList.add(node);
	}
}
