package org.huang.vote.model;

import java.util.LinkedList;
import java.util.Queue;

public class IPInfoStore {
	
	private Queue<IPInfo> ipPortQueue = new LinkedList<IPInfo>();

	public Queue<IPInfo> getIpPortQueue() {
		return ipPortQueue;
	}
}
