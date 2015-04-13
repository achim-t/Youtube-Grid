package com.tae.youtube;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

@SuppressWarnings("serial")
public class YTVideo implements Comparable<YTVideo>, Serializable {
	private String id;
	private String channelId;
	private String title;
	private String thumbnailUrl;
	private String duration;
	private DateTime publishedAt;
	private boolean watched = false;
	private boolean filtered = false;
	

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isWatched() {
		return watched;
	}

	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	private static HashMap<String, String> regexMap = null;
	private static String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
	private static String two = "0$1";

	private static String convertDuration(String duration) {
		if (regexMap == null) {
			regexMap = new HashMap<>();
			regexMap.put("PT(\\d\\d)S", "00:$1");
			regexMap.put("PT(\\d\\d)M", "$1:00");
			regexMap.put("PT(\\d\\d)H", "$1:00:00");
			regexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
			regexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
			regexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
			regexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");
		}
		
		String d = duration.replaceAll(regex2two, two);
        String regex = getRegex(d);
        if (regex == null) {
            System.out.println(d + ": invalid");
            return duration;
//            throw new Exception();
        }
        String newDuration = d.replaceAll(regex, regexMap.get(regex));
		return newDuration;
	}

	@Override
	public int hashCode() {
		
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof YTVideo))
			return false;
		YTVideo other = (YTVideo) obj;
		return id.equals(other.getId());
	}

	private static String getRegex(String date) {
	    for (String r : regexMap.keySet())
	        if (Pattern.matches(r, date))
	            return r;
	    return null;
	}
	
	public YTVideo(Video video) {
		id = video.getId();
		VideoSnippet snippet = video.getSnippet();
		publishedAt = snippet.getPublishedAt();
		channelId = snippet.getChannelId();
		title = snippet.getTitle();
		thumbnailUrl = snippet.getThumbnails().getMedium().getUrl();
		duration = convertDuration(video.getContentDetails().getDuration());
	}

	public String getId() {
		return id;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getTitle() {
		return title;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getDuration() {
		return duration;
	}

	public DateTime getPublishedAt() {
		return publishedAt;
	}

	@Override
	public int compareTo(YTVideo other) {
		return -1
				* Long.compare(publishedAt.getValue(), other.getPublishedAt()
						.getValue());

	}

	public String toString() {
		return title;
	}
}
