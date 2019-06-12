package org.huang.vote.model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class IPInfoStore {
	
	private BlockingQueue<IPInfo> ipPortQueue = new ArrayBlockingQueue<IPInfo>(50);

	public BlockingQueue<IPInfo> getIpPortQueue() {
		return ipPortQueue;
	}
}