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
	public void getProfileInfo(String screenName, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		// Specify query string
		RequestParams params = new RequestParams();
		if (screenName != null) {
			apiUrl = getApiUrl("users/show.json");
			params.put("screen_name", screenName);
		}
		client.get(apiUrl, params, handler);
	}

	/**
	 * Get Mentions Timeline
	 *
	 */
	public void getMentionsTimeline(long maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Specify query string
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (maxId == 0) {
			params.put("since_id", 1);
		} else {
			params.put("max_id", maxId);
		}

		getClient().get(apiUrl, params, handler);
	}


	/**
	 * Get UsersTimeline
	 */
	public void getUserTimeline(long maxId, String screenName, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		if (maxId == 0) {
			params.put("since_id", 1);
		} else {
			params.put("max_id", maxId);
		}

		getClient().get(apiUrl, params, handler);
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


    /**
	 * getfollowers
	**/
	public void getFollowersUserList(String screenName, Long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		if(cursor != null){
			params.put("cursor", cursor);
		}
		getClient().get(apiUrl, params, handler);
	}

	/**
	 * getfriends
	 **/
	public void getFollowingUserList(String screenName, Long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		if(cursor != null){
			params.put("cursor", cursor);
		}
		getClient().get(apiUrl, params, handler);
	}


	//post favorite/unfavorite
	public void postFavorite(Long tweetId, boolean isFavorite, JsonHttpResponseHandler handler){
		String apiUrl;

		if(isFavorite){
			apiUrl = getApiUrl(String.format("favorites/destroy.json?id=%s", tweetId));
		}else{
			apiUrl = getApiUrl(String.format("favorites/create.json?id=%s", tweetId));
		}


		getClient().post(apiUrl, handler);
	}

	//post retweet/unretweet
	public void postRetweetStatus(Long retweetId, boolean isRetweet, JsonHttpResponseHandler handler){
		String apiUrl;
		if(isRetweet){
			apiUrl = getApiUrl(String.format("statuses/unretweet/%s.json", retweetId));
		}else{
			apiUrl = getApiUrl(String.format("statuses/retweet/%s.json", retweetId));
		}

		RequestParams params = new RequestParams();
		getClient().post(apiUrl, params, handler);
	}

	//search
	public void getTweetsOnSearch(long maxId, String query, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("q", query);
		params.put("result_type", "recent");
		if (maxId != 0) {
			params.put("max_id", maxId);
		}
		getClient().get(apiUrl, params, handler);
	}

	//post message
	public void postMessage(String screenName, String message, JsonHttpResponseHandler handler)
	{
		String apiUrl = getApiUrl("direct_messages/new.json");
		RequestParams params = new RequestParams();
		params.put("screen_name",screenName);
		params.put("text", message);
		getClient().post(apiUrl, params, handler);
	}

	//getmessage
	public void getMessages(long maxId, JsonHttpResponseHandler handler)
	{
		String apiUrl = getApiUrl("direct_messages/sent.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if (maxId != 0) {
			params.put("max_id", maxId);
		}
		getClient().get(apiUrl, params, handler);
	}




}