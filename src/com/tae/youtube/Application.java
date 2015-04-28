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
		YouTube youtube = Auth.getYoutube(credential);
		ChannelListResponse list = youtube.channels().list("snippet")
				.setMine(true).execute();
		com.google.api.services.youtube.model.Channel channel = list.getItems()
				.get(0);
		String youtubeId = channel.getId();
		String name = channel.getSnippet().getTitle();
		if (youtubeId != null) {
			user = getUserByYouTubeId(youtubeId);
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();
			if (user == null) {
				user = new User();
				user.setId(youtubeId);
				user.setName(name);
				em.persist(user);
				user.setYoutube(youtube);
			}
			sessionIdToYoutubeIdMapping.put(sessionId, youtubeId);
			em.persist(new SessionToYoutube(sessionId, youtubeId));
			em.getTransaction().commit();
			em.close();
		}
		return user;
	}

	public static void init() {
		factory = Persistence.createEntityManagerFactory("default");
		int numThreads = 50;
		executor = Executors.newFixedThreadPool(numThreads);
		users = new HashMap<>();
	}

	private static User getUserByYouTubeId(String id) {
		if (id == null)
			return null;
		if (users.containsKey(id))
			return users.get(id);

		User user = null;
		EntityManager em = factory.createEntityManager();
		try {
			user = em.find(User.class, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
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
		return executor;
	}

	public static User getUserBySessionId(String sessionId) {
		if (sessionIdToYoutubeIdMapping.containsKey(sessionId)) {
			String youtubeId = sessionIdToYoutubeIdMapping.get(sessionId);
			return getUserByYouTubeId(youtubeId);
		}
		EntityManager em = factory.createEntityManager();
		SessionToYoutube sessionToYoutube = em.find(SessionToYoutube.class,
				sessionId);
		em.close();
		if (sessionToYoutube != null) {
			String youtubeId = sessionToYoutube.getYoutubeId();
			sessionIdToYoutubeIdMapping.put(sessionId, youtubeId);
			return getUserByYouTubeId(youtubeId);
		}
		return null;
	}
}
