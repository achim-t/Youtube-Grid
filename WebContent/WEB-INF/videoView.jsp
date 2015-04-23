<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Youtube Grid</title>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="./css/style.css">
<link rel="stylesheet" href="./css/ladda-themeless.min.css">
<link rel="icon" href="./favicon.ico" type="image/x-icon">
</head>
<body>
<%@include file="header.jsp"%>
	<div class="container-fluid">
		<div class="video-list row"></div>
		<button class="btn btn-default center-block ladda-button"
			data-spinner-color="grey" data-style="expand-left" id="btnMore">
			<span class="ladda-label">Load More</span>
		</button>
	</div>
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
					<h4 class="modal-title">Add a filter for <span id="channelName"></span>'s Channel</h4>
				</div>
				<div class="modal-body">
					<form id="filter-add-form">
						<div class="form-group">

							<input type="hidden" id="channel"> <input type="hidden"
								id="id">
							<div class="input-group">
								<input type="text" class="form-control" id="filter" autofocus>
								<span class="input-group-btn">
									<input type="submit" class="btn btn-primary" id="saveFilter" value="Save">
								</span>
							</div>
						</div>

					</form>
					<p>This filter will be applied immediately only to the selected video, 
					as well as to all videos loaded from now on.</p>
				</div>

			</div>
		</div>
	</div>

	<%@include file="filter.html"%>
	<%@include file="footer.jsp"%>

	<script src="https://code.jquery.com/jquery-git2.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script src="./js/spin.min.js"></script>
	<script src="./js/ladda.min.js"></script>
	<script src="./js/moment.min.js"></script>
	<script src="./js/app.js"></script>
	<script src="./js/filters.js"></script>

</body>
</html>