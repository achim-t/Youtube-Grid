package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.tae.youtube.Auth;
import com.tae.youtube.User;

@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String sessionId = req.getSession().getId();

		User user = User.getBySessionId(sessionId);
		if (user == null) {
			Credential credential = Auth.getCredential(sessionId);
			if (credential == null) {
				String url = Auth.getAuthorizationUrl();
				resp.sendRedirect(url);
				return;
			}

			else {
				user = User.createUser(credential, sessionId);

				System.out.println("user was created at: "
						+ user.getCreatedAt());
			}
		}
		req.getRequestDispatcher("/index").forward(req, resp);
	}

}
