package org.huang.vote;

import org.huang.vote.model.IPInfoStore;
import org.huang.vote.service.SpiderIPService;
import org.huang.vote.service.VoteService;
import org.huang.vote.service.consumer.Consumer;
import org.huang.vote.service.producer.Producer;

public class App {
    public static void main( String[] args ) {
    	IPInfoStore store = new IPInfoStore();
    	
    	SpiderIPService sService = new SpiderIPService(store, "http://www.xicidaili.com/nn/1", "utf-8");
    	VoteService vService = new VoteService();
    	
    	Consumer consumer = new Consumer(vService, store);
    	Thread conThread = new Thread(consumer);
    	conThread.start();
    	
    	Producer producer = new Producer(sService);
    	Thread proThread = new Thread(producer);
    	proThread.start();
    	
    }
}
