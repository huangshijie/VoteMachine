package org.huang.vote.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.huang.vote.model.IPInfo;

public abstract class HttpBaseService implements BaseService {
	
	private static final Logger logger = LogManager.getLogger(HttpBaseService.class);
	
	public HttpResponse doPostWithForm(String url, Map<String, String> headers, IPInfo ipInfo,
			List<NameValuePair> params) {
		
		HttpResponse result = null;
		
		try ( 
				CloseableHttpClient httpClient = HttpClients.createDefault() 
			){
			HttpPost httpPost = new HttpPost(url);
	
			if (headers != null) {
				for (String key : headers.keySet()) {
					httpPost.addHeader(key, headers.get(key));
				}
			}
	
			if (params != null) {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
	
				httpPost.setEntity(formEntity);
			}
	
			if (ipInfo != null) {
				HttpHost proxy = new HttpHost(ipInfo.getIp(), ipInfo.getPort());
				RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(10000)
						.setSocketTimeout(10000).setConnectionRequestTimeout(3000).build();
				httpPost.setConfig(requestConfig);
			}
		
			result = httpClient.execute(httpPost);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}

	// private HttpResponse doPost(String url) {
	// return null;
	// }

	public static String getHmacSHA1(String password, String loginname, String algorithm) {

		byte[] keyBytes = password.getBytes();
		Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
		Mac mac = null;
		try {
			mac = Mac.getInstance(algorithm);
			mac.init(key);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			logger.error(e.getMessage(), e);
		}
		return byteArrayToHex(mac.doFinal(loginname.getBytes()));
	}

	protected static String byteArrayToHex(byte[] a) {
		int hn, ln, cx;
		String hexDigitChars = "0123456789abcdef";
		StringBuffer buf = new StringBuffer(a.length * 2);
		for (cx = 0; cx < a.length; cx++) {
			hn = ((int) (a[cx]) & 0x00ff) / 16;
			ln = ((int) (a[cx]) & 0x000f);
			buf.append(hexDigitChars.charAt(hn));
			buf.append(hexDigitChars.charAt(ln));
		}
		return buf.toString();
	}

	public String getEntityContent(String url, String charset) {
		String jsonContent = null;

		// use get
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig config = RequestConfig.custom()
				// .setProxy(proxy)
				.setConnectTimeout(10000)// 连接超时
				.setSocketTimeout(10000)// 读取超时
				.build();
		httpGet.setConfig(config);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:63.0) Gecko/20100101 Firefox/63.0");

		// get the request's response
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		HttpEntity entity = response.getEntity();

		try {
			jsonContent = EntityUtils.toString(entity, charset);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return jsonContent;
	}
}
