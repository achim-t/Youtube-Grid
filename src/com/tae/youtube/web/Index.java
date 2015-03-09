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
		String nextPageToken = "";
		List<Channel> channelList = new ArrayList<>();
		String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?maxResults=50&part=snippet&mine=true&pageToken=";
		do {
			OAuthRequest oReq = new OAuthRequest(Verb.GET,requestUrl+nextPageToken);
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

			for (int i = 0; i < jsonArray.length(); i++) {
				channelList.add(new Channel(jsonArray.getJSONObject(i)));
			}
			if (jsonObject.has("nextPageToken"))
				nextPageToken = jsonObject.getString("nextPageToken");
			else 
				nextPageToken = "";
		} while (nextPageToken.length() > 0);
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
