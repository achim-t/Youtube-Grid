package com.tae.youtube;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

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
	
	public void addFilter(String filter){
		filters.add(filter);
	}
	
	public void removeFilter(String filter){
		filters.remove(filter);
	}
	
	public Collection<String>getFilters(){
		return filters;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void doFilter(Collection<YTVideo>videos){
		
		if (filters.isEmpty())
			return;
		for (YTVideo video: videos){
			if (video.getChannelId().equals(channelId)){
				video.setFiltered(false);
				for (String filter:filters){
					if (video.getTitle().contains(filter)){
						video.setFiltered(true);
						break;
					}
				}
			}
		}
		
		
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
}
