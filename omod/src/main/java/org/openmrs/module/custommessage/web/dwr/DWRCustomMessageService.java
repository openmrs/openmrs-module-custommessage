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
package org.openmrs.module.custommessage.web.dwr;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.custommessage.CustomMessage;
import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.CustomMessageSource;
import org.openmrs.module.custommessage.service.CustomMessageService;
import org.openmrs.module.custommessage.util.CustomMessageUtil;
import org.openmrs.util.LocaleUtility;

/**
 * DWR based service class that exposes functionality for managing custom messages to be available
 * via AJAX calls
 */
public class DWRCustomMessageService {
	
	/**
	 * This method simply turns on/off translate mode by changing value of user property identified
	 * by {@link CustomMessageConstants#USER_PROPERTY_TRANSLATE_MODE_ENABLED} constant
	 */
	public void toggleTranslateMode() {
		CustomMessageUtil.toggleTranslateMode();
	}
	
	/**
	 * Performs save operation of given text <em>value</em> using passed in <em>key</em> and
	 * <em>language</em> parameters
	 * 
	 * @param code the key to save message by
	 * @param message the text of message to be saved using given key (if blank string is passed in
	 *            the custom message will be removed)
	 * @param locale <b>(optional)</b> the locale to save message in (if it is not set, then current
	 *            system locale will be used)
	 */
	public String save(String code, String message, String locale) {
		
		// if message key is not set then raise an error immediately
		if (StringUtils.isBlank(code)) {
			throw new IllegalArgumentException("Message code can not be blank");
		}
		
		Locale messageLocale = null;
		// check if language parameter is set
		if (StringUtils.isNotBlank(locale)) {
			messageLocale = LocaleUtility.fromSpecification(locale);
		}
		
		// if message locale is still null set it to current
		if (messageLocale == null) {
			messageLocale = Context.getLocale();
		}
		
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		
		// treat given key and messageLocale as search parameters for custom message lookup
		CustomMessage customMessage = customMessageService.getCustomMessagesForCodeAndLocale(code, messageLocale);
		
		// if custom message for given parameters does not exists, create it, otherwise update existing
		if (customMessage != null) {
			customMessage.setMessage(message);
		} else {
			customMessage = new CustomMessage();
			customMessage.setCode(code);
			customMessage.setLocale(messageLocale);
			customMessage.setMessage(message);
			customMessage.setMessageLocation(customMessageService.resolveLocationForCode(code));
		}
		
		// if passed in text is not blank, save message
		boolean hasChanged = Boolean.TRUE;
		if (StringUtils.isNotBlank(message)) {
			customMessageService.saveCustomMessage(customMessage);
		} else if (customMessage.getId() != null) {
			// otherwise, if message exists for given locale and code, remove it
			customMessageService.deleteCustomMessage(customMessage);
		} else {
			hasChanged = Boolean.FALSE;
		}
		
		// if customization changed, refresh the cache used by message source
		if (hasChanged) {
			// have to do it as long as we need refresh of custom messages cache after each save operation
			((CustomMessageSource) Context.getMessageSourceService().getActiveMessageSource()).refreshCache();
		}
		
		// return the text of the message that has been saved,
		// if message has been removed, return corresponding non-customized text from message source
		return StringUtils.isNotBlank(message) ? message : get(code, locale);
	}
	
	/**
	 * Gets the text of message specified by passed in <em>key</em> and <em>language</em> parameters
	 * 
	 * @param code the key to read message by
	 * @param locale <b>(optional)</b> the locale to save message in (if it is not set, then current
	 *            system locale will be used)
	 * @return the text of message found using given key
	 */
	public String get(String code, String locale) {
		
		// if message key is not set then raise an error immediately
		if (StringUtils.isBlank(code)) {
			throw new IllegalArgumentException("Message code can not be blank");
		}
		
		Locale messageLocale = null;
		// check if language parameter is set
		if (StringUtils.isNotBlank(locale)) {
			messageLocale = LocaleUtility.fromSpecification(locale);
		}
		
		// if message locale is still null set it to current
		if (messageLocale == null) {
			messageLocale = Context.getLocale();
		}
		
		// resolve message using active message source without passing in an argument array
		return Context.getMessageSourceService().getActiveMessageSource().getMessage(code, null, messageLocale);
	}
	
}
