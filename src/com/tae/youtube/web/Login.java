package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Channels.List;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.tae.youtube.Auth;
import com.tae.youtube.User;

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String youtubeId = (String) session.getAttribute("youtube_id");
		DataStore<User> users = (DataStore<User>) getServletContext()
				.getAttribute("users");
		User user = null;
		
		
		if (youtubeId == null){
			String userId = session.getId();
			Credential credential = Auth.getCredential(userId);
			if (credential == null) {
				String url = Auth.getAuthorizationUrl();
				resp.sendRedirect(url);
				return;
			}
			YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
					Auth.JSON_FACTORY, credential).build();
			ChannelListResponse list = youtube.channels().list("id").setMine(true).execute();
			youtubeId = list.getItems().get(0).getId();
			
		}
		
		
		if (youtubeId != null && users.containsKey(youtubeId)) {
			user = users.get(youtubeId);
			System.out.println("returning user found "+user.getId());
		}
		
		if (user==null) {
			user = new User();
			user.setId(youtubeId);
			users.set(youtubeId, user);
			session.setAttribute("youtube_id", youtubeId);
			System.out.println("new user created "+user.getId());
		}
		System.out.println("user was created at: " + user.getCreatedAt());
		req.getRequestDispatcher("/index").forward(req, resp);
	}

}
