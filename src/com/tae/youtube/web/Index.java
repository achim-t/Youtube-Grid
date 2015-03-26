package com.tae.youtube.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tae.youtube.User;
import com.tae.youtube.YTVideo;

@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String youtubeId = (String) session.getAttribute("youtube_id");

		if (youtubeId == null) {
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}
		User user = User.getById(youtubeId);

		String sessionId = session.getId();

		List<YTVideo> videos = user.getVideos(sessionId);

		if (videos.size() > 0) {
			if (videos.size() > 25)
				request.setAttribute("videoList", videos.subList(0, 25));
			else {
				request.setAttribute("videoList", videos);
			}
			request.getRequestDispatcher("videoView")
					.forward(request, response);
		} else
			response.getWriter().println("no subscriptions found");

	}

}
