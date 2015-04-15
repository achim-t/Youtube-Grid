<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
</head>
<body>
	<div class="container-fluid"></div>

	<!-- Modal -->
	<div class="modal fade" id="editFiltersModal" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			<div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Edit/Delete Filters</h4>
      </div>
				<div class="modal-body">
					<form method='post' action='./editFilters' id='myForm'></form>
<!-- 					<button id='submit'>Submit</button> -->
				</div>
				<div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-primary" id="submit">Save changes</button>
      </div>
			</div>
		</div>
	</div>
	<!-- Button trigger modal -->
	<button type="button" class="btn btn-primary btn-lg"
		data-toggle="modal" data-target="#editFiltersModal">Launch
		demo modal</button>

	<script src="https://code.jquery.com/jquery-git2.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script src="./filters.js"></script>
</body>
</html>