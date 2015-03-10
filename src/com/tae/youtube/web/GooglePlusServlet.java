package com.tae.youtube.web;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.oauth.OAuthService;

import com.tae.youtube.OAuthServiceProvider;


@WebServlet("/googleplus")
public class GooglePlusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OAuthService service = OAuthServiceProvider.getService(); //Now build the call
		HttpSession session = request.getSession();
		session.setAttribute("oauth2Service", service);
		response.sendRedirect(service.getAuthorizationUrl(null));
	}
}
