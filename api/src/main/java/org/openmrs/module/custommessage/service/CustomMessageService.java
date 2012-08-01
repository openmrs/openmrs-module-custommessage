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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openmrs.api.OpenmrsService;
import org.openmrs.messagesource.PresentationMessageMap;
import org.openmrs.module.custommessage.CustomMessage;
import org.openmrs.module.custommessage.MessagesLocation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for managing custom messages
 */
@Transactional
public interface CustomMessageService extends OpenmrsService {
	
	/**
	 * @return the CustomMessage with the given id
	 * @should return null if there is no object with given id
	 * @should return custom message by id
	 */
	public CustomMessage getCustomMessage(Integer id);
	
	/**
	 * @return the CustomMessage with the given uuid
	 * @should return null if no object found with given uuid
	 * @should return custom message by uuid
	 */
	public CustomMessage getCustomMessageByUuid(String uuid);
	
	/**
	 * @return all custom messages defined
	 * @should get all custom messages
	 */
	public List<CustomMessage> getAllCustomMessages();
	
	/**
	 * @return all custom messages for the associated code
	 * @should return empty list if no message found
	 * @should return custom messages for code
	 */
	public List<CustomMessage> getCustomMessagesForCode(String code);
	
	/**
	 * Looks for custom message by given code and locale
	 * 
	 * @param code custom message code to filter messages with
	 * @param locale the locale to filter custom messages in
	 * @return custom message for the associated code and locale
	 * @should return null if no message found
	 * @should return custom message for code and locale
	 */
	public CustomMessage getCustomMessagesForCodeAndLocale(String code, Locale locale);
	
	/**
	 * @param customMessage the Custom Message to save to the database
	 * @should create a new custom message in database
	 * @should update existing custom message in database
	 */
	public void saveCustomMessage(CustomMessage customMessage);
	
	/**
	 * @param customMessage the Custom Message to delete from the database
	 * @should fail if custom message is null
	 * @should delete custom message
	 */
	public void deleteCustomMessage(CustomMessage customMessage);
	
	/**
	 * @return all messages by locale
	 */
	public Map<Locale, PresentationMessageMap> getPresentationMessagesByLocale();
	
	/**
	 * @return all custom messages by code
	 */
	public Map<String, Map<Locale, CustomMessage>> getCustomMessagesByCode();
	
	/**
	 * Gets messages location by given string identifier of location.
	 * 
	 * @return the instance of {@link MessagesLocation} with the given id
	 * @should return null if there is no object with given id
	 * @should return messages location by id
	 */
	public MessagesLocation getMessagesLocation(String locationId);
	
	/**
	 * Gets messages location by given uuid of location.
	 * 
	 * @return the instance of {@link MessagesLocation} with the given uuid
	 * @should return null if no object found with given uuid
	 * @should return messages location by uuid
	 */
	public MessagesLocation getMessagesLocationByUuid(String uuid);
	
	/**
	 * Gets the list of all existing messages location within system
	 * 
	 * @return all messages locations defined within system
	 * @should get all messages locations
	 */
	public List<MessagesLocation> getAllMessagesLocations();
	
	/**
	 * Saves changes to database if given location already exists or creates new location within
	 * database if there is not location in database with <em>locationId</em> that equal to given
	 * one's
	 * 
	 * @param messagesLocation the messages location to save to the database
	 * @should create a new messages location in database
	 * @should update existing messages location in database
	 */
	public void saveMessagesLocation(MessagesLocation messagesLocation);
	
	/**
	 * Completely removes given location from the system. Any messages, related to this location
	 * will be unlinked from it
	 * 
	 * @param messagesLocation the messages locations to delete from the database
	 * @should delete messages location
	 */
	public void deleteMessagesLocation(MessagesLocation messagesLocation);
	
	/**
	 * Gets an identifier of the location of message by given code, if message location does not
	 * exists yet, it creates new instance, saves this instance to database and returns it
	 * 
	 * @param messageCode the code of message to resolve source with
	 * @return default location if message came from core or module id as source id if message with
	 *         given code came from any of started modules
	 * @should fail if message code is blank
	 * @should return default location for core message code
	 * @should return existing location by given code
	 * @should create new location by given code
	 */
	public MessagesLocation resolveLocationForCode(String messageCode);
	
	/**
	 * Gets the map of available messages locations. Messages location id is used as key, and location
	 * name - as value. Final map will be created as result of merging of messages locations from
	 * database and modules, started in system
	 * 
	 * @return map of messages locations available in system
	 * @should return map as result of merge of messages locations and started modules
	 */
	public Map<String, String> getAvailableMessagesLocationsMap();
}
