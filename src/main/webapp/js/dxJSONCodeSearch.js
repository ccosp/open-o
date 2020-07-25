
	jQuery(document).ready(function() {

		jQuery( "#jsonDxSearch, .jsonDxSearch" ).autocomplete({			
			source: function(request, response) {
				jQuery.ajax({
				    url: ctx + "/dxCodeSearchJSON.do",
				    type: 'POST',
				    data: {
				    	method: 'search' + (jQuery( '#codingSystem' ).find(":selected").val()).toUpperCase(),
				    	keyword: request.term
				    },
				  	dataType: "json",
				    success: function(data) {
						response(jQuery.map( data, function(item) { 
							return {
								label: item.description.trim() + ' (' + item.code + ')',
								value: item.code,
								id: item.id
							};
				    	}))
				    }			    
				})					  
			},
			delay: 100,
			minLength: 2,
			select: function( event, ui ) {
				event.preventDefault();
				jQuery( this ).val(ui.item.value);
			},
			focus: function(event, ui) {
		        event.preventDefault();
		        jQuery( this ).val(ui.item.label);
		    }
		})

				
	})
