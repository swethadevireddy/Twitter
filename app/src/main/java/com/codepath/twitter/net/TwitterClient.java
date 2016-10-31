package com.codepath.twitter.net;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a Twitter API.s
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "a4atQB2AFs300QoWhExI8HXAT";
	public static final String REST_CONSUMER_SECRET = "SLSfyD9Svr0rDcyIwcv7bkbVeMpxy0yM9NJOGJmWBFNUFcVwsR";
	public static final String REST_CALLBACK_URL = "oauth://twitter";
	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}


	//HomeTimeline - Get Home Timeline
	/*https://api.twitter.com/1.1/statuses/home_timeline.json
	count:25
	since_id :12345*/
	public void getHomeTimeLine(long maxId, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		if(maxId == 0){
		   params.put("since_id", "1");
		}else{
		   params.put("max_id", maxId);
		}

		client.get(apiUrl, params, handler);
	}

	/**
	 * to get the profile of logged in user
	 * @param handler
     */
	public void getProfileInfo(JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, handler);
	}

	/**
	 * Post Tweet
	 * @param handler
	 */
	public void postTweet(JsonHttpResponseHandler handler, String status, Long inReplyToId) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		if(inReplyToId != null){
			params.put("in_reply_to_status_id", inReplyToId);
		}
		getClient().post(apiUrl, params, handler);
	}

}