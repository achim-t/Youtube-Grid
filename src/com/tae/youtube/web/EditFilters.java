package com.tae.youtube.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.tae.youtube.User;

/**
 * Servlet implementation class EditFilters
 */
@WebServlet("/editFilters")
public class EditFilters extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = User.getBySessionId(sessionId);
		Map<String, Collection<String>> allFilters = user.getFilters();

		String json = new Gson().toJson(allFilters);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String sessionId = request.getSession().getId();
		User user = User.getBySessionId(sessionId);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				request.getInputStream()));
		String json = null;
		if (br != null) {
			json = br.readLine();
		}
		if (json != null) {
			Map<String, Collection<String>> filters = new Gson().fromJson(json,
					Map.class);
			user.setFilters(filters);
		}
	}

}
