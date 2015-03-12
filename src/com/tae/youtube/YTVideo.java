package com.tae.youtube;

import java.io.Serializable;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

public class YTVideo  implements Comparable<YTVideo>, Serializable{
	private String id;
	private String channelId;
	private String title;
	private String thumbnailUrl;
	private String duration;
	private DateTime publishedAt;
	
	


	public YTVideo(Video video) {
		id = video.getId();
		VideoSnippet snippet = video.getSnippet();
		publishedAt=snippet.getPublishedAt();
		channelId=snippet.getChannelId();
		title=snippet.getTitle();
		thumbnailUrl=snippet.getThumbnails().getMedium().getUrl();
		duration = video.getContentDetails().getDuration();
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


	public String getDuration() {
		return duration;
	}


	public DateTime getPublishedAt() {
		return publishedAt;
	}


	@Override
	public int compareTo(YTVideo other) {
		return -1* Long.compare(publishedAt.getValue(), other.getPublishedAt().getValue());
		
	}
	public String toString(){
		return title;
	}
}
