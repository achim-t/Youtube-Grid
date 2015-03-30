/**
 * 
 */
$(function() {
	toggleWatched = function() {
		
		if ($(this).hasClass("active")) {

			$(".muted").show();
		} else {
			$(".muted").hide();
		}

	};

	watched = function(e) {
		e.preventDefault();
		var video = $(this).closest(".video");
		video.addClass("muted");
		$(this).on("click", unwatched);
		if (!$("#cbWatched").hasClass("active")) {

			video.hide();
		}
		$("a", this).attr("title", "Mark as Unwatched");
		$.ajax({
			url : './video',
			type : 'POST',
			data : {
				'action' : 'mark',
				'id' : '1234'
			}
		});

	};
	unwatched = function(e) {
		e.preventDefault();
		$(this).closest(".video").removeClass("muted");
		$(this).on("click", watched);
		$("a", this).attr("title", "Mark as Watched");
		$.ajax({
			url : './video',
			type : 'POST',
			data : {
				'action' : 'unmark',
				'id' : '1234'
			}
		});
	}
	
	$("#cbWatched").on("click", toggleWatched);
	$(".mark-watched").on("click", watched);
});