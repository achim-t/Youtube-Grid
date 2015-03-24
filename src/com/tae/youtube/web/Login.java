package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.util.store.DataStore;
import com.tae.youtube.User;

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = getUserFromSession(session);

		System.out.println("user was created at: " + user.getCreatedAt());

	}

	private User getUserFromSession(HttpSession session) throws IOException {
		String id = (String) session.getAttribute("youtube_id");
		DataStore<User> users = (DataStore<User>) getServletContext()
				.getAttribute("users");
		User user = null;
		if (id != null && users.containsKey(id)) {
			user = users.get(id);
			System.out.println("returning user found");
		} else {
			user = new User();
			String id2 = session.getId();
			users.set(id2, user);
			session.setAttribute("youtube_id", id2);
			System.out.println("new user created");
		}
		return user;
	}

}
