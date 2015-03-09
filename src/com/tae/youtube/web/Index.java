package com.tae.youtube.web;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.tae.youtube.Channel;

/**
 * Servlet implementation class Index
 */
@WebServlet("/")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		OAuthService service = (OAuthService) session
				.getAttribute("oauth2Service");
		OAuthRequest oReq = new OAuthRequest(Verb.GET,
				"https://www.googleapis.com/youtube/v3/subscriptions?part=snippet&mine=true");
		Token token = (Token) session.getAttribute("token");
		if (service == null) {
			request.getRequestDispatcher("googleplus").forward(request,
					response);
			return;
		}
		service.signRequest(token, oReq);
		Response oResp = oReq.send();
		JSONObject jsonObject = new JSONObject(oResp.getBody());
		JSONArray jsonArray = jsonObject.getJSONArray("items");
		List<Channel> channelList = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			channelList.add(new Channel(jsonArray.getJSONObject(i)));
		}
		request.setAttribute("channelList", channelList);
		request.getRequestDispatcher("channelView").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
