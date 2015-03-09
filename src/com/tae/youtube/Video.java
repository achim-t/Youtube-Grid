package com.tae.youtube;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.json.JSONObject;

public class Video {
	private String id;
	private String channelId;
	private String title;
	private String thumbnailUrl;
	private Duration duration;
	private Date publishedAt;
	
	
	public Video(JSONObject videoJson){
		this.id = videoJson.getString("id");
		JSONObject snippet = videoJson.getJSONObject("snippet");
		snippet.getString("publishedAt"); //TODO
		this.channelId = snippet.getString("channelId");
		this.title=snippet.getString("title");
		this.thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
		String duration = videoJson.getJSONObject("contentDetails").getString("duration");
		try {
			this.duration = DatatypeFactory.newInstance().newDuration(duration);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public String getId() {
		return id;
	}


	public String getChannelId() {
		return channelId;
	}


	public String getTitle() {
		return title;
	}


	public String getThumbnailUrl() {
		return thumbnailUrl;
	}


	public Duration getDuration() {
		return duration;
	}


	public Date getPublishedAt() {
		return publishedAt;
	}
}
