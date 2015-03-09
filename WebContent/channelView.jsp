<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.List,com.tae.youtube.Channel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Channel List</title>
</head>
<body>
<table border="1">
<% 
	for (Channel channel : (List<Channel>)request.getAttribute("channelList")){ 
%>
<tr>
<td><%= channel.getChannelId() %></td>
<td><%= channel.getTitle() %></td>
<td><img src='<%= channel.getThumbnailUrl() %>' /></td>
</tr>
<% } %>

</table>
<a href="signout">Sign out</a>
</body>
</html>