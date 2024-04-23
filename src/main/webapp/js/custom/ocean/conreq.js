var section = 'consultation';

(function (script) {
    let oceanHost = script.attributes['ocean-host'];
    if (oceanHost && oceanHost.value) {
        window.oceanHost = decodeURIComponent(oceanHost.value);
    }
})(document.currentScript);


jQuery(document).ready(function(){
    jQuery("#ocean").append("<div id='ocean_div'></div>");
    jQuery.ajax({
        url: window.oceanHost + "/robots.txt",
        cache: true,
        dataType: "text",
        success: function() {
            jQuery.ajax({
                url: window.oceanHost + "/oscar_resources/OscarToolbar.js",
                cache: true,
                dataType: "script"
            });
        },
        error: function(jqXHR, textStatus, error) {
            console.log("Ocean toolbar error: " + textStatus + ", " + error);
            jQuery("#ocean_div").show().css("padding", "5px").
            css("text-align", "center");
        }
    });
});

function eRefer(event) {
    let demographicNo = document.getElementById("demographicNo").serialize();
    let documents = getDocuments(event);
    let data = demographicNo + "&" + documents;
    jQuery.ajax({
        type: 'POST',
        url: document.getElementById("contextPath").value + '/oscarEncounter/eRefer.do',
        data: data,
        success: function(response) {
            console.log(response);
        }
    });
}

function getDocuments(event) {
    let documents = "";
    let attachments = document.getElementsByClassName("delegateAttachment");
    let isFormAttached = false;
    for (const attachment of attachments) {
        documents = documents + attachment.name.charAt(0).toUpperCase() + attachment.value + "|";
        if (attachment.name.charAt(0).toUpperCase() === 'F') { isFormAttached = true; }
    }

    if (isFormAttached) {
        const canProceed = confirm("Forms cannot be attached to Ocean referrals.\n\nWorkaround:\nCreate a PDF of the form and upload it to the patient's chart as a document. Then you can attach that document with the referral.\n\nTo proceed without attaching Form, please press \'OK\'");
        if (!canProceed) { 
            event.stopImmediatePropagation(); 
            documents = "";
        }
    }

    return "documents=" + documents;
}

