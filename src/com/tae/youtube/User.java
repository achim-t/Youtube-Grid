package com.tae.youtube;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;


public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6228474404485120672L;
	private Map<String, YTVideo> videos;
	private Map<String, Channel> subscriptions;
	private String youtubeId;
	private transient YouTube youtube;
	private String name;
	private Settings settings;
	private Map<String, Collection<String>> filters;

	private static Map<String, User> users;
	private static DataStore<User> userDataStore;
	private static Map<String, String> sessionIdToYoutubeIdMapping = new HashMap<>();

	public static User createUser(Credential credential, String sessionId)
			throws IOException {
		User user = null;
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
				Auth.JSON_FACTORY, credential).build();
		ChannelListResponse list = youtube.channels().list("snippet")
				.setMine(true).execute();
		com.google.api.services.youtube.model.Channel channel = list.getItems()
				.get(0);
		String youtubeId = channel.getId();
		String name = channel.getSnippet().getTitle();
		if (youtubeId != null) {
			user = getUserByYouTubeId(youtubeId);
			if (user == null) {
				user = new User();
				user.setId(youtubeId);
				user.setName(name);
				User.users.put(youtubeId, user);
				user.setYoutube(youtube);
			}
			sessionIdToYoutubeIdMapping.put(sessionId, youtubeId);
		}
		return user;
	}

	private void setYoutube(YouTube youtube) {
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

	private User() {
		subscriptions = new HashMap<>();
		videos = new HashMap<>();
		settings = new Settings();
		filters = new HashMap<>();
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
		try {
			FileInputStream fileIn = new FileInputStream(
					System.getProperty("user.home") + "/" + "local_data" + "/"
							+ "user.map");
			ObjectInputStream in = new ObjectInputStream(fileIn);

			sessionIdToYoutubeIdMapping = (Map<String, String>) in.readObject();

			in.close();
			fileIn.close();
		} catch (Exception i) {
			sessionIdToYoutubeIdMapping = new HashMap<>();
		}
		users = new HashMap<>();

	}

	private static User getUserByYouTubeId(String id) {
		if (users.containsKey(id))
			return users.get(id);

		User user = null;

		try {
			if (userDataStore.containsKey(id))
				user = userDataStore.get(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		if (user != null)
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

		try {
			FileOutputStream fileOut = new FileOutputStream(
					System.getProperty("user.home") + "/" + "local_data" + "/"
							+ "user.map");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(sessionIdToYoutubeIdMapping);
			out.close();
			fileOut.close();

		} catch (IOException i) {
			i.printStackTrace();
		}
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
			if (!videos.containsKey(video.getId())) {
				result.add(video);
				videos.put(video.getId(), video);
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
		List<YTVideo> videos = new ArrayList<>();

		for (Channel channel : channels) {
			List<YTVideo> list = channel.getVideos(youtube);
			videos.addAll(list);
		}

		Collections.sort(videos);
		return videos;
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

	public static User getUserBySessionId(String sessionId) {
		if (sessionIdToYoutubeIdMapping.containsKey(sessionId)) {
			String youtubeId = sessionIdToYoutubeIdMapping.get(sessionId);
			return getUserByYouTubeId(youtubeId);
		} else {
			return null;
		}
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
			Channel channel = subscriptions.get(channelId);
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

}
