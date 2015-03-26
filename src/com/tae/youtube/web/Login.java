package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.Credential;
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

		if (youtubeId == null) {
			String userId = session.getId();
			Credential credential = Auth.getCredential(userId);
			if (credential == null) {
				String url = Auth.getAuthorizationUrl();
				resp.sendRedirect(url);
				return;
			}

			else {
				User user = User.createUser(credential);

				session.setAttribute("youtube_id", user.getId());
				System.out.println("user was created at: "
						+ user.getCreatedAt());
			}
		}
		req.getRequestDispatcher("/index").forward(req, resp);
	}

}
