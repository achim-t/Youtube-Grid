package com.tae.youtube;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SessionToYoutube {
	
	@Id
	private String sessionId;
	private String youtubeID;
	public SessionToYoutube() {
	}
	public SessionToYoutube(String sessionId, String youtubeID) {
		this.sessionId = sessionId;
		this.youtubeID = youtubeID;
	}
	
	public String getYoutubeId(){
		return youtubeID;
	}

	
}
