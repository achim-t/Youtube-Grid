package com.tae.youtube;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.json.JSONException;
import org.json.JSONObject;

public class Video  implements Comparable<Video>, Serializable{
	private String id;
	private String channelId;
	private String title;
	private String thumbnailUrl;
	private Duration duration;
	private Date publishedAt;
	
	
	public Video(JSONObject videoJson){
		this.id = videoJson.getString("id");
		JSONObject snippet = videoJson.getJSONObject("snippet");
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			publishedAt = simpleFormat.parse(snippet.getString("publishedAt"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //TODO
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


	@Override
	public int compareTo(Video other) {
		
		return -1 * this.getPublishedAt().compareTo(other.getPublishedAt());
	}
}
