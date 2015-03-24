package com.tae.youtube;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable{
	private Set<Channel> subscriptions;
	private String id;
	private Date createdAt;
	public User(){
		createdAt = new Date();
		subscriptions = new HashSet<>();
	}
	public Set<Channel> getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(Set<Channel> subscriptions) {
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
	public Set<Channel> getActiveSubscriptions() {
		Set<Channel> temp = new HashSet<>();
		for (Channel channel: subscriptions)
			if (channel.isActive())
				temp.add(channel);
		return temp;
	}

	
}
