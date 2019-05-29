package org.huang.vote.service.consumer;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.huang.vote.model.IPInfo;
import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.UtilsService;
import org.huang.vote.service.VoteService;
import org.json.JSONObject;

public class Consumer implements Runnable{
	
	private VoteService service;
	
	private volatile IPInfoStore store;
	
	private IPInfo ipInfo;
	
	public VoteService getService() {
		return service;
	}

	public void setService(VoteService service) {
		this.service = service;
	}

	public IPInfoStore getStore() {
		return store;
	}

	public void setStore(IPInfoStore store) {
		this.store = store;
	}
	
	public Consumer(VoteService service, IPInfoStore store) {
		this.service = service;
		this.store = store;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			
			synchronized(this.getStore()) {
				if(this.getStore().getIpPortQueue().size() == 0) {
					
					try {
						System.out.println("Consumer wait...");
						this.getStore().wait();
						System.out.println("Consumer waking...");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if( this.ipInfo == null ){
					synchronized(this) {
		    			do {
		    				this.ipInfo = this.getStore().getIpPortQueue().poll();
		    			} while (!UtilsService.isValidIpPort(ipInfo));
		    			System.out.println("Current IpInfo is null After consume: "+ this.getStore().getIpPortQueue().size());
		    		}
				}
				
				if(this.getStore().getIpPortQueue().size() > 0) {
					System.out.println("Before consume: "+ this.getStore().getIpPortQueue().size());
					
					if(this.getStore().getIpPortQueue().size() < 10 ) this.getStore().notifyAll();
					
					try {
						HttpResponse response = service.doVote(ipInfo);

						if (response != null){
						    HttpEntity entity = response.getEntity();  //获取返回实体
						    if (entity != null){
						    	String result = EntityUtils.toString(entity,"utf-8");
						    	JSONObject o = new JSONObject(result);
						    	int status = o.getInt("status");
						    	if(200 != status) {
						    		synchronized(this) {
						    			do {
						    				this.ipInfo = this.getStore().getIpPortQueue().poll();
						    			} while (! UtilsService.isValidIpPort(ipInfo));
						    			System.out.println("After consume: "+ this.getStore().getIpPortQueue().size());
						    		}
						    	}
						        System.out.println(result);
						    }
						}
					} catch (ParseException | IOException e) {

						synchronized(this) {
			    			do {
			    				this.ipInfo = this.getStore().getIpPortQueue().poll();
			    			} while (! UtilsService.isValidIpPort(ipInfo));
			    			System.out.println("Exception After consume: "+ this.getStore().getIpPortQueue().size());
			    		}
						
						e.printStackTrace();
					}
				}
				
			}
			
			try {
                Thread.sleep(1000*15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		} 
	}

}