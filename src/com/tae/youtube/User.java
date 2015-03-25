package com.tae.youtube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
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
	public List<Channel> getActiveSubscriptions() {
		List<Channel> temp = new ArrayList<>();
		for (Channel channel: subscriptions)
			if (channel.isActive())
				temp.add(channel);
		return temp;
	}

	
}
