<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.List,com.tae.youtube.Video"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Channel List</title>
<link rel="stylesheet" href="/Youtube/style.css" />
</head>
<body>

<% 
	for (Video video : ((List<Video>)request.getAttribute("videoList"))){ 
%>

<div class="video">
<div class="title"><%= video.getTitle() %></div>
<div class="img-container">
	<a href="https://www.youtube.com/watch?v=<%= video.getId() %>"><img src='<%= video.getThumbnailUrl() %>' /></a>
	<span class="video-duration"><%= video.getDuration() %></span>
</div>
<%-- <td><%= video.getPublishedAt() %> --%>
</div>
<% } %>


<a href="signout">Sign out</a>
</body>
</html>