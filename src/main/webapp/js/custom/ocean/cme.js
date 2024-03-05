(function (script) {
  let oceanHost = script.attributes['ocean-host'];
  if (oceanHost && oceanHost.value) {
      window.oceanHost = decodeURIComponent(oceanHost.value);
  }
})(document.currentScript);

 jQuery(document).ready(function(){
   issueNoteUrls = {
       divR1I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=SocHistory&title=" + socHistoryLabel + "&cmd=divR1I1",
             divR1I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=MedHistory&title=" + medHistoryLabel + "&cmd=divR1I2",
             divR2I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=Concerns&title=" + onGoingLabel + "&cmd=divR2I1",
             divR2I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=Reminders&title=" + remindersLabel + "&cmd=divR2I2"                                         
     };

     init();
     // rather than highjacking a <div> lets put OCEAN where the OSCAR instance has allowance
     jQuery("#ocean_placeholder").append("<div id='ocean_div' style='width: 100%; display: none; font-size: 11px;'>Sorry, the Ocean toolbar is currently unavailable.</div>");

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


 function notifyIssueUpdate() {
   
 }

 function notifyDivLoaded(divId) {	  
 }
