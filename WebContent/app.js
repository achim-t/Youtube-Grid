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
			class : 'mark-watched'
		}).appendTo($imgcontainer);
		$a = $('<a>', {
			href : '#',
			title : 'mark as watched',
			'onClick' : 'watched'
		}).appendTo($mark);
		$('<span>', {
			class : "glyphicon glyphicon-ok",
			'aria-hidden' : 'true'
		}).appendTo($a);
		var $filter = $('<div>', {
			'class' : 'filter'
		}).appendTo($imgcontainer);
		$a = $('<a>', {
			href : '#',
			title : 'Filter Videos like this'
		}).appendTo($filter);
		$('<span>', {
			class : "glyphicon glyphicon-filter",
			'aria-hidden' : 'true'
		}).appendTo($a);

		$video.appendTo($('.container-fluid'));
	}
});