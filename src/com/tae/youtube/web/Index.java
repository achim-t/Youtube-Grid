package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tae.youtube.User;

@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String youtubeId = (String) session.getAttribute("youtube_id");
		User user = User.getByYouTubeId(youtubeId);
		if (youtubeId == null || user == null) {
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}

		request.getRequestDispatcher("videoView").forward(request, response);

	}

}
