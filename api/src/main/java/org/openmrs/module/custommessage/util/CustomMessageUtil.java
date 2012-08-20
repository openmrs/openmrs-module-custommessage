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
package org.openmrs.module.custommessage.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.custommessage.CustomMessageConstants;

/**
 * This class contains convenient methods for working with custom messages entities, related global
 * privileges, etc
 */
public class CustomMessageUtil {
	
	/**
	 * Determines whether module translation capabilities is enabled or not globally. The decision
	 * is made considering with value of global property specified by
	 * {@link CustomMessageConstants#GLOBAL_PROPERTY_INPLACE_CUSTOMIZATION_ENABLED}
	 * 
	 * @return true if in-place translation capabilities is enabled, false otherwise
	 */
	public static boolean isInplaceCustomizationEnabled() {
		return Boolean.valueOf(Context.getAdministrationService().getGlobalProperty(
		    CustomMessageConstants.GLOBAL_PROPERTY_INPLACE_CUSTOMIZATION_ENABLED, "false"));
	}
	
	/**
	 * Determines whether translate mode is enabled or not for current user. The decision is made
	 * considering with value of user property specified by
	 * {@link CustomMessageConstants#USER_PROPERTY_TRANSLATE_MODE_ENABLED}
	 * 
	 * @return true if translate mode is enabled, false otherwise
	 * @should return false if in-place customization disabled
	 * @should return false if there is no authenticated user
	 * @should return false if user does not have translate mode property
	 * @should return true if translate mode is enabled
	 */
	public static boolean isTranslateModeEnabled() {
		boolean isEnabled = isInplaceCustomizationEnabled();
		User currentUser = Context.getAuthenticatedUser();
		// if there is a user that is already logged in
		if (currentUser != null) {
			isEnabled = Boolean.valueOf(currentUser.getUserProperty(
			    CustomMessageConstants.USER_PROPERTY_TRANSLATE_MODE_ENABLED, "false"));
		} else {
			isEnabled = Boolean.FALSE;
		}
		return isEnabled;
	}
	
	/**
	 * Toggles the value of user property specified by
	 * {@link CustomMessageConstants#USER_PROPERTY_TRANSLATE_MODE_ENABLED}. If there is no value for
	 * current user, then new user property with value "true" will be created and saved into
	 * database. If there is no current user for method call, then {@link IllegalStateException}
	 * exception will be thrown
	 * 
	 * @should throw an exception if there is no authenticated user
	 * @should switch on translate mode for first time
	 * @should toggle translate mode value
	 */
	public static void toggleTranslateMode() {
		User currentUser = Context.getAuthenticatedUser();
		// if there is a user that is already logged in
		if (currentUser != null) {
			// toggle user's property and save it
			currentUser.setUserProperty(CustomMessageConstants.USER_PROPERTY_TRANSLATE_MODE_ENABLED,
			    String.valueOf(!isTranslateModeEnabled()));
		} else {
			throw new IllegalStateException("Can not toggle translate mode for not authorized user");
		}
	}
	
	/**
	 * Convenient method that check if translate mode is enabled, then it alters passed in message
	 * by enclosing it with span &lt;span class="translate"&gt; and &lt;/span&gt; tags, otherwise, it returns 
	 * 
	 * @param code the code of translatable message supplied by this method
	 * @param message the text message to be handled
	 * @return passed in <em>message</em> represented as "translatable" if translate mode is enabled, or original message otherwise
	 * 
	 * @should escape HTML within message
	 */
	public static String makeMessageTranslatable(String code, String message) {
		if (CustomMessageUtil.isTranslateModeEnabled()) {
			return String.format("<span class=translate code=%s>%s</span>", code, StringEscapeUtils.escapeHtml(message));
		} else {
			return message;
		}
	}
}
