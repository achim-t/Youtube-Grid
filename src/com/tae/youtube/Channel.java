package com.tae.youtube;
import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

@SuppressWarnings("serial")
public class Channel implements Serializable{

	private String thumbnailUrl;
	private String title;
	private String channelId;
	private boolean isActive=true;
	private SortedSet<YTVideo> videos;

	public YTVideo getNewestVideo()throws NoSuchElementException{
		return videos.first();
	}

	public Channel(Subscription sub) {
		SubscriptionSnippet snippet = sub.getSnippet();
		title = snippet.getChannelTitle();
		thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
		channelId = snippet.getResourceId().getChannelId();
		videos = new TreeSet<>();
	}
	
	public void addVideo(YTVideo video){
		videos.add(video);
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

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Channel))
			return false;
		return channelId.equals(((Channel) obj).getChannelId());
	}

	@Override
	public int hashCode() {
		
		return channelId.hashCode();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Collection<YTVideo> getVideos() {
		return videos;
	}
	
	
	

}
