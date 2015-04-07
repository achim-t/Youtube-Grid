package com.tae.youtube.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.tae.youtube.User;
import com.tae.youtube.YTVideo;


@WebServlet("/videoList")
public class Videos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String youtubeId = (String) session.getAttribute("youtube_id");
		User user = User.getByYouTubeId(youtubeId);
		

		String sessionId = session.getId();
		

		List<YTVideo> videos = user.getVideos(sessionId);
		
		String json = new Gson().toJson(videos);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
		
	}


}