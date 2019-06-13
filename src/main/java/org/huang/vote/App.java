package org.huang.vote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.SpiderIPService;
import org.huang.vote.service.VoteService;
import org.huang.vote.service.consumer.Consumer;
import org.huang.vote.service.producer.Producer;

public class App {
	
	private static final Logger logger = LogManager.getLogger(App.class) ;
	
    public static void main( String[] args ) {
    	
    	logger.info("Start App");
    	IPInfoStore store = new IPInfoStore();
    	
    	SpiderIPService sService = new SpiderIPService(store, "http://www.xicidaili.com/nn/1", "utf-8");
    	VoteService vService = new VoteService();
    	
    	Consumer consumer = new Consumer(vService, store);
    	Thread conThread = new Thread(consumer, "Consumer-1");
    	conThread.start();
    	
    	Consumer consumer2 = new Consumer(vService, store);
    	Thread conThread2 = new Thread(consumer2, "Consumer-2");
    	conThread2.start();
    	
    	Producer producer = new Producer(sService);
    	Thread proThread = new Thread(producer, "Producer");
    	proThread.start();
    	
    }
}
