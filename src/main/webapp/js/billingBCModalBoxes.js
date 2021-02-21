
jQuery.noConflict();

/*
 * Add the modal container on load
 */

function modalContainer() {
	jQuery().remove("div#modaldialog");
	var wrapper = jQuery("<div />").attr("id", "modaldialog");
	var formwrapper = jQuery("<div />").attr("id", "modal-form");
	
	formwrapper.appendTo(wrapper);
	wrapper.appendTo("body");
}

/*
 * Blocks checking of any more checkboxes if 3 are checked.
 */
function checkboxChanged() {

	var numberchecked = jQuery("form#servicecode").find("input:checked").length;
	if(numberchecked == 3)
	{
		jQuery('form#servicecode input:checkbox:not(:checked)').each(function(){
			jQuery(this).attr("disabled", true).attr("title", "3 codes selected");
		});
	}
	else if (numberchecked < 3)
	{
		jQuery('form#servicecode input:checkbox:not(:checked)').each(function(){
			jQuery(this).removeAttr("disabled").removeAttr("title");
		});
	}
	
	jQuery("#addbutton").focus();
}

/*
 * update the description of a MSP Service code.
 */
function updateDescription(codeid) {
	var param = {};
	param.description = jQuery("form#servicecode #codedescription_" + codeid).val();
	param.codeid = codeid;
	param.method = "updateDescription";
	
	jQuery.post(ctx + '/billing/CA/BC/billingServiceCode.do', param , function(data, status){
		if(data.success)
		{
			jQuery('form#servicecode input#' + data.codeid).css("background-color", "green").val("updated");
		}
		else
		{
			jQuery('form#servicecode input#' + data.codeid).css("background-color", "red").val("error");
		}
		
		setInterval(function(){
			jQuery('form#servicecode input#' + data.codeid).css("background-color", "#428bca").val("update");
		}, 10000);
	});
}


jQuery(document).ready(function(jQuery){
	
	modalContainer();
	
	/*
	 * jQuery dialog modal window for selection of 
	 * service codes and referral doctors.
	 */
	 jQuery( "#modaldialog" ).dialog({
		autoOpen: false,
		height: 500,
		width: 770,
		modal: true,
		buttons: [
			{
				text: 'Add Selected', 
				"id": "addbutton",
				click: function() {
					var form = jQuery("#modaldialog #modal-form form")[0];			
				    var searchForm = jQuery("#modaldialog #modal-form form input[name='searchAction']").val();				    
				    codeAttach(form, searchForm);
				    jQuery( "#modaldialog" ).dialog( "close" );
				}
			},
			{
				text: "Cancel", 
				click: function() {
					jQuery( "#modaldialog" ).dialog( "close" );
				}
			}
		]
	});
	 
})