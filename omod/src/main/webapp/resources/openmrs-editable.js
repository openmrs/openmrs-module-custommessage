
// global variable that keeps reference to last edited in-line element
var lastEdited;

/**
 * Handles given translate mode value
 */
function handleTranslateMode(translateMode) {
	
	// move translate button from the page header to footer
	moveTranslateButton();
	
	// if translate mode is enabled
    if (translateMode) {

    	// make all inputs to be in-line editable buttons 
    	makeEditableButtons();
    	// make all in-line editable links to have special shift-key holding down dependent click handler
    	rearrangeEditableLinks();
    	// sanitize all spans in order to avoid possible break of existing widgets on page
    	sanitizeSpans();
    	
    	// make translatable text to be editable
    	jQuery("span.translate").editable(saveMessage, 
    	{
    		id		: "code",
    		style	: "inherit",
    		data 	: getMessage,
    		onblur	: handleBlur,
    		onedit  : editHook,
    		onreset : resetHook
    	});
    	
        
        jQuery(document).bind("keydown", function(e) {
        	// if user presses shift key, temporary disable
        	// translate mode until he releases it
            if (editHook(null, null, e)) {
            	translateMode = false;
                jQuery("#translateButton").val("Translate: ON (press " + translateKey() + " to activate)");
                jQuery(".translate").each(function(){ 
                	jQuery(this).addClass("customizable"); 
                });
            }
        });

        jQuery(document).bind("keyup", function(e) {
        	// if user releases shift key, enable
        	// translate mode again
            if (editHook(null, null, e)) {
            	translateMode = true;
            	jQuery(".translate").each(function(){ 
                	jQuery(this).removeClass("customizable"); 
                });
                jQuery("#translateButton").val("Translate: ON (press " + translateKey() + " to activate)");
            }
        });    	
    }
    // set corresponding text as caption of translate button
    jQuery("#translateButton").val("Translate: " + (translateMode ? "ON (press " + translateKey() + " to activate)" : "OFF"));
    // toggle translate mode on/off
    jQuery("#translateButton").click(function(e) {
        translateMode = !translateMode;
        // call dwr service to proceed with toggling
        DWRCustomMessageService.toggleTranslateMode({async: false});
        jQuery(this).val("Translate: " + (translateMode ? "ON (press " + translateKey() + " to activate)" : "OFF"));
        location.reload();
	});
    
}

/**
 * Makes all inputs and buttons to be buttons having tag body, but skip input
 * with #translateButton in this case
 */
function makeEditableButtons() {
	jQuery("input[type=button],input[type=submit]").not("#translateButton").each(function(e) {
		var button = jQuery('<button />');
		// preserve bounded click handlers and onclick attribute on button element  
		// to be called, when user presses button holding down shift key
		var clickHandlers = [];
		// push onclick handler attribute into click handlers array as first element
		if (jQuery(this).attr("onclick")) {
			clickHandlers.push(jQuery(this).attr("onclick"));
		}
		// then iterate over existing click handlers and push them into array one by one
		if (jQuery(this).data("events")) {
			jQuery.each(jQuery(this).data("events"), function() {
			    jQuery.each(this, function(j, h) {
			    	clickHandlers.push(h.handler);
			    });
			});
		}
		
		// customize click listener on button element in order to fix https://tickets.openmrs.org/browse/CSTM-13
		button.click(function(event) {
			// allow button click only when shift key is not being held down
	        if (editHook(null, null, event)) {
	            event.preventDefault();
    	         // mozilla assumes that click event is received from button rather
    	         // than from inner span element, so, trigger that event on span manually 
	             var element = jQuery('span.translate', this);
    	         if (event.target == this && element && lastEdited != element[0]) {
    	        	 element.click();
    	         } else if (element && !element[0].editing){
    	        	 // reset last edited element in order to allow further editing of this element
    	        	 lastEdited = null;
    	         }
	        } else {
	        	// call function that was set as onclick attribute on button and all preserved click handlers
	        	jQuery.each(clickHandlers, function(index, handler) {
	        		if (jQuery.isFunction(handler)) {
	        			handler.apply(this, [event]);
                    }
	    		});
	        }
	    });
		button.attr("id", jQuery(this).attr("id"));
		button.html(jQuery(this).val());
		jQuery(this).replaceWith(button);
	});
}

/**
 * Makes all in-line editable (having inner "span.translate" elements) <a/>
 * tags to have special click handler that activates natural link behaviour only
 * when shift key is being hold down
 */
function rearrangeEditableLinks() {
	jQuery("a").each(function () {
		// if <a/> element has inner span with class "translate"
		if (jQuery(this).children("span.translate").length > 0) {
			// customize it's click handler in order to disable natural
			// link behavior called on click if shift key is not held down
			jQuery(this).click(function(event, isSyntetic) {
				if (!isSyntetic) {
					event.preventDefault();
					if (!editHook(null, null, event)
					  && !jQuery(this).children("span.translate")[0].editing) {
						jQuery(this).trigger("click", [true]);
					}
				} else {
					// since it is a syntetic call of this handler
					// and shift key was pressed, avoid opening new
					// window and simulate simple link click
					window.location.href = jQuery(this).attr("href");
				}
			});
		}
	});
}

/**
 * Sanitizes all spans having in-place editable tag body in textual representation
 */
function sanitizeSpans() {
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
	/* save current this element as last edited */
	lastEdited = this;
	/* read message synchronously, using openmrs dwr custom message
	 * service */
	DWRCustomMessageService.get(jQuery(this).attr('code'), '', {
		async : false,
		callback : function(result) {
			if (result) {
				message = result;
				// use the result returned from the server as revert text
				// to avoid the problem described in https://tickets.openmrs.org/browse/CSTM-23
				lastEdited.revertText = result;
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
	if (value != this.revertText) {
		var input = jQuery("input", inLineForm);
		var self = this;
		// create dynamic confirmation dialog about loosing changes
		var confirmDialog = jQuery("<div id=confirmDialog title=" + confirmationMessageTitle + ">" + confirmationMessageText + "</div>");
		
		// create buttons with localized captions
		var buttonOpts = {};
		buttonOpts[confirmationYes] = function() { 
    		resetHook(self, settings);
	        jQuery(this).dialog("close");
    	}
		buttonOpts[confirmationNo] = function() {
    		jQuery(this).dialog("close");
    		input.focus();
    	}
		
		var dialogElement = confirmDialog.dialog( {
		    buttons: buttonOpts,
		    close: function(e) { 
		    	// escape should "undo" the dialog
		    	if (e.which == 27) {
		    		input.focus();
		    	}
		    },
		    autoOpen: false,
		    modal: true
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
		resetHook(this, settings);
	}
}

/**
 * Resets in-line editable element within passed in container
 * 
 * @param container
 *            the container that contains in-line input to be reset
 * @param settings
 *            the complete setting instance received from Jeditable plugin
 */
function resetHook(container, settings) {
	// cancel in-line form editing
	jQuery(container).html(container.revert);
	container.editing   = false;
    if (!jQuery.trim(jQuery(container).html())) {
    	jQuery(container).html(settings.placeholder);
    }
    /* show tooltip again. */
    if (settings.tooltip) {
        jQuery(container).attr('title', settings.tooltip);                
    }
}

/**
 * Determines if in-line editing can occur or not. This function will be used to
 * hook to edit handler on editable element
 * 
 * @param container
 *            the container that contains in-line input to be reset
 * @param settings
 *            complete setting instance received from Jeditable plugin
 * @param event
 *            the event object that has triggered handler on editable element
 * @returns true if in-line editing is allowed
 */
function editHook(settings, container, e) {
    var os = (function() {
      var ua = navigator.userAgent.toLowerCase();
      return {
          isWindows: /win/.test(ua),
          isLinux: /linux/.test(ua),
          isMac: /mac/.test(ua)
      };
    }());
    if (os.isMac) {
        // JQuery provides e.metaKey to detect Command key, but different
        // browsers use different keycodes for the Command key(s)
        if (jQuery.browser.mozilla)
            // Firefox defines both Command keys as keycode 224
            return e.which == 224 || window.event.metaKey;
        else if (jQuery.browser.opera)
            // Opera defines both Command keys as keycode 17
            return e.which == 17 || window.event.metaKey;
        else
            // Other browsers (Chrome/Safari/IE) define left Command as 91, right as 93
            return e.which == 91 || e.which == 93 || window.event.metaKey;
    } else {
        // Check for Ctrl key via keycode or e.ctrlKey
        return e.which == 17 || e.ctrlKey;
    }
}

/**
 * Finds translate button within DOM, clones it and moves to footer strip, where
 * language bar should be
 */
function moveTranslateButton() {
	jQuery("#translateButton").prependTo("#localeOptions");
}

/**
 * Gets the name of key to be used to activate in-line editing/highlighting depending on user agent
 * @returns the name of key to use for activating in-line translate
 */
function translateKey () {
	var ua = navigator.userAgent.toLowerCase();
	return /mac os/.test(ua) ? "CMD" : "CTRL";
}