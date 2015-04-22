package com.tae.youtube;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Settings  {
	@Id
	@GeneratedValue
	private int id;

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
