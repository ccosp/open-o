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

function eRefer() {
    let demographicNo = document.getElementById("demographicNo").serialize();
    let documents = getDocuments();
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

function getDocuments() {
    let documents = "";
    let attachments = document.getElementsByClassName("delegateAttachment");
    for (const attachment of attachments) {
        documents = documents + attachment.name.charAt(0).toUpperCase() + attachment.value + "|";
    }
    return "documents=" + documents;
}

