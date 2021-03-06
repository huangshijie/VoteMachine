package org.huang.vote.model;

public class IPInfo {
	
	private String ip;
	
	private int port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public IPInfo(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	public String toString(){
		return this.ip + ":" + this.port;
	}

}
