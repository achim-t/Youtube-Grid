package com.tae.youtube;

import java.io.Serializable;

public class Settings implements Serializable {
	
	private boolean watched=false;
	private boolean filtered=false;
	public boolean isWatched(){
		return watched;
	}
	public boolean isFiltered() {
		return filtered;
	}
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}
	public void setWatched(boolean watched) {
		this.watched = watched;
	}
}
