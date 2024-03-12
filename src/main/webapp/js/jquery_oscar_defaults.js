jQuery(document).ajaxError(function(event, request, options, error) { 
    // Ignoring /oscar/ws/rs/ocean/getSettings error
    if (options.url.endsWith("/ocean/getSettings")) { return; }
    alert('Error contacting server, please try again. \n'+options.url); 
});
