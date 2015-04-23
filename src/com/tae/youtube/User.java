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
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

@Entity
public class User {
	@OneToMany(cascade = CascadeType.ALL)
	private Map<String, YTVideo> videos = new HashMap<>();
	@OneToMany(cascade = CascadeType.ALL)
	private Map<String, Channel> subscriptions = new HashMap<>();;
	@Id
	private String youtubeId;
	@Transient
	private transient YouTube youtube;
	private String name;
	@OneToOne(cascade = CascadeType.ALL)
	private Settings settings = new Settings();
	private Map<String, Collection<String>> filters = new HashMap<>();
	private static final int BATCH_SIZE = 20;

	public void setYoutube(YouTube youtube) {
		this.youtube = youtube;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private YouTube getYoutube(String sessionId) throws IOException {
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
			} else {
				subscriptions.get(channelId).setActive(true);
			}
		}
		return currentSubscriptions;
	}

	public List<YTVideo> getSavedVideos() {
		List<YTVideo> videoList = new ArrayList<>(videos.values());
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
		EntityManager em = Application.getFactory().createEntityManager();
		em.getTransaction().begin();
		em.merge(this);
		em.getTransaction().commit();
		em.close();
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
		List<YTVideo> videos = new ArrayList<>();
		List<String> ids = getVideoIds(channels);

		assert (BATCH_SIZE <= 50);
		Collection<Callable<VideoListResponse>> tasks = new ArrayList<>();
		for (int i = 0; i * BATCH_SIZE < ids.size(); i++) {
			int start = BATCH_SIZE * i;
			int end = start + BATCH_SIZE < ids.size() ? start + BATCH_SIZE
					: ids.size();
			List<String> subList = ids.subList(start, end);
			tasks.add(new GetVideosTask(subList));
		}
		try {
			List<Future<VideoListResponse>> results = Application.getExecutor()
					.invokeAll(tasks);
			for (Future<VideoListResponse> result : results) {
				VideoListResponse videoListResponse = result.get();
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
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videos;
	}

	private List<String> getVideoIds(Collection<Channel> channels)
			throws IOException {
		List<String> ids = new ArrayList<>();
		Collection<Callable<List<String>>> tasks = new ArrayList<>();
		for (Channel channel : channels) {
			tasks.add(new GetVideoIdsTask(channel));
		}

		try {
			List<Future<List<String>>> results = Application.getExecutor()
					.invokeAll(tasks);
			for (Future<List<String>> result : results) {
				for (String id : result.get()) {
					if (!this.videos.containsKey(id)) {
						ids.add(id);
					}
				}
			}
		} catch (InterruptedException| ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ids;
	}

	private List<Channel> getSubscriptionsFromYouTube() throws IOException {

		List<Channel> channelList = new ArrayList<>();
		String nextPageToken = "";

		do {
			SubscriptionListResponse subscriptionListResponse = null;
			try {
				subscriptionListResponse = youtube.subscriptions()
						.list("snippet").setMine(true).setMaxResults((long) 50)
						.setPageToken(nextPageToken).execute();
			} catch (TokenResponseException | GoogleJsonResponseException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
			nextPageToken = subscriptionListResponse.getNextPageToken();
			for (Subscription sub : subscriptionListResponse.getItems()) {
				channelList.add(new Channel(sub));
			}
		} while (nextPageToken != null && nextPageToken.length() > 0);

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
			Channel channel = subscriptions.get(channelId);// TODO channel can
															// be null
			if (channel!=null && channel.isActive()) {
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

	private final class GetVideoIdsTask implements Callable<List<String>> {
		private final Channel channel;

		GetVideoIdsTask(Channel channel) {
			this.channel = channel;
		}

		@Override
		public List<String> call() throws Exception {

			return channel.getVideos(youtube);
		}
	}

	private final class GetVideosTask implements Callable<VideoListResponse> {
		private final Collection<String> ids;

		GetVideosTask(Collection<String> ids) {
			this.ids = ids;
		}

		@Override
		public VideoListResponse call() throws Exception {
			String idsString = String.join(",", ids);
			return youtube.videos().list("snippet,contentDetails")
					.setId(idsString).execute();
		}

	}

}
