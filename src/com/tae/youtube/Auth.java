package com.tae.youtube;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;

public class Auth {

	public static final String REDIRECT_URI = "/oauth2callback";
	private static final String CLIENTSECRETS_LOCATION = "client_secret.json";
	private static final Collection<String> SCOPES = Collections
			.singleton("https://www.googleapis.com/auth/youtube.readonly");
	private static GoogleAuthorizationCodeFlow flow = null;
	private static final String CREDENTIALS_DIRECTORY = "local_data";
	private static final String CREDENTIAL_DATASTORE = "oauth";
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static GoogleAuthorizationCodeFlow getFlow() throws IOException {
		if (flow == null) {
			FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(
					new File(System.getProperty("user.home") + "/"
							+ CREDENTIALS_DIRECTORY));
			DataStore<StoredCredential> datastore = fileDataStoreFactory
					.getDataStore(CREDENTIAL_DATASTORE);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
					JSON_FACTORY, new InputStreamReader(

					Auth.class.getResourceAsStream(CLIENTSECRETS_LOCATION)));
			flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
					JSON_FACTORY, clientSecrets, SCOPES)
					.setAccessType("offline").setApprovalPrompt("force")
					.setCredentialDataStore(datastore).build();
		}
		return flow;
	}

	public static YouTube getYoutube(Credential credential) {
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
				Auth.JSON_FACTORY, credential).setApplicationName("yt-grid")
				.build();
		return youtube;
	}

	public static Credential getCredential(String id) throws IOException {
		return getFlow().loadCredential(id);
	}

	public static void deleteUserFromCredentialDataStore(String userId)
			throws IOException {
		getFlow().getCredentialDataStore().delete(userId);

	}

	public static String getAuthorizationUrl(String redirectUri)
			throws IOException {
		return getFlow().newAuthorizationUrl().setRedirectUri(redirectUri)
				.build();
	}

	public static String getRedirectUri(HttpServletRequest req) {
		StringBuffer requestURL = req.getRequestURL();
		String servletPath = req.getServletPath();
		int start = requestURL.lastIndexOf(servletPath);
		requestURL.delete(start, requestURL.length());
		requestURL.append(REDIRECT_URI);
		return requestURL.toString();
	}
}
