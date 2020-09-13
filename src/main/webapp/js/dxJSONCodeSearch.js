
function searchDxCode(request, response) {
	jQuery.ajax({
	    url: ctx + "/dxCodeSearchJSON.do",
	    type: 'POST',
	    data: {
	    	method: request.method,
	    	keyword: request.term,
	    	codeSystem: request.codeSystem
	    },
	  	dataType: "json",
	    success: function(data) {
	    	if(data) {
				response(jQuery.map( data, function(item) { 
					return {
						label: item.code + ": " + item.description.trim(),
						value: item.code,
						id: item.id
					};
		    	}))
	    	}	
	    }				    
	})				
}

jQuery(document).ready(function() {

	jQuery( "#jsonDxSearch, .jsonDxSearch" ).autocomplete({			
		source: function(request, response) {
			request.method = 'search' + (jQuery( '#codingSystem option:selected, input#codingSystem' ).val()).toUpperCase();
			searchDxCode(request, response);					  
		},
		delay: 100,
		minLength: 2,
		select: function( event, ui ) {
			event.preventDefault();
			this.value = ui.item.value;
			jQuery( this ).prop('title', ui.item.label)
		}
	})
	
		
	jQuery(".jsonDxSearchButton").click(function () {
		var inputField = jQuery("#" + this.value);	
		var codeSystem = (jQuery( '#codingSystem option:selected, input#codingSystem' ).val()).toUpperCase();
		inputField.autocomplete({ 		
			source: function(request, response) {
				request.method = 'search' + codeSystem
				searchDxCode(request, response);					  
			},
			minLength: 3,
			select: function( event, ui ) {
				event.preventDefault();
				this.value = ui.item.value;
				jQuery( this ).prop('title', ui.item.label)
				inputField.autocomplete("destroy");
			},
			change: function( event, ui ) {
				inputField.autocomplete("destroy");
			} 
		}).autocomplete("search", inputField.val());
	});
})
