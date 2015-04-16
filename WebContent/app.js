/**
 * 
 */
$(function() {
	var template = Handlebars.compile($('#template').html());
	
	toggleWatched = function() {

		if (this.checked) {
			$.ajax({
				url : './user',
				type : 'POST',
				data : {
					'setting' : 'showWatched'
				}
			});
			$(".watched").show();
		} else {
			$.ajax({
				url : './user',
				type : 'POST',
				data : {
					'setting' : 'hideWatched'
				}
			});
			$(".watched").hide();
		}

	};
	toggleFiltered = function() {

		if (this.checked) {
			$.ajax({
				url : './user',
				type : 'POST',
				data : {
					'setting' : 'showFiltered'
				}
			});
			$(".filtered").show();
		} else {
			$.ajax({
				url : './user',
				type : 'POST',
				data : {
					'setting' : 'hideFiltered'
				}
			});
			$(".filtered").hide();
		}

	};
	var markAs = function(video, state){
		video.addClass(state);
		var $imgcontainer = $(".img-container", video);
		$imgcontainer.addClass("muted");
		$imgcontainer.append($('<div class="watched-badge">'+state.toUpperCase()+'</div>'));
	}
	var markWatched = function(video) {
		if (!$("#cbWatched").is(":checked")) {

			video.hide("fast");
		}
		markAs(video, 'watched');
		mark = $(".mark-watched", video);
		mark.off();
		mark.click(unwatched)
		
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
		$video.removeClass("watched")
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
	
	$('#btnRefresh').on("click",refresh);
	
	var l = Ladda.create(document.querySelector( '.ladda-button' ));
	l.start();
	$.ajax({
		url : './videoList'

	}).done(function(responseJson) {
		l.stop();
		responseJson.reverse();
		$.each(responseJson, createVideo);
		
		refresh();

	});

	function refresh(){
		console.log("trying to refresh Videos")
		l.start();
		$.ajax({
			url : './refreshVideos'
		}).done(function(responseJson) {
			responseJson.reverse();
			console.log("got response for refreshing videos")
			$.each(responseJson, createVideo);
			l.stop();
		});
	}
	var createVideo2 = function(index, data){
		var html = template(data);
		var $video = $('<div>').html(html);
		$video.appendTo($('.container-fluid'));
	};
	
	var createVideo = function(index, data) {
		var $video = $('<div>', {
			'class' : 'video',
			'id' : data.id
		});

		var $imgcontainer = $('<div>', {
			'class' : 'img-container'
		}).appendTo($video);
		var $a = $('<a>', {
			'href' : "https://www.youtube.com/watch?v=" + data.id
		}).click(function() {
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
		if (data.watched) {
			$video.addClass('watched');
			$imgcontainer.append($('<div>', {
				'class' : 'watched-badge'
			}).text("WATCHED"));
			var $mark = $('<div>', {
				'class' : 'mark-watched',
				'title' : 'mark as unwatched'
			}).click(unwatched);
		} else {
			var $mark = $('<div>', {
				'class' : 'mark-watched',
				'title' : 'mark as watched'
			}).click(watched);
		}

		$mark.appendTo($imgcontainer);
		$('<span>', {
			'class' : "glyphicon glyphicon-ok",
			'aria-hidden' : 'true'
		}).appendTo($mark);
		if (data.filtered) {
			$video.addClass('filtered');
			$imgcontainer.append($('<div>', {
				'class' : 'watched-badge' 
			}).text("FILTERED"));
		} else {
			var $filter = $('<div>', {
				'class' : 'filter',
				'data-toggle' : 'modal',
				'data-target' : '#filterModal',
				'data-title' : data.title,
				'data-channel' : data.channelId,
				'data-id' : data.id,
				'title' : 'Filter Videos like this'
			}).appendTo($imgcontainer);
			$('<span>', {
				'class' : "glyphicon glyphicon-filter",
				'aria-hidden' : 'true'
			}).appendTo($filter);
		}
		$video.append($('<div>', {
			'class' : 'title'
		}).text(data.title));
		$video.append(
				$('<div>', 
						{ 'class':'byline' })
						.text("by ")
						.append(
								$('<span>', {
									'class': 'channel'})
									.text(data.channelName)
								)
				);
		$('.container-fluid').prepend($video);
		if (data.watched) {
			$imgcontainer.addClass("muted");
			if (!$("#cbWatched").is(":checked")) {

				$video.hide();
			}
		}
		if (data.filtered) {
			$imgcontainer.addClass("muted");
			if (!$("#cbFiltered").is(":checked")) {

				$video.hide();
			}
		}
	}

	$('#filterModal').on('show.bs.modal', function(event) {
		var button = $(event.relatedTarget) // Button that triggered the
		// modal
		var title = button.data('title') // Extract info from data-*
		// attributes
		// If necessary, you could initiate an AJAX request here (and then
		// do the updating in a callback).
		// Update the modal's content. We'll use jQuery here, but you could
		// use a data binding library or other methods instead.
		var channel = button.data('channel')
		var id = button.data('id')
		var modal = $(this)
		modal.find('.modal-title').text('Filter Videos on Channel' + channel)
		modal.find('#filter').val(title)
		modal.find('#channel').val(channel)
		modal.find('#id').val(id)

	})
	$('#saveFilter').on('click', function(event) {
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
		$('#filterModal').modal('hide');
		console.log('#'+$('#id').val())
		var video = $('#'+$('#id').val())
		markAs(video, 'filtered')
		
		if (!$("#cbFiltered").is(":checked")){
			video.hide()
		}
	});
});