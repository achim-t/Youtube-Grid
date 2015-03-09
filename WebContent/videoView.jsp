<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.SortedMap,com.tae.youtube.Video"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Channel List</title>
</head>
<body>
<table border="1">
<% 
	for (Video video : ((SortedMap<String,Video>)request.getAttribute("videoList")).values()){ 
%>
<tr>
<td><%= video.getId() %></td>
<td><%= video.getTitle() %></td>
<td><img src='<%= video.getThumbnailUrl() %>' /></td>
</tr>
<% } %>

</table>
<a href="signout">Sign out</a>
</body>
</html>