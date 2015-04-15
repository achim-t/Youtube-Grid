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
					var $formGroup = $('<div>', {
						'class' : 'form-group',
					})
					form.append($('<input>', {
						'type' : 'hidden',
						'name' : 'channel'
					}).val(channel.channelId))
					$.each(channel.filters, function(index, filter) {
						var $row = $('<div>', {
							'class' : 'input-group'
						});
						form.append($row);
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
							$(this).closest('.input-group').remove();
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