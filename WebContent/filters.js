/**
 * 
 */
$(function() {
	$('#editFiltersModal').on('show.bs.modal', function(event) {
		var form = $('form#editFilterForm').empty();
		$.ajax({
			url : './editFilters'

		}).done(function(responseJson) {
			console.log(responseJson)

			$.each(responseJson, function(index, channel) {
				if (channel.filters.length > 0) {
					var $channel = $('<div>', {
						'class' : 'row'
					})
					$channelInfo = $('<div>', {
						'class' : 'col-sm-3'
					})
					$channelInfo.append($('<img>', {
						'src' : channel.thumbnailUrl,
						'class' : 'img-thumbnail'
					}));
					$channelInfo.append($('<div>', {
						'class' : 'caption'
					}).text(channel.title))
					$channel.append($channelInfo)
					form.append($channel)
					var $col = $('<div>', {
						'class' : 'form-group col-sm-9',
					})
					$channel.append($col)
					form.append($('<input>', {
						'type' : 'hidden',
						'name' : 'channel'
					}).val(channel.channelId))
					$.each(channel.filters, function(index, filter) {
						var $row = $('<div>', {
							'class' : 'input-group'
						});
						$col.append($row);
						$row.append($('<input>', {
							'type' : 'text',
							'name' : 'filter',
							'class' : 'form-control',
							'channel' : channel.channelId

						}).val(filter))

						var $span = $('<span>', {
							'class' : 'input-group-btn'
						});
						$row.append($span);
						$span.append($('<button>', {
							'class' : 'btn btn-default',
							'type' : 'button'
						}).append($('<span>', {
							'class' : 'glyphicon glyphicon-remove',
							'style' : 'color:red'
						})).click(function(event) {
							event.preventDefault();
							$target = $(this).closest('.input-group');
							$target.hide(function() {
								$target.remove();
							});
						}));
					});
				}
			})

		});
	});
	$('button#submit').click(function() {

		var json = createJSON();
		json = JSON.stringify(json)
		console.log("json: " + json)
		$.ajax({
			url : './editFilters',
			type : 'post',
			dataType : 'json',
			data : json,
			contentType : 'application/json; charset=UTF-8'
		});
		$('#editFiltersModal').modal('hide');
		reload()
	})
	function createJSON() {
		jsonObj = {};
		var form = $('form#editFilterForm')
		$("input[name=channel]", form).each(function() {
			var channel = $(this).val();
			item = [];
			$("input[channel=" + channel + "]").each(function() {
				item.push(this.value);
			})
			jsonObj[channel] = item;
		});

		return jsonObj;
	}

});