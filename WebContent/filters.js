/**
 * 
 */
$(function() {
	$.ajax({
		url : './editFilters'

	}).done(function(responseJson) {
		console.log(responseJson)

		$.each(responseJson, function(index, channel) {
			if (channel.filters.length > 0) {
				var $formGroup = $('<div>', {
					'class' : 'form-group',
				})
				$('form').append($('<input>', {
					'type' : 'hidden',
					'name' : 'channel'
				}).val(channel.channelId))
				$.each(channel.filters, function(index, filter) {
					var $row = $('<div>', {
						'class' : 'row'
					});
					$('form').append($row);
					$row.append($('<input>', {
						'type' : 'text',
						'name' : 'filter',
						'channel' : channel.channelId

					}).val(filter))
					$row.append($('<button>').text('x').click(function(event) {
						event.preventDefault();
						$(this).closest('.row').remove();
					}));
				})
			}
		})

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
	})
	function createJSON() {
		jsonObj = {};
		$("input[name=channel]").each(function() {
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