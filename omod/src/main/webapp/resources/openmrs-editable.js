
/**
 * Handles given translate mode value
 */
function handleTranslateMode(translateMode) {
	// if translate mode is enabled
    if (translateMode) {
    	// make all inputs and buttons to be buttons having tag body 
    	// but skip input with #translateButton in this case
    	jQuery("input[type=button],input[type=submit]").not("#translateButton").each(function(e) {
    		var button = jQuery('<button />');
    		button.attr("id", jQuery(this).attr("id"));
    		button.html(jQuery(this).val());
    		jQuery(this).replaceWith(button);
    	});
    	// sanitize spans having in-place editable
    	// tag body in textual representation
    	jQuery("span").each(function() {
    		jQuery(this).contents().filter(function() {
	    	    return this.nodeType == Node.TEXT_NODE;
	    	}).each(function (index) {
	    		var bodyStr = jQuery(this).text();
	    		// if element has a body, and this is a span, handle it
	    		if (bodyStr && (bodyStr == bodyStr.match(new RegExp("<\s*span[^>]*>(.*?)<\s*/\s*span>", "g")))) {
	    			var element = jQuery(bodyStr);
	    			// if element can be created off the body text
	    			// and it is a span having .translate class
	    			if (element && element.is("span.translate")) {
	    				// add span to DOM and remove text node
	    	        	jQuery(this).parent().append(element);
	    	        	jQuery(this).remove();
	    			}
	    		}
	        })
    	});
    	
    	 // add custom input type in order to fix https://tickets.openmrs.org/browse/CSTM-13
        jQuery.editable.addInputType('customtext', {
        	element : function(settings, original) {
                var input = jQuery('<input />');
                if (settings.width  != 'none') { input.attr('width', settings.width);  }
                if (settings.height != 'none') { input.attr('height', settings.height); }
                /* https://bugzilla.mozilla.org/show_bug.cgi?id=236791 */
                //input[0].setAttribute('autocomplete','off');
                input.attr('autocomplete','off');
                // disable form submitting when space-bar is pressed
                input.bind("keypress", function(event) {
                	if (event.keyCode == 32) {
                		event.stopPropagation();
                	}
                });
                jQuery(this).append(input);
                return(input);
            }
        });
        
    	// make translatable text to be editable
    	jQuery("span.translate").editable(saveMessage, 
    	{
    		id		: "code",
    		style	: "inherit",
    		data 	: getMessage,
    		type	: "customtext",
    		onblur	: handleBlur
    	});
    }
    // set corresponding text as caption of translate button
    jQuery("#translateButton").val("Translate: " + (translateMode ? "ON" : "OFF"));
    // toggle translate mode on/off
    jQuery("#translateButton").click(function(e) {
        translateMode = !translateMode;
        // call dwr service to proceed with toggling
        DWRCustomMessageService.toggleTranslateMode();
        jQuery(this).val("Translate: " + (translateMode ? "ON" : "OFF"));
        location.reload();
	});
    
}

/**
 * Called after user submits in-line edits. Sends already edited in-line message
 * to the server and saving it
 * 
 * @param value
 *            the edited content
 * @param settings
 *            the complete setting instance received from Jeditable plugin
 * @returns the message that has been saved in case of successfully called dwr
 *          or passed in value otherwise
 */
function saveMessage(value, settings) {
	/* as fallback use just edited content */
	var savedMessage = value;
	/* synchronously sent data to server using openmrs dwr custom message
	 * service */
	DWRCustomMessageService.save(jQuery(this).attr('code'), value, '', {
		async : false,
		callback : function(result) {
			if (result) {
				savedMessage = result;
			}
		}
	});
	return savedMessage;
}

/**
 * Called right before each in-line edit. Reads message for code,
 * specified as attribute of internal this arg of function
 * 
 * @param value
 *            the text to be edited in-line
 * @param settings the complete setting instance received from Jeditable plugin
 * @returns received data from server in case of success or given value otherwise
 */
function getMessage(value, settings) {
	/* as fallback use current element text */
	var message = value;
	DWRCustomMessageService.get(jQuery(this).attr('code'), '', {
		async : false,
		callback : function(result) {
			if (result) {
				message = result;
			}
		}
	});
	return message;
}

/**
 * Called right after user blurs the input element, shows confirmation dialog about possible loosing of changes if user made some edit of text inside input 
 * 
 * @param value
 *            the text to be edited in-line
 * @param settings the complete setting instance received from Jeditable plugin
 */
function handleBlur(value, settings) {
	// if in-line input value has changed 
	var inLineForm = jQuery("form", this);
	if (value != this.revert) {
		var input = jQuery("input", inLineForm);
		var self = this;
		// create dynamic confirmation dialog about loosing changes
		var confirmDialog = jQuery("<div id=confirmDialog title='Confirm loosing changes'>Are you sure you want to cancel in-line editing ?</div>");
		var dialogElement = confirmDialog.dialog( {
		    buttons: {
            	"Yes" : function() { 
            		// cancel in-line form editing
            		jQuery(self).html(self.revert);
    		    	self.editing   = false;
    		        if (!jQuery.trim(jQuery(self).html())) {
    		        	jQuery(self).html(settings.placeholder);
    		        }
    		        /* Show tooltip again. */
    		        if (settings.tooltip) {
    		            jQuery(self).attr('title', settings.tooltip);                
    		        }
    		        jQuery(this).dialog("close");
            	}, 
            	"No"  : function() {
            		input.focus();
            		jQuery(this).dialog("close");
            	}
		    },
		    closeOnEscape: false,
		    autoOpen: false
		} );
		// IE has it's own opinion about jQuery dialog showing, so, use lazy opening
		dialogElement.dialog("open");
		// disable X close button for this dialog
		dialogElement.dialog( "widget" ).find( ".ui-dialog-titlebar-close" ).hide();
		// place dialog beside an input element
		dialogElement.dialog( "widget" ).position({ 
	        my			: 'left top',
	        at			: 'right bottom',
	        of			: input,
	        collision	: 'fit'
	    });
	} else {
		// cancel in-line form editing
		jQuery(this).html(this.revert);
		this.editing   = false;
        if (!jQuery.trim(jQuery(this).html())) {
        	jQuery(this).html(settings.placeholder);
        }
        /* Show tooltip again. */
        if (settings.tooltip) {
            jQuery(this).attr('title', settings.tooltip);                
        }
	}
}