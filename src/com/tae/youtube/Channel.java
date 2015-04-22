package com.tae.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

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
	private String channelId;
	public Channel() {
		super();
	}

	private boolean isActive = true;
	private List<String> filters;
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
		filters = new ArrayList<>();
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
		filters.clear();
		filters.addAll(collection);
	}

	public List<String> getVideos(YouTube youtube) throws IOException {
		List<String> ids = new ArrayList<>();
		com.google.api.services.youtube.YouTube.Search.List searchList = youtube
				.search().list("id").setChannelId(channelId).setOrder("date")
				.setType("video").setMaxResults(50L)
				.setPublishedAfter(lastRefreshTime);

		SearchListResponse listResponse = searchList.execute();
		for (SearchResult item : listResponse.getItems()) {
			String id = item.getId().getVideoId();
			ids.add(id);
		}
		return ids;
	}
}
