/* printControl - Changes eform to add a server side generated PDF 
 *                with print functionality intact (if print button on the form).
 */

if (typeof jQuery == "undefined") { alert("The printControl library requires jQuery. Please ensure that it is loaded first"); }
var printControl = {
	initialize: function () {

		var submit = jQuery("input[name='SubmitButton']");
		var printSave = jQuery("input[name='PrintSaveButton']");
		submit.append("<input name='pdfSaveButton' type='button'>");
		submit.append("<input name='pdfButton' type='button'>");
		var pdf = jQuery("input[name='pdfButton']");
		var pdfSave = jQuery("input[name='pdfSaveButton']");
		if (! pdf) {
			pdf = jQuery("input[name='pdfButton']");
		}
		if (! pdfSave) {
			pdfSave = jQuery("input[name='pdfSaveButton']");
		}
	
		pdf.insertAfter(submit);	
		pdfSave.insertAfter(submit);

		if (pdf) {
			pdf.attr("onclick", "").unbind("click");
			pdf.attr("value", "PDF");
			pdf.click(function(){submitPrintButton(false);});
		}
		if (pdfSave) {
			pdfSave.attr("onclick", "").unbind("click");
			pdfSave.attr("value", "Submit & PDF");
			pdfSave.click(function(){submitPrintButton(true);});
		}
		if (printSave) {
			printSave.attr("value", "Submit & Print");
		}

	}
};

function submitPrintButton(save) {
	
	// Setting this form to print.
	var printHolder = jQuery('#printHolder');
	if (printHolder == null || ! printHolder) {
		jQuery("form").append("<input id='printHolder' type='hidden' name='print' value='true' >");
	}	
	printHolder = jQuery('#printHolder');
	printHolder.val("true");
	
	var saveHolder = jQuery("#saveHolder");
	if (saveHolder == null || ! saveHolder) {
		jQuery("form").append("<input id='saveHolder' type='hidden' name='skipSave' value='"+!save+"' >");
	}
	saveHolder = jQuery("#saveHolder");
	saveHolder.val(!save);
	needToConfirm=false;
	
	if (document.getElementById('Letter') != null) {
		document.getElementById('Letter').value=editControlContents('edit');		
	}	
	
	jQuery("form")[0].submit();
	if (save) { setTimeout("window.close()", 3000); }
	printHolder.val("false");
	saveHolder.val("false");
	
}


jQuery(document).ready(function(){printControl.initialize();});
