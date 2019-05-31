package org.huang.vote.service.consumer;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfo;
import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.UtilsService;
import org.huang.vote.service.VoteService;
import org.json.JSONObject;

public class Consumer implements Runnable {

	private static final Logger logger = LogManager.getLogger(Consumer.class);

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

				if (this.getStore().getIpPortQueue().size() > 0) {
					logger.info("Before consume: " + this.getStore().getIpPortQueue().size());

					try {
						String result = service.doVote(ipInfo);

						if (result != null) {
							logger.info(result);
							JSONObject o = new JSONObject(result);
							int status = o.getInt("status");
							if (200 != status) {
								synchronized (this) {
									do {
										logger.info("Before Consume change ip: " + this.getStore().getIpPortQueue().size());

										if (this.getStore().getIpPortQueue().size() <= 0) {
											logger.info("IPInfo's ip store is too small");
											this.getStore().notifyAll();
											break;
										}
										this.ipInfo = this.getStore().getIpPortQueue().poll();
									} while (!UtilsService.isValidIpPort(ipInfo));
									logger.info("After consume: " + this.getStore().getIpPortQueue().size());
								}
							}
						} else {
							
							throw new RuntimeException("HTTP Base Service Error");
							
						}
					} catch (RuntimeException | IOException e) {

						logger.error(e.getMessage(), e);

						synchronized (this) {
							do {
								logger.info("Before Exception change ip: " + this.getStore().getIpPortQueue().size());

								if (this.getStore().getIpPortQueue().size() <= 0) {
									logger.info("IPInfo's ip store is too small");
									this.getStore().notifyAll();
									break;
								}

								this.ipInfo = this.getStore().getIpPortQueue().poll();
							} while (!UtilsService.isValidIpPort(ipInfo));
							logger.info("Exception After consume: " + this.getStore().getIpPortQueue().size());
						}
					}
				}
			}

			try {
				Thread.sleep(1000 * 30);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}