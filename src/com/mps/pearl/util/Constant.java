package com.mps.pearl.util;

/**
 * This interface represents all the constants used in the system.
 * The constant properties of the Application can be configured 
 * from this class.
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public interface Constant {

	/**
	 * Max set of Node while can be Inactive as a Cache Size 
	 */
	public static final int MAX_INACTIVE_NODE_CACHE_SIZE = 100;
	
	/**
	 * Other Nodes ( Clients Node ) ping frequency
	 */
	public static final int CLIENT_PING_EVERY_SECONDS = 5;  // seconds
	
	/**
	 * Coordinator ( Server Node ) abort time 
	 */
	public static final int SERVER_ABORT_EVERY_SECONDS = 10; // seconds
	
	/**
	 * Coordinator Backlog as a  requested maximum length of the queue 
	 * of incoming connections
	 */
	public static final int SERVER_BACKLOG = 10; 
}
