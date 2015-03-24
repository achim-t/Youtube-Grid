package com.tae.youtube.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.tae.youtube.User;

@WebListener
public class InitListener implements ServletContextListener {

	public InitListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		FileDataStoreFactory fileDataStoreFactory;
		try {
			fileDataStoreFactory = new FileDataStoreFactory(new File(
					System.getProperty("user.home") + "/" + "local_data"));
			DataStore<User> users = fileDataStoreFactory.getDataStore("users");
			event.getServletContext().setAttribute("users", users);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
