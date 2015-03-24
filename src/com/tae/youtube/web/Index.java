package com.tae.youtube.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.tae.youtube.Auth;
import com.tae.youtube.Channel;
import com.tae.youtube.User;
import com.tae.youtube.YTVideo;

@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String youtubeId = (String) session.getAttribute("youtube_id");
		DataStore<User> users = (DataStore<User>) getServletContext()
				.getAttribute("users");
		if (youtubeId == null || !users.containsKey(youtubeId)) {
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}
		User user = users.get(youtubeId);
		Credential credential = Auth.getCredential(session.getId());

		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
				Auth.JSON_FACTORY, credential).build();
		request.setAttribute("youtube", youtube);

		List<Channel> channelList = getSubscriptions(request);
		Set<Channel> subscriptions = user.getSubscriptions();

		for (Channel subscription : subscriptions) {
			if (!channelList.contains(subscription))
				subscription.setActive(false);
		}

		subscriptions.addAll(channelList);

		if (subscriptions.size() > 0) {
			request.setAttribute("channelList", subscriptions);

			SortedMap<String, YTVideo> videos = getVideosFromChannelList(request);
			ArrayList<YTVideo> arrayList = new ArrayList<YTVideo>(
					videos.values());
			Collections.sort(arrayList);

			request.setAttribute("videoList", arrayList);
			request.getRequestDispatcher("videoView")
					.forward(request, response);
		} else {
			response.getWriter().println("no subscriptions found");
		}
		users.set(user.getId(), user);

	}

	private List<Channel> getSubscriptions(HttpServletRequest request)
			throws IOException {
		// HttpSession session
		YouTube youtube = (YouTube) request.getAttribute("youtube");
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
				Auth.deleteUserFromCredentialDataStore(request.getSession()
						.getId());
				// response.sendRedirect("/Test/home");
				// return;
			}
			for (Subscription sub : subscriptionListResponse.getItems()) {
				channelList.add(new Channel(sub));
			}
		} while (nextPageToken.length() > 0); // TODO

		return channelList;
	}

	private SortedMap<String, YTVideo> getVideosFromChannelList(
			HttpServletRequest request) throws IOException {
		YouTube youtube = (YouTube) request.getAttribute("youtube");
		Collection<Channel> channelList = (Collection<Channel>) request
				.getAttribute("channelList");
		SortedMap<String, YTVideo> videoList = new TreeMap<>();
		List<String> videoIdList = new ArrayList<>();
		String ids = "";

		for (Channel channel : channelList) {
			SearchListResponse listResponse = youtube.search().list("id")
					.setChannelId(channel.getChannelId()).setOrder("date")
					.setType("youtube#video").execute();
			for (SearchResult item : listResponse.getItems()) {
				String id = item.getId().getVideoId();
				ids += id + ",";
				videoIdList.add(id);
			}
		}

		VideoListResponse videoListResponse = youtube.videos()
				.list("snippet,contentDetails").setId(ids).execute();

		for (Video v : videoListResponse.getItems()) {
			YTVideo video = new YTVideo(v);
			videoList.put(video.getId(), video);
		}

		return videoList;

	}

}
