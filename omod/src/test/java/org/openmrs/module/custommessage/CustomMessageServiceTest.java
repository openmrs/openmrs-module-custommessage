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
package org.openmrs.module.custommessage;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.custommessage.service.CustomMessageService;
import org.openmrs.module.custommessage.util.CustomMessageUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.LocaleUtility;

/**
 * This class contains unit tests of {@link CustomMessageService} class
 */
public class CustomMessageServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET_FILE_NAME = "CustomMessageServiceTest-testDataset.xml";
	
	private CustomMessageService customMessageService;
	
	@Before
	public void before() throws Exception {
		executeDataSet(DATASET_FILE_NAME);
		customMessageService = Context.getService(CustomMessageService.class);
	}
	
	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(CustomMessageService.class));
	}
	
	/**
	 * @see CustomMessageService#getCustomMessage(Integer)
	 */
	@Test
	@Verifies(value = "should return null if there is no object with given id", method = "getCustomMessage(Integer)")
	public void getCustomMessage_shouldReturnNullIfThereIsNoObjectWithGivenId() {	
		assertNull(customMessageService.getCustomMessage(9999));
	}
	
	/**
	 * @see CustomMessageService#getCustomMessage(Integer)
	 */
	@Test
	@Verifies(value = "should return custom message by id", method = "getCustomMessage(Integer)")
	public void getCustomMessage_shouldReturnCustomMessageById() {	
		CustomMessage customMessage = customMessageService.getCustomMessage(1);
		assertNotNull(customMessage);
	}
	
	/**
	 * @see CustomMessageService#getCustomMessageByUuid(String)
	 */
	@Test
	@Verifies(value = "should return null if no object found with given uuid", method = "getCustomMessageByUuid(String)")
	public void getCustomMessageByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {	
		assertNull(customMessageService.getCustomMessageByUuid("invalid uuid"));
	}
	
	/**
	 * @see CustomMessageService#getCustomMessageByUuid(String)
	 */
	@Test
	@Verifies(value = "should return custom message by uuid", method = "getCustomMessageByUuid(String)")
	public void getCustomMessageByUuid_shouldReturnCustomMessageByUuid() {	
		assertNotNull(customMessageService.getCustomMessageByUuid("a2e25607-d0c4-4e44-8be6-31e1ac7e100d"));
	}
	
	/**
	 * @see CustomMessageService#getAllCustomMessages()
	 */
	@Test
	@Verifies(value = "should get all custom messages", method = "getAllCustomMessages()")
	public void getAllCustomMessages_shouldGetAllCustomMessages() {	
		List<CustomMessage> actual = customMessageService.getAllCustomMessages();
		assertNotNull(actual);
		assertEquals(19, actual.size());
	}
	
	/**
	 * @see CustomMessageService#getCustomMessagesForCode(String)
	 */
	@Test
	@Verifies(value = "should return empty list if no message found", method = "getCustomMessagesForCode(String)")
	public void getCustomMessagesForCode_shouldReturnEmptyListIfNoMessageFound() {	
		List<CustomMessage> actual = customMessageService.getCustomMessagesForCode("wrong.code");
		assertNotNull(actual);
		assertEquals(0, actual.size());
	}
	
	/**
	 * @see CustomMessageService#getCustomMessagesForCode(String)
	 */
	@Test
	@Verifies(value = "should return custom messages for code", method = "getCustomMessagesForCode(String)")
	public void getCustomMessagesForCode_shouldReturnCustomMessagesForCode() {	
		List<CustomMessage> actual = customMessageService.getCustomMessagesForCode("test.code");
		assertNotNull(actual);
		assertEquals(6, actual.size());
	}
	
	/**
	 * @see CustomMessageService#getCustomMessagesForCodeAndLocale(String, java.util.Locale)
	 */
	@Test
	@Verifies(value = "should return null if no message found", method = "getCustomMessagesForCodeAndLocale(String, java.util.Locale)")
	public void getCustomMessagesForCodeAndLocale_shouldReturnNullIfNoMessageFound() {	
		CustomMessage actual = customMessageService.getCustomMessagesForCodeAndLocale("wrong.test.code", LocaleUtility.fromSpecification("fr"));
		assertNull(actual);
	}
	
	/**
	 * @see CustomMessageService#getCustomMessagesForCodeAndLocale(String, java.util.Locale)
	 */
	@Test
	@Verifies(value = "should return custom message for code and locale", method = "getCustomMessagesForCodeAndLocale(String, java.util.Locale)")
	public void getCustomMessagesForCodeAndLocale_shouldReturnCustomMessagesForCodeAndLocale() {	
		CustomMessage actual = customMessageService.getCustomMessagesForCodeAndLocale("test.code", LocaleUtility.fromSpecification("fr"));
		assertNotNull(actual);
	}
	
	/**
	 * @see CustomMessageService#saveCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should create a new custom message in database", method = "saveCustomMessage(CustomMessage)")
	public void saveCustomMessage_shouldCreateNewCustomMessageInDatabase() {	
		int beforeSize = customMessageService.getAllCustomMessages().size();
		CustomMessage customMessage = new CustomMessage();
		customMessage.setCode("new.code");
		customMessage.setLocale(Context.getLocale());
		customMessage.setMessage("test message");
		customMessage.setMessageLocation(customMessageService.getMessagesLocation("test"));
		customMessageService.saveCustomMessage(customMessage);
		assertEquals(beforeSize + 1, customMessageService.getAllCustomMessages().size());
	}
	
	/**
	 * @see CustomMessageService#saveCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should update existing custom message in database", method = "saveCustomMessage(CustomMessage)")
	public void saveCustomMessage_shouldUpdateExistingCustomMessageInDatabase() {	
		CustomMessage customMessage = customMessageService.getCustomMessage(1);
		String beforeMessage = customMessage.getMessage();
		String actualMessage = "test message";
		customMessage.setMessage(actualMessage);
		customMessageService.saveCustomMessage(customMessage);
		// check that message attribute has changed
		customMessage = customMessageService.getCustomMessage(1);
		assertNotSame(beforeMessage, customMessage.getMessage());
		assertEquals(actualMessage, customMessage.getMessage());
	}
	
	/**
	 * @see CustomMessageService#deleteCustomMessage(CustomMessage)
	 */
	@Test(expected=IllegalArgumentException.class)
	@Verifies(value = "should fail if custom message is null", method = "deleteCustomMessage(CustomMessage)")
	public void deleteCustomMessage_shouldFailIfCustomMessageIsNull() {	
		customMessageService.deleteCustomMessage(null);
	}
	
	/**
	 * @see CustomMessageService#deleteCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should delete custom message", method = "deleteCustomMessage(CustomMessage)")
	public void deleteCustomMessage_shouldDeleteCustomMessage() {	
		int beforeSize = customMessageService.getAllCustomMessages().size();
		CustomMessage customMessage = customMessageService.getCustomMessage(1);
		assertNotNull(customMessage);
		customMessageService.deleteCustomMessage(customMessage);
		assertEquals(beforeSize - 1, customMessageService.getAllCustomMessages().size());
	}
	
	/**
	 * @see CustomMessageUtil#resolveLocationForCode(String)
	 */
	@Test(expected=IllegalArgumentException.class)
	@Verifies(value = "should fail if message code is blank", method = "resolveLocationForCode(String)")
	public void resolveLocationForCode_shouldFailIfMessageCodeIsBlank() {
		customMessageService.resolveLocationForCode(null);
	}
	
	/**
	 * @see CustomMessageUtil#resolveLocationForCode(String)
	 */
	@Test
	@Verifies(value = "should return default location for core message code", method = "resolveLocationForCode(String)")
	public void resolveLocationForCode_shouldReturnDefaultLocationForCoreMessageCode() {
		MessagesLocation messagesLocation = customMessageService.resolveLocationForCode("test.code");
		assertNotNull(messagesLocation);
		assertSame(messagesLocation.getLocationId(), CustomMessageConstants.CUSTOM_MESSAGES_LOCATION_DEFAULT_ID);
	}
	
	/**
	 * @see CustomMessageUtil#resolveLocationForCode(String)
	 */
	@Test
	@Verifies(value = "should return existing location by given code", method = "resolveLocationForCode(String)")
	public void resolveLocationForCode_shouldReturnExistingLocationByGivenCode() {
		String mockModuleId = "test";
		// assert that location already exists
		MessagesLocation expectedLocation = customMessageService.getMessagesLocation(mockModuleId);
		assertNotNull(expectedLocation);
		// intentionally put mock module as value with mockModuleId key to the map of started within system modules
		ModuleFactory.getStartedModulesMap().put(mockModuleId, new Module("Test module", mockModuleId, "", "", "", ""));
		MessagesLocation messagesLocation = customMessageService.resolveLocationForCode(String.format("%s.code", mockModuleId));
		// assert that module id can be properly resolved from the code
		assertNotNull(messagesLocation);
		assertSame(expectedLocation, messagesLocation);
		// get rid of entry with mockModuleId from the map of started modules
		ModuleFactory.getStartedModulesMap().remove(mockModuleId);
	}
	
	/**
	 * @see CustomMessageUtil#resolveLocationForCode(String)
	 */
	@Test
	@Verifies(value = "should return existing location by given code", method = "resolveLocationForCode(String)")
	public void resolveLocationForCode_shouldCreateNewLocationByGivenCode() {
		String mockModuleId = "custommessages";
		// assert the there is no location by mock module id
		MessagesLocation location = customMessageService.getMessagesLocation(mockModuleId);
		assertNull(location);
		// intentionally put mock module as value with mockModuleId key to the map of started within system modules
		ModuleFactory.getStartedModulesMap().put(mockModuleId, new Module("Test module", mockModuleId, "", "", "", ""));
		MessagesLocation messagesLocation = customMessageService.resolveLocationForCode(String.format("%s.code", mockModuleId));
		// assert that module id can be properly resolved from the code
		assertNotNull(messagesLocation);
		assertSame(mockModuleId, messagesLocation.getLocationId());
		// get rid of entry with mockModuleId from the map of started modules
		ModuleFactory.getStartedModulesMap().remove(mockModuleId);
	}
	
	/**
	 * @see CustomMessageService#getMessagesLocation(String)
	 */
	@Test
	@Verifies(value = "should return messages location by id", method = "getMessagesLocation(String)")
	public void getMessagesLocation_shouldReturnMessagesLocationById() {	
		MessagesLocation messagesLocation = customMessageService.getMessagesLocation("test");
		assertNotNull(messagesLocation);
	}
	
	/**
	 * @see CustomMessageService#getMessagesLocation(String)
	 */
	@Test
	@Verifies(value = "should return null if there is no object with given id", method = "getMessagesLocation(String)")
	public void getMessagesLocation_shouldReturnNullIfThereIsNoObjectWithGivenId() {	
		assertNull(customMessageService.getMessagesLocation("wrongId"));
	}
	
	/**
	 * @see CustomMessageService#getMessagesLocationByUuid(String)
	 */
	@Test
	@Verifies(value = "should return null if no object found with given uuid", method = "getMessagesLocationByUuid(String)")
	public void getMessagesLocationByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {	
		assertNull(customMessageService.getMessagesLocationByUuid("invalid uuid"));
	}
	
	/**
	 * @see CustomMessageService#getMessagesLocationByUuid(String)
	 */
	@Test
	@Verifies(value = "should return messages location by uuid", method = "getMessagesLocationByUuid(String)")
	public void getMessagesLocationByUuid_shouldReturnMessagesLocationByUuid() {	
		assertNotNull(customMessageService.getMessagesLocationByUuid("b2e65607-d0c4-4e44-8be6-31e1ac7e199d"));
	}
	
	/**
	 * @see CustomMessageService#getAllMessagesLocations()
	 */
	@Test
	@Verifies(value = "should get all messages locations", method = "getAllMessagesLocations()")
	public void getAllMessagesLocations_shouldGetAllMessagesLocations() {	
		List<MessagesLocation> actual = customMessageService.getAllMessagesLocations();
		assertNotNull(actual);
		assertEquals(2, actual.size());
	}
	
	/**
	 * @see CustomMessageService#saveMessagesLocation(MessagesLocation)
	 */
	@Test
	@Verifies(value = "should create a new messages location in database", method = "saveMessagesLocation(MessagesLocation)")
	public void saveMessagesLocation_shouldCreateNewMessagesLocationInDatabase() {	
		int beforeSize = customMessageService.getAllMessagesLocations().size();
		MessagesLocation messagesLocation = new MessagesLocation();
		messagesLocation.setLocationId("just another test");
		messagesLocation.setName("Just another test name");
		customMessageService.saveMessagesLocation(messagesLocation);
		assertEquals(beforeSize + 1, customMessageService.getAllMessagesLocations().size());
	}
	
	/**
	 * @see CustomMessageService#saveMessagesLocation(MessagesLocation)
	 */
	@Test
	@Verifies(value = "should update existing messages location in database", method = "saveMessagesLocation(MessagesLocation)")
	public void saveMessagesLocation_shouldUpdateExistingMessagesLocationInDatabase() {	
		MessagesLocation messagesLocation = customMessageService.getMessagesLocation("test");
		String beforeName = messagesLocation.getName();
		String actualName = "test message";
		messagesLocation.setName(actualName);
		customMessageService.saveMessagesLocation(messagesLocation);
		// check that message attribute has changed
		messagesLocation = customMessageService.getMessagesLocation("test");
		assertNotSame(beforeName, messagesLocation.getName());
		assertEquals(actualName, messagesLocation.getName());
	}
	
	/**
	 * @see CustomMessageService#deleteMessagesLocation(MessagesLocation)
	 */
	@Test
	@Verifies(value = "should delete messages location", method = "deleteMessagesLocation(MessagesLocation)")
	public void deleteMessagesLocation_shouldDeleteMessagesLocation() {	
		int beforeSize = customMessageService.getAllMessagesLocations().size();
		MessagesLocation messagesLocation = customMessageService.getMessagesLocation("test");
		assertNotNull(messagesLocation);
		customMessageService.deleteMessagesLocation(messagesLocation);
		assertEquals(beforeSize - 1, customMessageService.getAllMessagesLocations().size());
	}
	
	
	/**
	 * @see CustomMessageService#getAvailableMessagesLocationsMap()
	 */
	@Test
	@Verifies(value = "should return map as result of merge of messages locations and started modules", method = "getAvailableMessagesLocationsMap()")
	public void getAvailableMessagesLocationsMap_shouldReturnMapAsResultOfMergeOfMessagesLocationsAndStartedModules() {
		int locationsFromDatabaseSize = customMessageService.getAllMessagesLocations().size();
		assertEquals(2, locationsFromDatabaseSize);
		// intentionally add mock module to map of started modules
		String mockModuleId = "custommessages";
		// intentionally put mock module as value with mockModuleId key to the map of started within system modules
		ModuleFactory.getStartedModulesMap().put(mockModuleId, new Module("Test module", mockModuleId, "", "", "", ""));
		
		Map<String, String> availableLocations = customMessageService.getAvailableMessagesLocationsMap();
		
		// get rid of entry with mockModuleId from the map of started modules
		ModuleFactory.getStartedModulesMap().remove(mockModuleId);
		
		// assert that locations map is properly created
		assertNotNull(availableLocations);
		assertEquals(locationsFromDatabaseSize + 1, availableLocations.size());
		assertTrue(availableLocations.containsKey(mockModuleId));
	}
	
}
