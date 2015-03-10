package com.tae.youtube;
import java.io.Serializable;

import org.json.JSONObject;

public class Channel implements Serializable{

	private String thumbnailUrl;
	private String title;
	private String channelId;

	public Channel(JSONObject channelJson) {
		JSONObject channelSnippet = channelJson.getJSONObject("snippet");
		title = channelSnippet.getString("title");
		thumbnailUrl = channelSnippet.getJSONObject("thumbnails")
				.getJSONObject("default").getString("url");
		channelId = channelSnippet.getJSONObject("resourceId").getString(
				"channelId");
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getChannelId() {
		return channelId;
	}
	
	

}
