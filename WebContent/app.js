/**
 * 
 */
$(function() {
	toggleWatched = function() {

		if (this.checked) {
			$.ajax({
				url: './user',
				type: 'POST',
				data:{
					'setting': 'showWatched'
				}
			});
			$(".muted").closest('.video').show();
		} else {
			$.ajax({
				url: './user',
				type: 'POST',
				data:{
					'setting': 'hideWatched'
				}
			});
			$(".muted").closest('.video').hide();
		}

	};
	toggleFiltered = function() {

		if (this.checked) {
			$.ajax({
				url: './user',
				type: 'POST',
				data:{
					'setting': 'showFiltered'
				}
			});
//			$(".muted").show();
		} else {
			$.ajax({
				url: './user',
				type: 'POST',
				data:{
					'setting': 'hideFiltered'
				}
			});
//			$(".muted").hide();
		}

	};
	var markWatched = function (video){
		var $imgcontainer = $(".img-container", video);
		$imgcontainer.addClass("muted");
		$imgcontainer.append($('<div class="watched-badge">WATCHED</div>'));
		mark = $(".mark-watched", video);
		mark.off();
		mark.click(unwatched)
		if (!$("#cbWatched").is(":checked")) {

			video.hide();
		}
		mark.attr("title", "Mark as Unwatched");
		$.ajax({
			url : './video',
			type : 'POST',
			data : {
				'action' : 'mark',
				'id' : video[0].id
			}
		});
	};
	var watched = function(e) {
		e.preventDefault();
		console.log("mark as watched");
		var video = $(this).closest(".video");
		
		markWatched(video);
	};
	var unwatched = function(e) {
		e.preventDefault();
		var $video = $(this).closest(".video");
		var $imgcontainer = $(this).closest(".img-container");
		$imgcontainer.removeClass("muted");
		$('.watched-badge', $imgcontainer).remove();  
		$(this).off();
		$(this).click(watched);
		$(this).attr("title", "Mark as Watched");
		$.ajax({
			url : './video',
			type : 'POST',
			data : {
				'action' : 'unmark',
				'id' : $video[0].id
			}
		});
	}

	$("#cbWatched").on("change", toggleWatched);
	$("#cbFiltered").on("change", toggleFiltered);
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
			class : 'video',
			id : data.id
		});
		
		
		var $imgcontainer = $('<div>', {
			class : 'img-container'
		}).appendTo($video);
		var $a = $('<a>', {
			href : "https://www.youtube.com/watch?v="+data.id
		}).click(function(){
			markWatched($video);
			window.open($a.attr('href'));
			return false;
		});
		$a.appendTo($imgcontainer);
		$('<img>', {
			src : data.thumbnailUrl
		}).appendTo($a);
		$imgcontainer.append($('<span>', {
			'class' : 'video-duration'
		}).text(data.duration));
		if (data.watched){
			$imgcontainer.append($('<div>', {
				class : 'watched-badge'
			}).text("WATCHED"));
			var $mark = $('<div>', {
				class : 'mark-watched',
				title : 'mark as unwatched'
			}).click(unwatched);
		}
		else {
			var $mark = $('<div>', {
				class : 'mark-watched',
				title : 'mark as watched'
			}).click(watched);
		}
		if (data.filtered){
			$imgcontainer.append($('<div>', {
				'class' : 'watched-badge' //TODO different badge
			}).text("FILTERED"));
		};	
		$mark.appendTo($imgcontainer);
		$('<span>', {
			class : "glyphicon glyphicon-ok",
			'aria-hidden' : 'true'
		}).appendTo($mark);
		var $filter = $('<div>', {
			'class' : 'filter',
			'data-toggle' : 'modal',
			'data-target' : '#myModal',
			'data-title' : data.title,
			'data-channel' : data.channelId,
			title : 'Filter Videos like this'
		}).appendTo($imgcontainer);
		$('<span>', {
			class : "glyphicon glyphicon-filter",
			'aria-hidden' : 'true'
		}).appendTo($filter);
		$video.append($('<div>', {
			class : 'title'
		}).text(data.title));
		$video.append($('<div>',{
			class : 'byline'
		}).text("by ")).append($('<span>',{
			class: 'channel'
		}).text(data.channelId));
		$video.appendTo($('.container-fluid'));
		if (data.watched){
			$imgcontainer.addClass("muted");
			if (!$("#cbWatched").is(":checked")) {

				$video.hide();
			}
		}
	}
	
	$('#myModal').on('show.bs.modal', function (event) {
		  var button = $(event.relatedTarget) // Button that triggered the modal
		  var title = button.data('title') // Extract info from data-* attributes
		  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
		  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
		  var channel = button.data('channel')
		  var modal = $(this)
		  modal.find('.modal-title').text('Filter on' + channel)
		  modal.find('#filter').val(title)
		  modal.find('#channel').val(channel)
		  
		  
		})
	$('#saveFilter').on('click', function(even){
		$.ajax({
			url : './filter',
			type : 'POST',
			data : {
				'filter' : $('#filter').val(),
				'channel' : $('#channel').val()
			}
		});
		console.log($('#filter').val());
		console.log($('#channel').val());
		$('#myModal').modal('hide');
	});
});