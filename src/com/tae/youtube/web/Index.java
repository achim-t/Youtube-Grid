package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tae.youtube.User;

@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String sessionId = request.getSession().getId();
		User user = User.getBySessionId(sessionId);
		if(user==null){
			request.getRequestDispatcher("/login").forward(request, response);
			return;
		}
		
		request.setAttribute("settings", user.getSettings());
		request.getRequestDispatcher("videoView").forward(request, response);
	}
}
