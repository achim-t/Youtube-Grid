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
		console.log("mark as watched");
		e.preventDefault();
		var video = $(this).closest(".video");
		video.addClass("muted");
		$(this).off();
		$(this).click(unwatched)
		if (!$("#cbWatched").is(":checked")) {

			video.hide();
		}
		$(this).attr("title", "Mark as Unwatched");
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
		$(this).off();
		$(this).click(watched);
		$(this).attr("title", "Mark as Watched");
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
	$('#btnMarkAll').on("click", function() {
		$('.video').addClass("muted");
		if (!$("#cbWatched").is(":checked")) {

			$('.video').hide();
		}
	});

	$.ajax({
		url : './videoList'

	}).done(function(responseJson) {
		$.each(responseJson, createVideo);
		console.log("trying to refresh Videos")
		$.ajax({
			url : './refreshVideos'
		}).done(function(responseJson){
			console.log("got response for refreshing videos")
			$.each(responseJson, createVideo);
			
		});
		
	});
	var createVideo = function(index, data) {
		var $video = $('<div>', {
			class : 'video'
		});
		$video.append($('<div>', {
			class : 'title'
		}).text(data.title));
		var $imgcontainer = $('<div>', {
			class : 'img-container'
		}).appendTo($video);
		var $a = $('<a>', {
			href : data.id
		}).appendTo($imgcontainer);
		$('<img>', {
			src : data.thumbnailUrl
		}).appendTo($a);
		$imgcontainer.append($('<span>', {
			'class' : 'video-duration'
		}).text(data.duration));
		var $mark = $('<div>', {
			class : 'mark-watched',
			title : 'mark as watched'
		}).click(watched).appendTo($imgcontainer);
		$('<span>', {
			class : "glyphicon glyphicon-ok",
			'aria-hidden' : 'true'
		}).appendTo($mark);
		var $filter = $('<div>', {
			'class' : 'filter',
			title : 'Filter Videos like this'
		}).appendTo($imgcontainer);
		$('<span>', {
			class : "glyphicon glyphicon-filter",
			'aria-hidden' : 'true'
		}).appendTo($filter);

		$video.appendTo($('.container-fluid'));
	}
});