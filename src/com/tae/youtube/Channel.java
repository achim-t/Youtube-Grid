package com.tae.youtube;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

@SuppressWarnings("serial")
public class Channel implements Serializable{

	private String thumbnailUrl;
	private String title;
	private String channelId;
	private boolean isActive=true;
	private List<String> filters;

	public Channel(Subscription sub) {
		SubscriptionSnippet snippet = sub.getSnippet();
		title = snippet.getChannelTitle();
		thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
		channelId = snippet.getResourceId().getChannelId();
		title = snippet.getTitle();
		filters = new ArrayList<>();
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

	public void setFilters(Collection<String> collection) {
		filters.clear();
		filters.addAll(collection);
	}
	
	public List<YTVideo> getVideos(YouTube youtube) throws IOException {
		List<YTVideo> list = new ArrayList<>();

		String ids = "";
		com.google.api.services.youtube.YouTube.Search.List searchList = youtube
				.search().list("id").setChannelId(channelId)
				.setOrder("date").setType("video").setMaxResults(50L);
		// DateTime publishedAt;
		// try {
		// YTVideo newestVideo = channel.getNewestVideo();
		// publishedAt = newestVideo.getPublishedAt();
		// searchList.setPublishedAfter(publishedAt);
		// } catch (NoSuchElementException e) {
		//
		// }
		SearchListResponse listResponse = searchList.execute();
		for (SearchResult item : listResponse.getItems()) {
			String id = item.getId().getVideoId();
			ids += "," + id;
		}
		ids = ids.substring(1);
		VideoListResponse videoListResponse = youtube.videos()
				.list("snippet,contentDetails").setId(ids).execute();

		for (Video v : videoListResponse.getItems()) {
			YTVideo video = new YTVideo(v);
			video.setChannelName(title);
			list.add(video);
		}
		return list;
	}
}
