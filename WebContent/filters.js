/**
 * 
 */
$(function() {
	$.ajax({
		url : './editFilters'

	}).done(function(responseJson) {
//		console.log("got response")
		console.log(responseJson)
		
		$.each(responseJson, function(index, channel){
			var $formGroup = $('<div>', {
				'class': 'form-group',
			})
			$('form').append($('<input>',{
				'type' : 'hidden',
				'name': 'channel'
			}).val(index))
			$.each(channel, function(index2, filter){
				$('form').append($('<input>',{
					'type': 'text',
					'name':'filter',
					'channel':index
						
				}).val(filter))
			})
//			$form.append($formGroup);
		})
		
	});
	
	$('button#submit').click(function(){
		
		var json = createJSON();
		json = JSON.stringify(json)
		console.log(json)
	    $.ajax({
	        url: './editFilters',
	        type: 'post',
	        dataType: 'json',
	        data: json,
	        contentType: 'application/json; charset=UTF-8'
	    });
	})
	function createJSON() {
    jsonObj = {};
    $("input[name=channel]").each(function() {

        var channel = $(this).val();

        item = [];
        $("input[channel="+channel+"]").each(function(){
        	item.push(this.value);
        })

        jsonObj[channel]=item;
    });

    return jsonObj;
}
	
});