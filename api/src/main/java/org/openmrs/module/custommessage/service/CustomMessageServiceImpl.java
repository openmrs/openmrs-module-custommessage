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
package org.openmrs.module.custommessage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.messagesource.PresentationMessageMap;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.custommessage.CustomMessage;
import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.MessagesLocation;
import org.openmrs.module.custommessage.service.db.CustomMessageDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for managing custom messages
 */
@Transactional
public class CustomMessageServiceImpl extends BaseOpenmrsService implements CustomMessageService {
	
	private CustomMessageDAO dao;
	
	/**
	 * @return the CustomMessage with the given id
	 */
	@Transactional(readOnly = true)
	public CustomMessage getCustomMessage(Integer id) {
		return dao.getCustomMessage(id);
	}
	
	/**
	 * @return the CustomMessage with the given uuid
	 */
	@Transactional(readOnly = true)
	public CustomMessage getCustomMessageByUuid(String uuid) {
		return dao.getCustomMessageByUuid(uuid);
	}
	
	/**
	 * @return all custom messages defined
	 */
	@Transactional(readOnly = true)
	public List<CustomMessage> getAllCustomMessages() {
		return dao.getAllCustomMessages();
	}
	
	/**
	 * @return all custom messages for the associated code
	 */
	@Transactional(readOnly = true)
	public List<CustomMessage> getCustomMessagesForCode(String code) {
		return dao.getCustomMessagesForCode(code);
	}
	
	/**
	 * @param customMessage the Custom Message to save to the database
	 */
	public void saveCustomMessage(CustomMessage customMessage) {
		customMessage.setUpdatedBy(Context.getAuthenticatedUser());
		customMessage.setUpdatedDatetime(new Date());
		dao.saveCustomMessage(customMessage);
	}
	
	/**
	 * @param customMessage the Custom Message to delete from the database
	 */
	public void deleteCustomMessage(CustomMessage customMessage) {
		if (customMessage == null) {
			throw new IllegalArgumentException("Can not delete null entity");
		}
		dao.deleteCustomMessage(customMessage);
	}
	
	/**
	 * @return all custom messages as presentation messages by locale
	 */
	@Transactional(readOnly = true)
	public Map<Locale, PresentationMessageMap> getPresentationMessagesByLocale() {
		Map<Locale, PresentationMessageMap> ret = new HashMap<Locale, PresentationMessageMap>();
		for (CustomMessage m : dao.getAllCustomMessages()) {
			PresentationMessageMap pmm = ret.get(m.getLocale());
			if (pmm == null) {
				pmm = new PresentationMessageMap(m.getLocale());
				ret.put(m.getLocale(), pmm);
			}
			pmm.put(m.getCode(), m.toPresentationMessage());
		}
		return ret;
	}
	
	/**
	 * @return all custom messages by code
	 */
	@Transactional(readOnly = true)
	public Map<String, Map<Locale, CustomMessage>> getCustomMessagesByCode() {
		Map<String, Map<Locale, CustomMessage>> ret = new TreeMap<String, Map<Locale, CustomMessage>>();
		for (CustomMessage m : dao.getAllCustomMessages()) {
			Map<Locale, CustomMessage> pmm = ret.get(m.getCode());
			if (pmm == null) {
				pmm = new HashMap<Locale, CustomMessage>();
				ret.put(m.getCode(), pmm);
			}
			pmm.put(m.getLocale(), m);
		}
		return ret;
	}
	
	/**
	 * @return the dao
	 */
	public CustomMessageDAO getDao() {
		return dao;
	}
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(CustomMessageDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getCustomMessagesForCodeAndLocale(java.lang.String,
	 *      java.util.Locale)
	 */
	@Override
	@Transactional(readOnly = true)
	public CustomMessage getCustomMessagesForCodeAndLocale(String code, Locale locale) {
		return dao.getCustomMessagesForCode(code, locale);
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getMessagesLocation(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public MessagesLocation getMessagesLocation(String locationId) {
		return dao.getMessagesLocation(locationId);
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getMessagesLocationByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public MessagesLocation getMessagesLocationByUuid(String uuid) {
		return dao.getMessagesLocationByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getAllMessagesLocations()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<MessagesLocation> getAllMessagesLocations() {
		return dao.getAllMessagesLocations();
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#saveMessagesLocation(org.openmrs.module.custommessage.MessagesLocation)
	 */
	@Override
	public void saveMessagesLocation(MessagesLocation messagesLocation) {
		dao.saveMessagesLocation(messagesLocation);
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#deleteMessagesLocation(org.openmrs.module.custommessage.MessagesLocation)
	 */
	@Override
	public void deleteMessagesLocation(MessagesLocation messagesLocation) {
		dao.deleteMessagesLocation(messagesLocation);
	}
	
	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#resolveLocationForCode(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public MessagesLocation resolveLocationForCode(String messageCode) {
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		MessagesLocation messageLocation = null;
		// if message code is not specified raise an error
		if (StringUtils.isBlank(messageCode)) {
			throw new IllegalArgumentException();
		}
		// iterate over the set of successfully started modules
		for (Module module : ModuleFactory.getStartedModules()) {
			if (messageCode.startsWith(module.getModuleId().concat("."))) {
				messageLocation = customMessageService.getMessagesLocation(module.getModuleId());
				if (messageLocation == null) {
					// if location does not exist yet, create in and save into database
					messageLocation = new MessagesLocation(module.getModuleId(), module.getName());
					customMessageService.saveMessagesLocation(messageLocation);
				}
				return messageLocation;
			}
		}
		// return default location meaning that it's core message as no matching module id is found
		messageLocation = customMessageService.getMessagesLocation(CustomMessageConstants.CUSTOM_MESSAGES_LOCATION_DEFAULT_ID);
		if (messageLocation == null) {
			// if core location does not exist yet, create in and save into database
			messageLocation = new MessagesLocation(CustomMessageConstants.CUSTOM_MESSAGES_LOCATION_DEFAULT_ID,
			        CustomMessageConstants.CUSTOM_MESSAGES_LOCATION_DEFAULT_NAME);
			customMessageService.saveMessagesLocation(messageLocation);
		}
		return messageLocation;
	}

	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getAvailableMessagesLocationsMap()
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, String> getAvailableMessagesLocationsMap() {
		// add map of locations to model to be used on export page
		Map<String, String> locationMap = new TreeMap<String, String>();
		List<MessagesLocation> messagesLocations = getAllMessagesLocations();
		if (messagesLocations != null) {
			for (MessagesLocation messagesLocation : messagesLocations) {
				locationMap.put(messagesLocation.getLocationId(), messagesLocation.getName());
			}
		}
		// merge messages locations with list of started modules which can be considered as potential messages locations
		for (Module startedModule : ModuleFactory.getStartedModules()) {
			locationMap.put(startedModule.getModuleId(), startedModule.getName());
		}
		return locationMap;
	}
}
