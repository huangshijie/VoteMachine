package org.huang.vote.service.producer;

import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.SpiderIPService;

public class Producer implements Runnable{

	private volatile IPInfoStore store;

	private SpiderIPService service;
	
	public IPInfoStore getStore() {
		return store;
	}

	public void setStore(IPInfoStore store) {
		this.store = store;
	}

	public SpiderIPService getService() {
		return service;
	}

	public void setService(SpiderIPService service) {
		this.service = service;
	}
	
	public Producer(IPInfoStore store, SpiderIPService service) {
		this.store = store;
		this.service = service;
	}
	
	@Override
	public void run() {

		while(true) {
			this.getService().getFreeIpInQuene();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}





	
	
}