package com.tae.youtube.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.tae.youtube.User;
import com.tae.youtube.YTVideo;


@WebServlet(urlPatterns = {"/videoList", "/refreshVideos"})
public class Videos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = User.getBySessionId(sessionId);
		
		List<YTVideo> videos = user.getSavedVideos();
		if (request.getRequestURI().endsWith("refreshVideos")){
			videos = user.getVideos(sessionId);
		}
		
		Collections.sort(videos);
		String json = new Gson().toJson(videos);
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);
	}
}
