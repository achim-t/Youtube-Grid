package com.tae.youtube;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.oauth.OAuthService;

public class OAuthServiceProvider {
	private static final String CLIENT_ID = "331645637746-72r1bogrn14ud43p02cqhoq53vopi9ft.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "qq14xBzl0IjKmzKVI2HWUrT0";

	public static OAuthService getService() {
		ServiceBuilder builder = new ServiceBuilder();
		OAuthService service = builder.provider(Google2Api.class)
				.apiKey(CLIENT_ID).apiSecret(CLIENT_SECRET)
				.callback("http://localhost:8080/Youtube/oauth2callback")
				.scope("https://www.googleapis.com/auth/youtube.readonly")
				.debugStream(System.out).build(); // Now build the call
		return service;
	}
}
