<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
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
<link rel="stylesheet" href="./ladda-themeless.min.css">
</head>
<body>
	<div class="container-fluid"></div>

	<!-- Modal -->
	<div class="modal fade" id="filterModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">Modal title</h4>
				</div>
				<div class="modal-body">
					<form>
						<div class="form-group">
							<input type="hidden" id="channel"> <input type="hidden"
								id="id"> <input type="text" class="form-control"
								id="filter">
							<button type="button" class="btn btn-primary" id="saveFilter">Save</button>
						</div>

					</form>
				</div>

			</div>
		</div>
	</div>

	<%@include file="filter.html"%>
	<%@include file="footer.jsp"%>

	<script id="template" type="x-tmpl-mustache">
<div class="video" id="{{id}}">
  	<div class="img-container">
		<a href="https://www.youtube.com/watch?v={{id}}">
			<img src="{{thumbnailUrl}}">
		</a>
		<span class="video-duration">{{duration}}</span>
		<div class="mark-watched" title="mark as watched">
			<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
		</div>
		<div class="filter" data-toggle="modal" data-target="#filterModal" data-title="{{title}}" data-channel="{{channelId}}" data-id="{{id}}" title="Filter Videos like this">
			<span class="glyphicon glyphicon-filter" aria-hidden="true"></span>
		</div>
	</div>
	<div class="title">{{title}}</div>
	<div class="byline">by <span class="channel">{{channelName}}</span></div>
</div>
</script>
	<script src="//cdn.jsdelivr.net/handlebarsjs/3.0.0/handlebars.js"></script>
	<script src="https://code.jquery.com/jquery-git2.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script src="./spin.min.js"></script>
	<script src="./ladda.min.js"></script>
	<script src="./app.js"></script>
	<script src="./filters.js"></script>

</body>
</html>