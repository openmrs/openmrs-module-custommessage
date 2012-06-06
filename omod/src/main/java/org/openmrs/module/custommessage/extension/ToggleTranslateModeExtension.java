/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.custommessage.extension;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.CustomMessageUtil;
import org.openmrs.web.WebConstants;

/**
 * This class defines the button that will appear on the each page within footer for toggling
 * translate mode on/off. This extension is enabled by defining it in the config.xml file.
 */
public class ToggleTranslateModeExtension extends Extension {
	
	/**
	 * @see org.openmrs.module.Extension#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
	 */
	public String getRequiredPrivilege() {
		return CustomMessageConstants.PRIVILEGE_MANAGE_CUSTOM_MESSAGES;
	}
	
	/**
	 * @see org.openmrs.module.Extension#getOverrideContent(java.lang.String)
	 */
	@Override
	public String getOverrideContent(String bodyContent) {
		User currentUser = Context.getAuthenticatedUser();
		// if in-place customization is being globally enabled and 
		// current user is granted to do messages customization
		if (CustomMessageUtil.isInplaceCustomizationEnabled() && currentUser != null
		        && currentUser.hasPrivilege(getRequiredPrivilege())) {
			String contextPath = WebConstants.WEBAPP_NAME;
			String translateModeStatus = currentUser.getUserProperty(
			    CustomMessageConstants.USER_PROPERTY_TRANSLATE_MODE_ENABLED, "false");
			return String
			        .format(
			            "<script src=\"/%s/dwr/interface/DWRCustomMessageService.js\"></script>\n<script  type=\"text/javascript\">\njQuery(document).ready(function() {\n\n    var translateMode = %s;\n    jQuery(\"#translateButton\").val(\"Translate: \" + (translateMode ? \"ON\" : \"OFF\"));\n    // Toggle translate mode on/off\n    jQuery(\"#translateButton\").click(function(e) {\n        translateMode = !translateMode;\n        DWRCustomMessageService.toggleTranslateMode();\n        jQuery(this).val(\"Translate: \" + (translateMode ? \"ON\" : \"OFF\"));\n        location.reload();\n   });\n\n});\n</script>\n<input type=\"button\" id=\"translateButton\">",
			            contextPath, translateModeStatus);
		} else {
			return bodyContent;
		}
	}
	
}
