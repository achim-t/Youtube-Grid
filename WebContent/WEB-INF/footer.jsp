<nav class="navbar navbar-default navbar-fixed-bottom">
	<div class="container-fluid">
		<div class="navbar-inner pull-center">
			<div class="nav">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default navbar-btn ${settings.filtered?"active":""}">
						<input type="checkbox" id="cbFiltered" autocomplete="off"
						${settings.filtered?"checked":""}><span
						class="glyphicon glyphicon-filter" aria-hidden="true"></span> <span>Show
							Filtered</span>
					</label> <label class="btn btn-default navbar-btn ${settings.watched?"active":""}">
						<input type="checkbox" id="cbWatched"
						${settings.watched?"checked":""} /><span
						class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> <span>Show
							Watched</span>
					</label>
				</div>
			</div>
			<div class="nav navbar-right">
				<button class="btn btn-danger navbar-btn" id="btnMarkAll">Mark
					all as watched</button>
			</div>
			<div class="nav navbar-left">
				<button class="btn btn-default navbar-btn" id="btnEditFilters"
					data-toggle="modal" data-target="#editFiltersModal">Manage
					Filters</button>
			</div>
		</div>
	</div>
</nav>