package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tae.youtube.Application;
import com.tae.youtube.User;

/**
 * Servlet implementation class User
 */
@WebServlet("/user")
public class UserSettingsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = Application.getUserBySessionId(sessionId);
		String setting = request.getParameter("setting");
		if (user != null && setting != null) {

			switch (setting) {
			case "showWatched":
				user.getSettings().setWatched(true);
				break;
			case "hideWatched":
				user.getSettings().setWatched(false);
				break;
			case "showFiltered":
				user.getSettings().setFiltered(true);
				break;
			case "hideFiltered":
				user.getSettings().setFiltered(false);
				break;

			default:
				break;
			}
		}

	}

}
