package com.tae.youtube;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

@SuppressWarnings("serial")
public class User implements Serializable {
	private Set<Channel> subscriptions;
	private String id;
	private Date createdAt;
	private static Map<String, User> users;
	private static DataStore<User> userDataStore;

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

}
