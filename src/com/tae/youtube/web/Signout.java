package com.tae.youtube.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tae.youtube.Auth;

/**
 * Servlet implementation class Signout
 */
@WebServlet("/signout")
public class Signout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String sessionId = req.getSession().getId();
		req.getSession().invalidate();
		Auth.deleteUserFromCredentialDataStore(sessionId);
		req.getRequestDispatcher("./index").forward(req, resp);;
	}

	
}
