package com.tae.youtube;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable{
	private List<Channel> subscriptions;
	private String id;
	private Date createdAt;
	public User(){
		createdAt = new Date();
	}
	public List<Channel> getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(List<Channel> subscriptions) {
		this.subscriptions = subscriptions;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreatedAt() {
		return createdAt;
	}

	
}
