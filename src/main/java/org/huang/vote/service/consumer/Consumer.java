package org.huang.vote.service.consumer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfo;
import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.VoteService;
import org.json.JSONObject;

public class Consumer implements Runnable {

	private static final Logger logger = LogManager.getLogger(Consumer.class);
	
	private static AtomicInteger SUCCESSNUM = new AtomicInteger(0);

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
		while (true) {
			
			this.ipInfo = this.getStore().getIpPortQueue().poll();

			synchronized (this.getStore()) {
				if (this.getStore().getIpPortQueue().size() == 0) {

					try {
						logger.info("Consumer wait...");
						this.getStore().wait();
						logger.info("Consumer waking...");
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
				
				this.ipInfo = this.getStore().getIpPortQueue().poll();
			}

			if (this.ipInfo != null) {
				while(true) {
					logger.info("Before consume: " + this.getStore().getIpPortQueue().size());
					logger.info("Use ip port info: " + this.ipInfo.toString());

					try {
						String result = service.doVote(ipInfo);

						if (result != null) {
							logger.info(result);
							JSONObject o = new JSONObject(result);
							int status = o.getInt("status");
							if (200 != status) {
								logger.info("This ip port info cannot be used any more: " + this.ipInfo.toString());
								break;
							} else {
								logger.fatal("Seccuss vote and current total number is " + SUCCESSNUM.incrementAndGet());
							}
						} else {
							throw new RuntimeException("HTTP Base Service Error");
						}
					} catch (RuntimeException | IOException e ) {
						logger.info("While use this ip port info face exception: " + this.ipInfo.toString());
						logger.error(e.getMessage(), e);
						break;
					}
				}
			}

			try {
				Thread.sleep(1000 * 15);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}