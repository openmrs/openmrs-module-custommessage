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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.custommessage.service.CustomMessageService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.LocaleUtility;

/**
 * This class contains unit tests of {@link CustomMessageService} class
 */
public class CustomMessageServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET_FILE_NAME = "CustomMessageServiceTest-testDataset.xml";
	
	@Before
	public void before() throws Exception {
		executeDataSet(DATASET_FILE_NAME);
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
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		assertNull(customMessageService.getCustomMessage(9999));
	}
	
	/**
	 * @see CustomMessageService#getCustomMessage(Integer)
	 */
	@Test
	@Verifies(value = "should return custom message by id", method = "getCustomMessage(Integer)")
	public void getCustomMessage_shouldReturnCustomMessageById() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		CustomMessage customMessage = customMessageService.getCustomMessage(1);
		assertNotNull(customMessage);
	}
	
	/**
	 * @see CustomMessageService#getCustomMessageByUuid(String)
	 */
	@Test
	@Verifies(value = "should return null if no object found with given uuid", method = "getCustomMessageByUuid(String)")
	public void getCustomMessageByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		assertNull(customMessageService.getCustomMessageByUuid("invalid uuid"));
	}
	
	/**
	 * @see CustomMessageService#getCustomMessageByUuid(String)
	 */
	@Test
	@Verifies(value = "should return custom message by uuid", method = "getCustomMessageByUuid(String)")
	public void getCustomMessageByUuid_shouldReturnCustomMessageByUuid() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		assertNotNull(customMessageService.getCustomMessageByUuid("a2e25607-d0c4-4e44-8be6-31e1ac7e100d"));
	}
	
	/**
	 * @see CustomMessageService#getAllCustomMessages()
	 */
	@Test
	@Verifies(value = "should get all custom messages", method = "getAllCustomMessages()")
	public void getAllCustomMessages_shouldGetAllCustomMessages() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
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
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
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
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
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
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		CustomMessage actual = customMessageService.getCustomMessagesForCodeAndLocale("wrong.test.code", LocaleUtility.fromSpecification("fr"));
		assertNull(actual);
	}
	
	/**
	 * @see CustomMessageService#getCustomMessagesForCodeAndLocale(String, java.util.Locale)
	 */
	@Test
	@Verifies(value = "should return custom message for code and locale", method = "getCustomMessagesForCodeAndLocale(String, java.util.Locale)")
	public void getCustomMessagesForCodeAndLocale_shouldReturnCustomMessagesForCodeAndLocale() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		CustomMessage actual = customMessageService.getCustomMessagesForCodeAndLocale("test.code", LocaleUtility.fromSpecification("fr"));
		assertNotNull(actual);
	}
	
	/**
	 * @see CustomMessageService#saveCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should create a new custom message in database", method = "saveCustomMessage(CustomMessage)")
	public void saveCustomMessage_shouldCreateNewCustomMessageInDatabase() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		int beforeSize = customMessageService.getAllCustomMessages().size();
		CustomMessage customMessage = new CustomMessage();
		customMessage.setCode("new.code");
		customMessage.setLocale(Context.getLocale());
		customMessage.setMessage("test message");
		customMessageService.saveCustomMessage(customMessage);
		assertEquals(beforeSize + 1, customMessageService.getAllCustomMessages().size());
	}
	
	/**
	 * @see CustomMessageService#saveCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should update existing custom message in database", method = "saveCustomMessage(CustomMessage)")
	public void saveCustomMessage_shouldUpdateExistingCustomMessageInDatabase() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
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
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		customMessageService.deleteCustomMessage(null);
	}
	
	/**
	 * @see CustomMessageService#deleteCustomMessage(CustomMessage)
	 */
	@Test
	@Verifies(value = "should delete custom message", method = "deleteCustomMessage(CustomMessage)")
	public void deleteCustomMessage_shouldDeleteCustomMessage() {	
		CustomMessageService customMessageService = Context.getService(CustomMessageService.class);
		int beforeSize = customMessageService.getAllCustomMessages().size();
		CustomMessage customMessage = customMessageService.getCustomMessage(1);
		assertNotNull(customMessage);
		customMessageService.deleteCustomMessage(customMessage);
		assertEquals(beforeSize - 1, customMessageService.getAllCustomMessages().size());
	}
	
}
