package com.tae.youtube;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

@SuppressWarnings("serial")
public class User implements Serializable {
	private Set<Channel> subscriptions;
	private String id;
	private Date createdAt;
	private static Map<String, User> users;
	private static DataStore<User> userDataStore;
	private transient YouTube youtube;

	public YouTube getYoutube(String sessionId) throws IOException{
		if (youtube == null) {
			Credential credential = Auth.getCredential(sessionId);

			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
					Auth.JSON_FACTORY, credential).build();
		}
		return youtube;
	}

	private User() {
		createdAt = new Date();
		subscriptions = new HashSet<>();
	}

	public Set<Channel> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<Channel> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public List<Channel> getActiveSubscriptions() {
		List<Channel> temp = new ArrayList<>();
		for (Channel channel : subscriptions)
			if (channel.isActive())
				temp.add(channel);
		return temp;
	}

	public static void init() {
		if (users != null)
			return;

		FileDataStoreFactory fileDataStoreFactory;
		try {
			fileDataStoreFactory = new FileDataStoreFactory(new File(
					System.getProperty("user.home") + "/" + "local_data"));
			userDataStore = fileDataStoreFactory.getDataStore("users");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		users = new HashMap<>();
	}

	public static User getById(String id) {
		if (users.containsKey(id))
			return users.get(id);

		User user = null;

		try {
			if (userDataStore.containsKey(id))
				user = userDataStore.get(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} finally {
			if (user == null)
				user = createUser(id);
		}
		users.put(id, user);
		return user;
	}

	public static void save() {
		for (User user : users.values()) {
			try {
				userDataStore.set(user.getId(), user);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static User createUser(String id) {
		User user = new User();
		user.setId(id);
		users.put(id, user);
		return user;

	}

	public List<YTVideo> getVideos(String sessionId) throws IOException {
		getYoutube(sessionId);
		List<Channel> currentSubscriptions = getSubscriptionsFromYouTube(sessionId);
		Set<Channel> allSubscriptions = this.getSubscriptions();

		for (Channel subscription : allSubscriptions) {
			if (!currentSubscriptions.contains(subscription))
				subscription.setActive(false);
		}

		allSubscriptions.addAll(currentSubscriptions);

		List<Channel> activeSubscriptions = getActiveSubscriptions();
		return getVideosFromChannelList(activeSubscriptions, sessionId);
	}
	
	private List<YTVideo> getVideosFromChannelList(Collection<Channel> channelList, String sessionId)
			throws IOException {
		List<YTVideo> videoList = new ArrayList<>();

		for (Channel channel : channelList) {
			String ids = "";
			com.google.api.services.youtube.YouTube.Search.List searchList = youtube
					.search().list("id").setChannelId(channel.getChannelId())
					.setOrder("date").setType("video").setMaxResults(50L);
			DateTime publishedAt;
			try {
				YTVideo newestVideo = channel.getNewestVideo();
				publishedAt = newestVideo.getPublishedAt();
				searchList.setPublishedAfter(publishedAt);
			} catch (NoSuchElementException e) {

			}
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
				channel.addVideo(video);

			}
			videoList.addAll(channel.getVideos());
		}

		Collections.sort(videoList);
		return videoList;

	}
	private List<Channel> getSubscriptionsFromYouTube(String sessionId)
			throws IOException {
		
		List<Channel> channelList = new ArrayList<>();

		String nextPageToken = "";

		do {

			SubscriptionListResponse subscriptionListResponse = null;
			try {
				subscriptionListResponse = youtube.subscriptions()
						.list("snippet").setMine(true).setMaxResults((long) 50)
						.execute();
			} catch (TokenResponseException | GoogleJsonResponseException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
//				Auth.deleteUserFromCredentialDataStore(request.getSession()
//						.getId());
				// response.sendRedirect("/Test/home");
				// return;
			}
			for (Subscription sub : subscriptionListResponse.getItems()) {
				channelList.add(new Channel(sub));
			}
		} while (nextPageToken.length() > 0); // TODO

		return channelList;
	}

}
