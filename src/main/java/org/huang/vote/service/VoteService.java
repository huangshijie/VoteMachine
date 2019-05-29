package org.huang.vote.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.huang.vote.model.IPInfo;

public class VoteService extends HttpBaseService{

	private static final String SECRET_ID = "9dBuznzAEMCA2x78";
	private static final String SECRET_CODE = "EGwGg1IuZv0ekM04PBN27Q1C9xs34O";
	
	private static final String ENCODE_METHOD = "HmacSHA1";
	
	private static final String VOTE_DATA = "320000_5ce14aa4ba093d4053485392";
	
	public HttpResponse doVote(IPInfo ipInfo) throws ParseException, IOException {
		
	    String accept = "*/*";
	    String baseUrl = "http://op.lottery.gov.cn/";
	    String path = "dreamer/dreamerIncrease";
	    long ts = Math.round(Math.random() * 1e3);
	    String contentType = "application/x-www-form-urlencoded";
	    String timestamp = String.valueOf(Math.round((new Date()).getTime() / 1e3));
	    String str = "POST\n" + accept + "\n" + contentType + "\n" + timestamp + "\n" + timestamp + ts + "\n/" + path;
	    String encrypted = getHmacSHA1(SECRET_CODE, str, ENCODE_METHOD); 
	    String authHeader = "Dataplus " + SECRET_ID + ":" + encrypted;
	    String url = baseUrl + path + "?v=" + (new Date()).getTime();
	    
	    HashMap<String, String> headers = new HashMap<>();
	    
	    headers.put("Content-Type", "application/x-www-form-urlencoded");
	    headers.put("accept", accept);
	    headers.put("authorization", authHeader);
	    headers.put("nonce", timestamp + ts);
	    headers.put("timestamp", timestamp);
	    
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair(VOTE_DATA, VOTE_DATA));
	    
	    return doPostWithForm(url, headers, ipInfo, params);
	}
	
//	public static void main(String[] args) {
//		VoteService service = new VoteService();
//		
//		while(true) {
//			try {
//				HttpResponse response = service.doVote();
//				
//				if (response != null){
//		            HttpEntity entity = response.getEntity();  //获取返回实体
//		            if (entity != null){
//		            	String result = EntityUtils.toString(entity,"utf-8");
//		            	JSONObject o = new JSONObject(result);
//		            	int status = o.getInt("status");
////		            	if(200 != status) {
////		            		break;
////		            	}
//		                System.out.println(result);
//		            }
//		        }
//				
//				Thread.sleep(1000*30);
//			} catch (ParseException | IOException | InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
//	}
}
