package org.huang.vote.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfo;
import org.huang.vote.model.IPInfoStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderIPService extends HttpBaseService{
	
	private static final Logger logger = LogManager.getLogger(SpiderIPService.class) ;
	
	private volatile IPInfoStore store;
	private String url;
	private String charset;
	
	public SpiderIPService(IPInfoStore store, String url, String charset) {
		this.store = store;
		this.url = url;
		this.charset = charset;
	}
	
	public IPInfoStore getStore() {
		return store;
	}

	public void setStore(IPInfoStore store) {
		this.store = store;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void getFreeIpInQuene() {
		String content = this.getEntityContent(this.getUrl(),this.getCharset());
		Document document = Jsoup.parse(content);
		Element ip_list = document.getElementById("ip_list");
		Elements classEmpty = ip_list.select("tr");
		
		//default value
        String ip="0.0.0.0";
        String port="8888";
		
		for (Element trEle : classEmpty) {
            //logger.info(trEle.toString());
            Elements tds = trEle.select("td");

            //if it not a efficient ipPort entry
            if(tds.size()<2)    continue;
            ip = tds.get(1).text();
            port = trEle.select("td").get(2).text();
            IPInfo tempIP = new IPInfo(ip, Integer.valueOf(port));
            logger.info("Got new ipPort: " + ip + " port: "+port);
            if(store.getIpPortQueue().contains(tempIP)){
                logger.info("xiciIp: check the repeating ipPort....");
                continue;
            }

            //the synchronized to ipPort
            synchronized (store) {
                if (store.getIpPortQueue().size() >= 20) {
                    logger.info("xiciIP producer wait...");
                    try {
                    	store.wait(); // the wait must in synchronized code
                        logger.info("xiciIP producer waking...");
                    } catch (InterruptedException e) {
                    	logger.error(e.getMessage(), e);
                    }
                }
                logger.info("xiciIP: before produce "+ store.getIpPortQueue().size());
                if (store.getIpPortQueue().size() < 20) store.getIpPortQueue().add(new IPInfo(ip, Integer.valueOf(port)));// add to queue
                logger.info("xiciIP: after produce "+ store.getIpPortQueue().size()+"\n");
                store.notifyAll();
            }
        }
        logger.info("xiciIP -> IpPortQueue's size is: "+store.getIpPortQueue().size());
	}
	
	public static void main(String[] args) {
		SpiderIPService service = new SpiderIPService(new IPInfoStore(), "http://www.xicidaili.com/nn/1", "utf-8");
		
		service.getFreeIpInQuene();
	}
}