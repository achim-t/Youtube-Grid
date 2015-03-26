package com.tae.youtube.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.tae.youtube.User;

@WebListener
public class InitListener implements ServletContextListener {

	public InitListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		User.save();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		User.init();
	}
}
