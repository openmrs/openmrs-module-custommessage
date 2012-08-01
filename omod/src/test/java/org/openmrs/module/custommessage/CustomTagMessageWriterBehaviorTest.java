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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.custommessage.util.CustomMessageUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * This class contains unit tests for {@link CustomTagMessageWriterBehavior} class
 */
public class CustomTagMessageWriterBehaviorTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET_FILE_NAME = "customTestDataset.xml";
	
	private CustomTagMessageWriterBehavior customTagBehavior = new CustomTagMessageWriterBehavior();
	
	@Before
	public void before() throws Exception {
		executeDataSet(DATASET_FILE_NAME);
	}
	
	/**
	 * @see CustomTagMessageWriterBehavior#renderMessage(String, String, String, String)
	 */
	@Test
	@Verifies(value = "should not enclose text if in-place customization disabled", method = "renderMessage(String, String, String, String)")
	public void renderMessage_shouldNotEncloseTextIfInplaceCustomizationDisabled() {
		String expectedText = "test";
		// intentionally disable in-place customization
		GlobalProperty gpInplaceCustomizationEnabled = Context.getAdministrationService().getGlobalPropertyObject(
		    CustomMessageConstants.GLOBAL_PROPERTY_INPLACE_CUSTOMIZATION_ENABLED);
		gpInplaceCustomizationEnabled.setPropertyValue("false");
		
		String actualText = customTagBehavior.renderMessage(expectedText, null, null, null);
		assertNotNull(actualText);
		assertEquals(expectedText, actualText);
	}
	
	/**
	 * @see CustomTagMessageWriterBehavior#renderMessage(String, String, String, String)
	 */
	@Test
	@Verifies(value = "should not enclose text if there is no authenticated user", method = "renderMessage(String, String, String, String)")
	public void renderMessage_shouldNotEncloseTextIfThereIsNoAuthenticatedUser() {
		Context.logout();
		String expectedText = "test";
		String actualText = customTagBehavior.renderMessage(expectedText, null, null, null);
		assertNotNull(actualText);
		assertEquals(expectedText, actualText);
	}
	
	/**
	 * @see CustomTagMessageWriterBehavior#renderMessage(String, String, String, String)
	 */
	@Test
	@Verifies(value = "should not enclose text if translate mode is disabled", method = "renderMessage(String, String, String, String)")
	public void renderMessage_shouldNotEncloseTextIfTranslateModeIsDisabled() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		Context.authenticate("test_user", "test");
		
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
		
		String expectedText = "test";
		String actualText = customTagBehavior.renderMessage(expectedText, null, null, null);
		assertNotNull(actualText);
		assertEquals(expectedText, actualText);
		Context.logout();
	}
	
	/**
	 * @see CustomTagMessageWriterBehavior#renderMessage(String, String, String, String)
	 */
	@Test
	@Verifies(value = "should enclose text if translate mode is enabled", method = "renderMessage(String, String, String, String)")
	public void renderMessage_shouldEncloseTextIfTranslateModeIsEnabled() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		Context.authenticate("test_translator", "test");
		
		assertTrue(CustomMessageUtil.isTranslateModeEnabled());
		
		String expectedText = "test";
		String actualText = customTagBehavior.renderMessage(expectedText, null, null, null);
		assertNotNull(actualText);
		assertNotSame(expectedText, actualText);
		Context.logout();
	}
}
