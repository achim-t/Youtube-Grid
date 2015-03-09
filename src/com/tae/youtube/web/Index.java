package com.tae.youtube.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.tae.youtube.Video;

@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		OAuthService service = (OAuthService) session
				.getAttribute("oauth2Service");
		Token token;
		String nextPageToken = "";
		List<Channel> channelList = new ArrayList<>();
		String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?maxResults=50&part=snippet&mine=true&pageToken=";
		do {
			OAuthRequest oReq = new OAuthRequest(Verb.GET, requestUrl
					+ nextPageToken);
			token = (Token) session.getAttribute("token");
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

		SortedMap<String, Video> videos = getVideos(channelList, service, token);
		System.out.println("hi");
		ArrayList<Video> arrayList = new ArrayList<Video>(videos.values());
		Collections.sort(arrayList);
		request.setAttribute("videoList", arrayList);
		request.getRequestDispatcher("videoView").forward(request, response);
		// request.setAttribute("channelList", channelList);
		// request.getRequestDispatcher("channelView").forward(request,
		// response);
	}

	private SortedMap<String, Video> getVideos(List<Channel> channelList,
			OAuthService service, Token token) {
		SortedMap<String, Video> videoList = new TreeMap<>();
		List<String> videoIdList = new ArrayList<>();
		String ids = "";
		String requestUrl = "https://www.googleapis.com/youtube/v3/search?order=date&part=id&channelId=";
		for (Channel channel : channelList) {
			OAuthRequest oReq = new OAuthRequest(Verb.GET, requestUrl
					+ channel.getChannelId());
			service.signRequest(token, oReq);
			Response oResp = oReq.send();
			JSONArray videos = new JSONObject(oResp.getBody())
					.getJSONArray("items");
			// System.out.println(oResp.getBody());
			for (int i = 0; i < videos.length(); i++) {
				JSONObject idJson = videos.getJSONObject(i).getJSONObject("id");
				if (idJson.has("videoId")) {
					String id = idJson.getString("videoId");
					ids += id + ",";
					videoIdList.add(id);
				}
			}
		}

		requestUrl = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails&id=";
		OAuthRequest oReq = new OAuthRequest(Verb.GET, requestUrl + ids);
		service.signRequest(token, oReq);
		Response oResp = oReq.send();

		JSONObject responseBody = new JSONObject(oResp.getBody());
		if (!responseBody.has("error")) {
			JSONArray videos = responseBody.getJSONArray("items");
			// System.out.println(oResp.getBody());
			for (int i = 0; i < videos.length(); i++) {
				Video video = new Video(videos.getJSONObject(i));
				videoList.put(video.getId(), video);
			}
		}
		return videoList;

	}

}
