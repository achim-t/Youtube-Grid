package com.tae.youtube.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.tae.youtube.User;
import com.tae.youtube.YTVideo;

@WebServlet(urlPatterns = { "/videoList", "/refreshVideos" })
public class Videos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int count = 10;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = User.getBySessionId(sessionId);
		List<YTVideo> videos = null;
		int offset = 0;
		if (request.getRequestURI().endsWith("videoList")) {
			try {
				String offsetString = request.getParameter("offset");
				offset = Integer.parseInt(offsetString);
			} catch (NumberFormatException e) {
				offset = 0;
			}
			videos = user.getSavedVideos();
			if (videos.size() == 0) // ie the user is new and has no saved
									// videos
				videos = user.getVideos(sessionId);
			else {
				int start = offset < videos.size() ? offset : videos.size();
				int end = (offset + count) < videos.size() ? offset + count
						: videos.size();
				videos = videos.subList(start, end);
			}

		}

		if (request.getRequestURI().endsWith("refreshVideos")) {
			videos = user.getVideos(sessionId);
		}
		if (videos != null) {
			String json = new Gson().toJson(videos);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}

	}
}
