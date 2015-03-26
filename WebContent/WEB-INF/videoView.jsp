<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.List,com.tae.youtube.YTVideo"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Channel List</title>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="/Youtube/style.css">
</head>
<body>
	<div class="container-fluid">
		<%
			for (YTVideo video : ((List<YTVideo>) request
					.getAttribute("videoList"))) {
		%>

		<div class="video">
			<div class="title"><%=video.getTitle()%></div>
			<div class="img-container">
				<a href="https://www.youtube.com/watch?v=<%=video.getId()%>"><img
					src='<%=video.getThumbnailUrl()%>' /></a> <span class="video-duration"><%=video.getDuration()%></span>
			</div>
			<%-- <td><%= video.getPublishedAt() %> --%>
		</div>
		<%
			}
		%>
	</div>
	<%@include file="footer.html"%>

	
	<script src="https://code.jquery.com/jquery-git2.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>