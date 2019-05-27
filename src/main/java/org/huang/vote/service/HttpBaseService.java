package org.huang.vote.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.huang.vote.model.IPInfo;

public abstract class HttpBaseService implements BaseService {
	
	public HttpResponse doPostWithForm (String url, Map<String, String> headers, IPInfo ipInfo, List<NameValuePair> params) {
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(url);
		
		try {
			
			if(headers != null ) {
				for(String key : headers.keySet()) {
					httpPost.addHeader(key, headers.get(key));
				}
			}
			
			if(params != null) {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
				
				httpPost.setEntity(formEntity);
			}
			
			if(ipInfo != null) {
				HttpHost proxy = new HttpHost(ipInfo.getIp(), ipInfo.getPort());
				RequestConfig requestConfig = RequestConfig.custom()
		                .setProxy(proxy)
		                .setConnectTimeout(10000)
		                .setSocketTimeout(10000)
		                .setConnectionRequestTimeout(3000)
		                .build();
		        httpPost.setConfig(requestConfig);
			}
			
			return httpClient.execute(httpPost);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
//	private HttpResponse doPost(String url) {
//		return null;
//	}
	
	
	public static String getHmacSHA1(String password,String loginname, String algorithm){
		
        byte[] keyBytes = password.getBytes();
        Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm);
        Mac mac = null;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return byteArrayToHex(mac.doFinal(loginname.getBytes()));
    }

	protected static String byteArrayToHex(byte [] a) {
        int hn, ln, cx;
        String hexDigitChars = "0123456789abcdef";
        StringBuffer buf = new StringBuffer(a.length * 2);
        for(cx = 0; cx < a.length; cx++) {
            hn = ((int)(a[cx]) & 0x00ff) /16 ;
            ln = ((int)(a[cx]) & 0x000f);
            buf.append(hexDigitChars.charAt(hn));
            buf.append(hexDigitChars.charAt(ln));
        }
        return buf.toString();
    }
	
}
