package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tae.youtube.Application;
import com.tae.youtube.User;
import com.tae.youtube.YTVideo;

@WebServlet("/video/*")
public class VideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = Application.getUserBySessionId(sessionId);
		if (user != null) {
			String videoId = request.getParameter("id");
			YTVideo video = user.getVideo(videoId);
			String action = request.getParameter("action");
			switch (action) {
			case "mark":
				video.setWatched(true);
				break;
			case "unmark":
				video.setWatched(false);
				break;

			}

		}
	}

}
