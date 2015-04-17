package com.tae.youtube.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.tae.youtube.Auth;

/**
 * Servlet implementation class OAuth2CallbackServlet
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/oauth2callback" })
public class OAuth2CallbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
		if (code != null) {
			HttpSession session = request.getSession();
			String userId = session.getId();
			GoogleAuthorizationCodeTokenRequest tokenRequest = Auth.getFlow()
					.newTokenRequest(code);
			GoogleTokenResponse tokenResponse = tokenRequest.setRedirectUri(
					Auth.REDIRECT_URI).execute();
			Auth.getFlow().createAndStoreCredential(tokenResponse, userId);
			response.sendRedirect("./login");
		}
		else {
			PrintWriter writer = response.getWriter();
			writer.write("y u no access :(");
		}
	}
}
