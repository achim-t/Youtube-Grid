
var l_more = Ladda.create(document.querySelector('#btnMore'));
var l_refresh = Ladda.create(document.querySelector('#btnRefresh'));
var count = 0;
$.ajaxSetup({ cache: false });
var dateFormatString = "dddd, MMMM Do YYYY, H:mm:ss";
function setAgo(){
	$('.video').each(function() {
		var $publishedAt=$('.publishedAt', $(this))
		var date=moment($publishedAt.attr('title'), dateFormatString)
		$publishedAt.text(date.fromNow())
	})

}
function toggleWatched() {

	if (this.checked) {
		$.ajax({
			url : './user',
			type : 'POST',
			data : {
				'setting' : 'showWatched'
			}
		});
		if (!$("#cbFiltered").is(":checked")) {
			$(".watched").not(".filtered").show();
		} else {
			$(".watched").show();
		}
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
function toggleFiltered() {

	if (this.checked) {
		$.ajax({
			url : './user',
			type : 'POST',
			data : {
				'setting' : 'showFiltered'
			}
		});
		if (!$("#cbWatched").is(":checked")) {
			$(".filtered").not(".watched").show();
		} else {
			$(".filtered").show();
		}
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
function markAs(video, state) {
	video.addClass(state);
	var $imgcontainer = $(".img-container", video);
	$imgcontainer.addClass("muted");
	$imgcontainer.append($('<div class="'+state+'-badge">' + state.toUpperCase()
			+ '</div>'));
}
function markWatched(video) {
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
function watched(e) {
	e.preventDefault();
	console.log("mark as watched");
	var video = $(this).closest(".video");
	markWatched(video);
};
function unwatched(e) {
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

function load(boolRefresh) {
	$.ajax({
		url : './videoList',
		data : {
			offset : count
		}
	}).done(function(responseJson) {
		if (responseJson.length===0){
			$('#btnMore').attr("disabled", "disabled")
		}
		$.each(responseJson, function(index, data) {
			$video = createVideo(index, data);
			$('.video-list').append($video);
		});
		l_refresh.stop()
		l_more.stop()
		if (boolRefresh) {
			refresh();
		}
		
	});
}
function refresh() {
	l_refresh.start()
	setAgo()
	console.log("trying to refresh Videos")
	$.ajax({
		url : './refreshVideos'
	}).done(function(responseJson) {
		responseJson.reverse();
		console.log("got response for refreshing videos")
		$.each(responseJson, function(index, data) {
			$video = createVideo(index, data);
			$('.video-list').prepend($video);
		});
		l_refresh.stop()
	});
}

function createVideo(index, data) {
	var url = "https://www.youtube.com/watch?v=" + data.videoId;
	var $video = $('<div>', {
		'class' : 'video',
		'id' : data.videoId
	});

	var $imgcontainer = $('<div>', {
		'class' : 'img-container',
		'title' : data.title
	}).appendTo($video);
	var $a = $('<a>', {
		'href' : url
	}).click(function() {
		markWatched($video);
		window.open(url);
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
			'class' : 'filtered-badge'
		}).text("FILTERED"));
	} else {
		var $filter = $('<div>', {
			'class' : 'filter',
			'data-toggle' : 'modal',
			'data-target' : '#filterModal',
			'data-title' : data.title,
			'data-channel' : data.channelId,
			'data-channelname' : data.channelName,
			'data-id' : data.videoId,
			'title' : 'Filter Videos like this'
		}).appendTo($imgcontainer);
		$('<span>', {
			'class' : "glyphicon glyphicon-filter",
			'aria-hidden' : 'true'
		}).appendTo($filter);
	}
	$video.append($('<a>', {
		'class' : 'title',
		'href' : url,
		'title' : data.title
	}).text(data.title).click(function() {
		markWatched($video);
		window.open(url);
		return false;
	}));
	$video.append($('<div>', {
		'class' : 'byline'
	}).text("by ").append($('<a>', {
		'class' : 'channel',
		'href' : 'https://www.youtube.com/channel/'+data.channelId
	}).click(function(event) {
		window.open('https://www.youtube.com/channel/'+data.channelId);
		return false;
	}).text(data.channelName)));
	var date = moment(data.publishedAt.value)
	$video.append($('<div>',{
		'class' : 'publishedAt',
		'title' : date.format(dateFormatString)
	}).text(date.fromNow()))
	

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
	count++;
	return $video;
}

function reload(){
	$('.video-list').empty();
	count = 0;
	load(false);
}
$(function() {
	$('#filterModal').on(
			'show.bs.modal',
			function(event) {
				var button = $(event.relatedTarget) // Button that triggered the
				// modal
				var title = button.data('title') // Extract info from data-*
				// attributes
				// If necessary, you could initiate an AJAX request here (and
				// then
				// do the updating in a callback).
				// Update the modal's content. We'll use jQuery here, but you
				// could
				// use a data binding library or other methods instead.
				var channel = button.data('channel')
				var channelName = button.data('channelname')
				var id = button.data('id')
				var modal = $(this)
				modal.find('#channelName').text(channelName)
				modal.find('#filter').val(title)
				modal.find('#channel').val(channel)
				modal.find('#id').val(id)

			})

	$('#filterModal').on('shown.bs.modal', function() {
		$('#filter').focus()
		$('#filter').select()
	})
	$('#filter-add-form').on('submit', function(event) {
		event.preventDefault()
		$.ajax({
			url : './filter',
			type : 'POST',
			data : {
				'filter' : $('#filter').val(),
				'channel' : $('#channel').val()
			}
		});
		$('#filterModal').modal('hide');
		var id = $('#id').val()
		var video = $('.video#'+id)
		if (!$("#cbFiltered").is(":checked")) {

			video.hide("fast");
		}
		markAs(video, 'filtered');
	});
	$("#cbWatched").on("change", toggleWatched);
	$("#cbFiltered").on("change", toggleFiltered);
	$('#btnMarkAll').on("click", function() {
		$('.video').filter(":visible").each(function() {
			markWatched($(this))
		})
	});

	$('#btnRefresh').on("click", refresh);
	$('#btnMore').on("click", function() {
		l_more.start()
		load(false)
		$(this).blur()
	})
	l_refresh.start()
	load(true)
});