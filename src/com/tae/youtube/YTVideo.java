package com.tae.youtube;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

@Entity
public class YTVideo implements Comparable<YTVideo> {
	private static String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
	private static HashMap<String, String> regexMap = null;
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
			// throw new Exception();
		}
		String newDuration = d.replaceAll(regex, regexMap.get(regex));
		return newDuration;
	}

	private static String getRegex(String date) {
		for (String r : regexMap.keySet())
			if (Pattern.matches(r, date))
				return r;
		return null;
	}

	
	private String channelId;
	private String channelName;
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	private String duration;
	private boolean filtered = false;

	@Id
	@GeneratedValue
	private long id;
	
	private String videoId;
	public YTVideo() {
	}

	private DateTime publishedAt;
	private String thumbnailUrl;
	private String title;

	private boolean watched = false;

	public YTVideo(Video video) {
		videoId = video.getId();
		VideoSnippet snippet = video.getSnippet();
		publishedAt = snippet.getPublishedAt();
		channelId = snippet.getChannelId();
		title = snippet.getTitle();
		thumbnailUrl = snippet.getThumbnails().getMedium().getUrl();
		duration = convertDuration(video.getContentDetails().getDuration());
	}

	@Override
	public int compareTo(YTVideo other) {
		return -1
				* Long.compare(publishedAt.getValue(), other.getPublishedAt()
						.getValue());

	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof YTVideo))
			return false;
		YTVideo other = (YTVideo) obj;
		return videoId.equals(other.getVideoId());
	}

	public String getChannelId() {
		return channelId;
	}

	public String getDuration() {
		return duration;
	}

	public String getVideoId() {
		return videoId;
	}

	public DateTime getPublishedAt() {
		return publishedAt;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {

		return videoId.hashCode();
	}

	public boolean isFiltered() {
		return filtered;
	}

	public boolean isWatched() {
		return watched;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	public String toString() {
		return title;
	}
}
