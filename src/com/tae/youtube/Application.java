package com.tae.youtube;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;

public class Application {
	private static Map<String, User> users;
	private static Map<String, String> sessionIdToYoutubeIdMapping = new HashMap<>();
	private static ExecutorService executor;
	private static EntityManagerFactory factory;
	
	public static EntityManagerFactory getFactory() {
		return factory;
	}

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
				System.out.println(youtubeId);
				user = new User();
				
				
				user.setId(youtubeId);
				user.setName(name);
//				User.users.put(youtubeId, user); //TODO save user
				EntityManager em = factory.createEntityManager();
				em.getTransaction().begin();
				em.persist(user);
//				em.persist(user.getSettings());
				user.setYoutube(youtube);
				em.getTransaction().commit();
				em.close();
			}
			sessionIdToYoutubeIdMapping.put(sessionId, youtubeId);
		}
		return user;
	}
	public static void init() {
		factory = Persistence.createEntityManagerFactory("default");
		int numThreads = 50;
		executor = Executors.newFixedThreadPool(numThreads);
		sessionIdToYoutubeIdMapping = new HashMap<>();
		users = new HashMap<>();
	}

	private static User getUserByYouTubeId(String id) {
		if (users.containsKey(id))
			return users.get(id);
		
		User user = null;
		EntityManager em = factory.createEntityManager();
		try {
			user = em.find(User.class, id);
		} catch (Exception e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		//TODO get from H2
		em.close();
		if (user != null)
			users.put(id, user);
		return user;
	}

	public static void save() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		for (User user : users.values()) {
			em.merge(user);
		}
		em.getTransaction().commit();
		em.close();
		factory.close();
		executor.shutdownNow();
	}

	public static ExecutorService getExecutor() {
		// TODO Auto-generated method stub
		return executor;
	}
	
	public static User getUserBySessionId(String sessionId) {
		if (sessionIdToYoutubeIdMapping.containsKey(sessionId)) {
			String youtubeId = sessionIdToYoutubeIdMapping.get(sessionId);
			return getUserByYouTubeId(youtubeId);
		} else {
			return null;
		}
	}
}
