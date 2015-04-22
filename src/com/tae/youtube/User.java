package com.tae.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;
import javax.persistence.Transient;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;



@Entity
public class User {
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String, YTVideo> videos= new HashMap<>();
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String, Channel> subscriptions=new HashMap<>();;
	@Id
	private String youtubeId;
	@Transient
	private transient YouTube youtube;
	private String name;
	@OneToOne(cascade = CascadeType.ALL)
	private Settings settings = new Settings();
	private Map<String, Collection<String>> filters= new HashMap<>();
	
	

	

	public void setYoutube(YouTube youtube) {
		this.youtube = youtube;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public YouTube getYoutube(String sessionId) throws IOException {
		if (youtube == null) {
			Credential credential = Auth.getCredential(sessionId);

			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
					Auth.JSON_FACTORY, credential).build();
		}
		return youtube;
	}

	public String getId() {
		return youtubeId;
	}

	public void setId(String id) {
		this.youtubeId = id;
	}

	private List<Channel> getActiveSubscriptions() throws IOException {
		List<Channel> currentSubscriptions = getSubscriptionsFromYouTube();
		
		for (Channel subscription : subscriptions.values()) {
			if (!currentSubscriptions.contains(subscription))
				subscription.setActive(false);
		}
		
		for (Channel subscription : currentSubscriptions) {
			
			String channelId = subscription.getChannelId();
			if (!subscriptions.containsKey(channelId)) {
				subscriptions.put(channelId, subscription);
			}
			else 
				subscriptions.get(channelId).setActive(true);
		
		}
		return currentSubscriptions;
	}

	

	public List<YTVideo> getSavedVideos() {
		List<YTVideo> videoList = new ArrayList<>();
		videoList.addAll(videos.values());
		doFilterVideos(videoList);
		return videoList;
	}

	public List<YTVideo> getVideos(String sessionId) throws IOException {
		getYoutube(sessionId);
		List<Channel> activeSubscriptions = getActiveSubscriptions();

		List<YTVideo> list = getVideosFromYoutube(activeSubscriptions);
		List<YTVideo> result = new ArrayList<>();
		for (YTVideo video : list) {
			if (!videos.containsKey(video.getVideoId())) {
				result.add(video);
				videos.put(video.getVideoId(), video);
			}

		}
		doFilterVideos(result);
		return result;
	}

	private void doFilterVideos(List<YTVideo> videos) {
		for (YTVideo video : videos) {
			video.setFiltered(false);
			String channelId = video.getChannelId();
			if (filters.containsKey(channelId)) {
				for (String filter : filters.get(channelId)) {
					if (video.getTitle().contains(filter)) {
						video.setFiltered(true);
						break;
					}
				}
			}
		}
		Collections.sort(videos);
	}

	private List<YTVideo> getVideosFromYoutube(Collection<Channel> channels)
			throws IOException {
		long startTime = System.currentTimeMillis();
		List<YTVideo> videos = new ArrayList<>();
		List<String> ids = getIds(channels);
		
		for (int i = 0; i * 50 < ids.size(); i++) {
			int start = 50 * i;
			int end = start + 50 < ids.size() ? start + 50 : ids.size();
			List<String> subList = ids.subList(start, end);
			String idsString = String.join(",", subList);
			VideoListResponse videoListResponse = youtube.videos()
					.list("snippet,contentDetails").setId(idsString).execute();

			for (Video v : videoListResponse.getItems()) {
				YTVideo video = new YTVideo(v);
				Channel channel = getChannel(video.getChannelId());
				video.setChannelName(channel.getTitle());
				videos.add(video);
				if (video.getPublishedAt().getValue() > channel
						.getLastRefreshTime().getValue()) {
					channel.setLastRefreshTime(video.getPublishedAt());
				}
			}
		}

		Collections.sort(videos);
		long stopTime = System.currentTimeMillis();
		System.out.println("Fetching update Videos took "+(stopTime-startTime)+"ms.");
		return videos;
	}

	private List<String> getIds(Collection<Channel> channels)
			throws IOException {
		List<String> ids = new ArrayList<>();
		Collection<Callable<List<String>>> tasks = new ArrayList<>();
		for (Channel channel : channels) {
			tasks.add(new GetVideosTask(channel));
		}
		
		try {
			List<Future<List<String>>> results = Application.getExecutor().invokeAll(tasks);
			for (Future<List<String>> result : results) {
				for (String id : result.get()) {
					if (!this.videos.containsKey(id)) {
						ids.add(id);
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ids;
	}
	

	private List<Channel> getSubscriptionsFromYouTube()
			throws IOException {

		List<Channel> channelList = new ArrayList<>();

		String nextPageToken = "";

		do {
			SubscriptionListResponse subscriptionListResponse = null;
			try {
				subscriptionListResponse = youtube.subscriptions()
						.list("snippet")
						.setMine(true)
						.setMaxResults((long) 50)
						.setPageToken(nextPageToken)
						.execute();
			} catch (TokenResponseException | GoogleJsonResponseException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				// Auth.deleteUserFromCredentialDataStore(request.getSession()
				// .getId());
				// response.sendRedirect("/Test/home");
				// return;
			}
			nextPageToken = subscriptionListResponse.getNextPageToken();
			for (Subscription sub : subscriptionListResponse.getItems()) {
				channelList.add(new Channel(sub));
			}
		} while (nextPageToken!=null && nextPageToken.length() > 0); 

		return channelList;
	}

	

	public YTVideo getVideo(String videoId) {
		return videos.get(videoId);
	}

	public Settings getSettings() {
		return settings;
	}

	public Channel getChannel(String channelId) {
		return subscriptions.get(channelId);
	}

	public Collection<Channel> getFilters() {
		Collection<Channel> list = new ArrayList<>();
		for (String channelId : filters.keySet()) {
			Channel channel = subscriptions.get(channelId);//TODO channel can be null
			if (channel.isActive()) {
				channel.setFilters(filters.get(channelId));
				list.add(channel);
			}
		}
		return list;
	}

	public void setFilters(Map<String, Collection<String>> filters) {
		this.filters.clear();
		this.filters.putAll(filters);
	}

	public void addFilter(String channelId, String filter) {
		if (filters.containsKey(channelId)) {
			filters.get(channelId).add(filter);
		} else {
			List<String> list = new ArrayList<>();
			list.add(filter);
			filters.put(channelId, list);
		}
	}
	
	private final class GetVideosTask implements Callable<List<String>>{
		private final Channel channel;
		GetVideosTask(Channel channel){
			this.channel = channel;
		}
		@Override
		public List<String> call() throws Exception {
			
			return channel.getVideos(youtube);
		}
		
	}

}
