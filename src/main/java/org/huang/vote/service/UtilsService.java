package org.huang.vote.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfo;

public class UtilsService extends HttpBaseService{
	
	private static final Logger logger = LogManager.getLogger(UtilsService.class) ;
	
	public static boolean isValidIpPort(IPInfo ipInfo) {
		
		HttpEntity entity = null;
		CloseableHttpResponse response = null;
		
		if(ipInfo != null) {
			logger.info("Start Test " + ipInfo.getIp()+ ":"+ipInfo.getPort());
			HttpGet httpGet = new HttpGet("https://www.douban.com/");
			
			try (CloseableHttpClient httpClient = HttpClients.createDefault();) {
		        HttpHost proxy = new HttpHost(ipInfo.getIp(), ipInfo.getPort());
	
		        RequestConfig config = RequestConfig
		                .custom()
		                .setProxy(proxy)
		                .setConnectTimeout(5000)
		                .setSocketTimeout(5000)
		                .build();
		        httpGet.setConfig(config);
		        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:63.0) Gecko/20100101 Firefox/63.0");

	            response = httpClient.execute(httpGet);
	        } catch (IOException e) {
	        	logger.error(e.getMessage(), e);
	            return false;
	        }
	        
	        
		}
		if(response != null) {
			
			entity = response.getEntity();
			
			if(entity != null){
	            return true;
	        }
		}
		
        return false;
	}

}