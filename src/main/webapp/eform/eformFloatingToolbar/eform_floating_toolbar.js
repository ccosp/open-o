	document.addEventListener("DOMContentLoaded", function(){   
		/**
		 * Trigger these functions every time this page loads.
		 */

			removeElements();
			addNavElement();
			moveSubjectReverse();
			
			top.window.resizeTo("1100","850");
			
				/*
				 * A little trick to bypass the override of the onload
				 * event in the Rich Text Letter writer. 
				 */
		//		var loadmethod = document.getElementsByTagName("body")[0].getAttribute("onload"); 
		//		
		//		if(! loadmethod)
		//		{
		//			onload = document.getElementsByTagName("form")[0].getAttribute("onload"); 
		//		}
		//
		//		if(loadmethod)
		//		{
		//			loadmethod = loadmethod.replace("(", '').replace(")", "").replace(";", "");
		//			return window[loadmethod]();
		//		}
		//		
		//		return;
		//	}
	});	
	/**
	 * Triggers the eForm save/submit function
	 */
	function remoteSave() {
		
		moveSubject();
		
		if (typeof saveRTL === "function")
		{
			window["saveRTL"]();
			document.RichTextLetter.submit();
			return true;
		} 
		
		
		if (document.getElementsByName("SubmitButton") && 
				typeof document.getElementsByName("SubmitButton")[0].click() === "function") 
		{
			try
			{
				document.getElementsByName("SubmitButton")[0].click();
				return true;
			}
			catch(error) 
			{
				console.log(error);
			}
		}
		
		if(typeof releaseDirtyFlag === "function")
		{
			window["releaseDirtyFlag"]();
		}
		
		if(typeof submission === "function")
		{
			try
			{
				window["submission"]();
				document.getElementsByName("form")[0].submit();
				return true;
			} 
			catch(error) 
			{
				console.log(error);
			}
		} 
		
		try
		{
			document.getElementsByName("form")[0].submit();
			return true;
		} 
		catch(error) 
		{
			console.log(error);
		}

		return false;

	}
	
	/**
	 * Adds a hidden input field into the eForm form with instructions to 
	 * open the Oscar Fax dialog.
	 */
	function remoteFax() {	
		var newElement = document.createElement("input");
		newElement.setAttribute("id", "faxAction");
		newElement.setAttribute("name", "faxEForm");
		newElement.setAttribute("value", "true");
		newElement.setAttribute("type", "hidden");
		document.forms[0].appendChild(newElement);
			
		remoteSave();
	
	}
	
	/**
	 * Triggers the eForm print function
	 */
	function remotePrint() {
		
		if( typeof formPrint === "function" ) 
		{
			formPrint();
		} 
		else if(typeof printLetter === "function") 
		{
			printLetter(); 
		} 
		else if(document.getElementById('edit')) 
		{
			document.getElementById('edit').contentWindow.print();
		} 
		else if (document.getElementsByName("PrintButton") &&
				typeof document.getElementsByName("PrintButton")[0].click() === "function")
		{
			document.getElementsByName("PrintButton")[0].click();
		}
		else 
		{
			alert("Cannot print. Try the print button on the eForm.");
		}
		/*
		 * Needs to be saved if this is
		 * a new eForm or it has been altered.
		 */
		if(needToConfirm) {			
			remoteSave();
		} 
	
	}
	
	/**
	 * Adds a hidden input field into the eForm form with instructions to  
	 * to generate a PDF of this form and then to 
	 * save it into the eChart Documents directory.
	 */
	function remoteEdocument() {
		
		var edocElement = document.getElementById("saveAsEdoc");
		if(edocElement)
		{
			edocElement.value = 'true';
		}
		else
		{
			var newElement = document.createElement("input");
			newElement.setAttribute("id", "saveAsEdoc");
			newElement.setAttribute("name", "saveAsEdoc");
			newElement.setAttribute("value", "true");
			newElement.setAttribute("type", "hidden");
			document.forms[0].appendChild(newElement);
		}
		remoteSave();
	
	}
	
	/**
	 * Close the entire eForm window.
	 */
	function remoteClose() {
		window.close();
	}
	
	/**
	 * Move the eForm subject value from the remote tool bar into the
	 * eForm form. 
	 * Should be done just before the save process. 
	 */
	function moveSubject() {
		var remoteSubject = document.getElementById("remote_eform_subject");
		var remoteSubjectValue = remoteSubject.value;
		document.forms[0].elements["subject"].value = remoteSubjectValue;
	}
	
	function moveSubjectReverse() {
		var subjectElement = document.forms[0].elements["subject"];
		var subjectElementValue = subjectElement.value;
		if(subjectElementValue)
		{
			document.getElementById("remote_eform_subject").value = subjectElementValue;
		}
	}
	
	/**
	 * Close this toolbar. Exposes buttons and text that is 
	 * hidden underneath. 
	 * A button is still visible on the right side to 
	 * restore the toolbar.
	 */
	function closeToolbar() {
	
		var toolbarContainer = document.getElementById("eform_floating_toolbar");
		var toolbarNav = document.getElementById("eform_floating_toolbar_nav");
		if(toolbarContainer && toolbarNav) {
			toolbarNav.style.display = "none";
			
			toolbarContainer.style.display = "table";
			toolbarContainer.style.position = "fixed";
			toolbarContainer.style.opacity = "100%";
			toolbarContainer.style.zIndex = "1029";
			toolbarContainer.style.bottom = "0";
			toolbarContainer.style.right = "0";
			toolbarContainer.style.marginBottom = "0";
			
			var openToolbarButton = document.getElementById("openToolbarButton");
			openToolbarButton.style.display = "table";
			openToolbarButton.style.minHeight = "50px";
			
		}
	}
	
	/**
	 * Restore the floating toolbar. 
	 * @returns
	 */
	function openToolbar() {
		var openToolbarButton = document.getElementById("openToolbarButton");
		var toolbarNav = document.getElementById("eform_floating_toolbar_nav");
		var toolbarContainer = document.getElementById("eform_floating_toolbar");
		if( toolbarContainer && openToolbarButton && toolbarNav) {
			toolbarNav.style.display = "initial";			
			openToolbarButton.style.display = "none";
			toolbarContainer.style = "";
		}
	}

	/**
	 * Remove all fax control buttons from the current 
	 * eform to avoid any confusion on what fax system is being used. 
	 */
	function removeElements() {
	    var element = document.getElementById("faxControl");
	    
	    if(element)
	    {
	    	element.parentNode.removeChild(element);
	    }
	    	
	    element = document.getElementById("fax_button");
	    
	    if(element)
	    {
	    	element.parentNode.removeChild(element);
	    }
	    
	    element = document.getElementById("faxSave_button");
	    
	    if(element)
	    {
	    	element.parentNode.removeChild(element);
	    }
	    
	    element = document.getElementById("faxEForm");
	    
	    if(element)
	    {
	    	element.parentNode.removeChild(element); 
	    }
	
	}
	
	/**
	 * A javascript includes method 
	 * @returns
	 */
	function includeHTML(elmnt) {
		var file = "../eform/eformFloatingToolbar/eform_floating_toolbar.jspf";
		if (file) {
			var xhttp = new XMLHttpRequest();
			xhttp.onreadystatechange = function() {
				if (this.readyState == 4) {
					
					if (this.status == 200) 
					{
						var toolbarWrapper = document.createElement("div");
						toolbarWrapper.setAttribute("id","toolbarWrapper");
						toolbarWrapper.setAttribute("class","hidden-print DoNotPrint no-print");
						toolbarWrapper.innerHTML = this.responseText;
						elmnt.append(toolbarWrapper);
					}
					
					if (this.status == 404) 
					{
						elmnt.append("eForm tool bar not found.");
					}
				}
			}
			xhttp.open("GET", file, true);
			xhttp.send();
			/* Exit the function: */
			return;
		}
	}
	
	/**
	 * Insert additional elements into the eForm to support 
	 * launch of the floating toolbar.
	 */
	function addNavElement() {
		
		/*
		 * Get the total height of the current eform
		 */
		var body = document.body;
	    html = document.documentElement;
		var documentheight = Math.max( body.scrollHeight, body.offsetHeight, 
		                       html.clientHeight, html.scrollHeight, html.offsetHeight );
		
		/*
		 * Include the eForm tool bar overlay
		 */
		includeHTML(body);
		
		/*
		 * Add a wedge to the bottom of the eform that will add 
		 * 65 pixels to the bottom so that the eForm clears the remote button 
		 * panel
		 */
		var formelement = document.getElementsByTagName("form");
		var spacer = document.createElement("div");
		spacer.setAttribute("id","eformPageSpacer");
		spacer.setAttribute("class","hidden-print DoNotPrint no-print");
		spacer.style.position = "absolute";
		spacer.style.left = 0;
		spacer.style.top = documentheight + 50;
		spacer.style.width = "100%";
		spacer.style.margin = 0;
		spacer.style.padding = 0;
		spacer.style.height = "1px";
		formelement[0].appendChild(spacer);
		
		/*
		 * Place the new CSS style into the parent page. 
		 */
		var headelement = document.getElementsByTagName("head");
		var style = document.createElement("link");
		style.setAttribute("rel", "stylesheet");
		style.setAttribute("type", "text/css");
		style.setAttribute("href", "../library/bootstrap/3.0.0/css/eform_floating_toolbar_bootstrap_custom.min.css");
		headelement[0].appendChild(style);

	}
	