/**
 * 
 */
$(function() {
	toggleWatched = function() {
		
		if (this.checked) {

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
		if (!$("#cbWatched").is(":checked")) {

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
	
	$("#cbWatched").on("change", toggleWatched);
	$(".mark-watched").on("click", watched);
	$('#btnMarkAll').on("click", function(){
		$('.video').addClass("muted");
		if (!$("#cbWatched").is(":checked")) {

			$('.video').hide();
		}
	});
});