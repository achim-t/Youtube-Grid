<footer class="footer">
	<div class="container">
		<div class="btn-group pull-right">
			<button class="btn btn-info" disabled="disabled">
				<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
				<span>${userName}</span>
			</button>
			<a href="signout" class="btn btn-default"> <span
				class="glyphicon glyphicon-log-out" title="Sign out"
				aria-hidden="true"></span>
			</a>
		</div>
		<div class="btn-group" data-toggle="buttons">
			<label class="btn btn-default ${settings.filtered?"active":""}"> <input type="checkbox" id="cbFiltered"
				autocomplete="off" ${settings.filtered?"checked":""}><span class="glyphicon glyphicon-filter"
				aria-hidden="true"></span> <span>Show Filtered</span>
			</label> <label class="btn btn-default ${settings.watched?"active":""}"> 
			<input type="checkbox"  id="cbWatched" ${settings.watched?"checked":""}/><span
				class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> <span>Show Watched</span>
			</label>

		</div>
		

		<button class="btn btn-default ladda-button" data-spinner-color="#000000" id="btnRefresh" data-style="zoom-in" title="Refresh" data-size='1'>
		<span class="ladda-label"><span class="glyphicon glyphicon-refresh " aria-hidden="true"></span></span>
 			
		</button>
		<button class="btn btn-danger center" id="btnMarkAll">
			Mark all as watched
		</button>
		<button class="btn btn-default" id="btnEditFilters" data-toggle="modal" data-target="#editFiltersModal">
			Manage Filters
		</button>
			</div>
</footer>