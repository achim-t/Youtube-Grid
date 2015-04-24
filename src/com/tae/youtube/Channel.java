package com.tae.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

@Entity
public class Channel {

	private String thumbnailUrl;
	private String title;
	@Id
	@GeneratedValue
	private long id;
	private String channelId;
	public Channel() {
		super();
	}

	private boolean isActive = true;
	@Transient // filters will be sent to the client via Gson, no need for a getter
	private Collection<String> filters;
	private DateTime lastRefreshTime;

	public DateTime getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(DateTime lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}

	public Channel(Subscription sub) {
		SubscriptionSnippet snippet = sub.getSnippet();
		title = snippet.getChannelTitle();
		thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
		channelId = snippet.getResourceId().getChannelId();
		title = snippet.getTitle();
		lastRefreshTime = new DateTime(0);
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
		if (!(obj instanceof Channel))
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

	public void setFilters(Collection<String> collection) {
		filters=new TreeSet<>(collection);
	}

	public List<String> getVideos(YouTube youtube){
		List<String> ids = new ArrayList<>();
		try {
			com.google.api.services.youtube.YouTube.Search.List searchList = youtube
					.search().list("id").setChannelId(channelId).setOrder("date")
					.setType("video").setMaxResults(50L)
					.setPublishedAfter(lastRefreshTime);

			SearchListResponse listResponse = searchList.execute();
			for (SearchResult item : listResponse.getItems()) {
				String id = item.getId().getVideoId();
				ids.add(id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ids;
	}
}
