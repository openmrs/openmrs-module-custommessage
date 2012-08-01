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
 * This class contains unit tests of {@link CustomMessageUtil} class
 */
public class CustomMessageUtilTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET_FILE_NAME = "customTestDataset.xml";
	
	@Before
	public void before() throws Exception {
		executeDataSet(DATASET_FILE_NAME);
	}
	
	/**
	 * @see CustomMessageUtil#isTranslateModeEnabled()
	 */
	@Test
	@Verifies(value = "should return false if in-place customization disabled", method = "isTranslateModeEnabled()")
	public void isTranslateModeEnabled_shouldReturnFalseIfInplaceCustomizationDisabled() {
		// intentionally disable in-place customization
		GlobalProperty gpInplaceCustomizationEnabled = Context.getAdministrationService().getGlobalPropertyObject(
		    CustomMessageConstants.GLOBAL_PROPERTY_INPLACE_CUSTOMIZATION_ENABLED);
		gpInplaceCustomizationEnabled.setPropertyValue("false");
		
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
	}
	
	/**
	 * @see CustomMessageUtil#isTranslateModeEnabled()
	 */
	@Test
	@Verifies(value = "should return false if there is no authenticated user", method = "isTranslateModeEnabled()")
	public void isTranslateModeEnabled_shouldReturnFalseIfThereIsNoAuthenticatedUser() {
		Context.logout();
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
	}
	
	/**
	 * @see CustomMessageUtil#isTranslateModeEnabled()
	 */
	@Test
	@Verifies(value = "should return false if user does not have translate mode property", method = "isTranslateModeEnabled()")
	public void isTranslateModeEnabled_shouldReturnFalseIfUserDoesNotHaveTranslateModeProperty() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		Context.authenticate("test_user", "test");
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
		Context.logout();
	}
	
	/**
	 * @see CustomMessageUtil#isTranslateModeEnabled()
	 */
	@Test
	@Verifies(value = "should return true if translate mode is enabled", method = "isTranslateModeEnabled()")
	public void isTranslateModeEnabled_shouldReturnTrueTranslateModeIsEnabled() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		Context.authenticate("test_translator", "test");
		assertTrue(CustomMessageUtil.isTranslateModeEnabled());
		Context.logout();
	}
	
	/**
	 * @see CustomMessageUtil#toggleTranslateMode()
	 */
	@Test(expected = IllegalStateException.class)
	@Verifies(value = "should throw an exception if there is no authenticated user", method = "toggleTranslateMode()")
	public void toggleTranslateMode_shouldThrowAnExceptionIfThereIsNoAuthenticatedUser() {
		Context.logout();
		CustomMessageUtil.toggleTranslateMode();
	}
	
	/**
	 * @see CustomMessageUtil#toggleTranslateMode()
	 */
	@Test
	@Verifies(value = "should switch on translate mode for first time", method = "toggleTranslateMode()")
	public void toggleTranslateMode_shouldSwitchOnTranslateModeForFirstTime() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		
		Context.authenticate("test_user", "test");
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
		CustomMessageUtil.toggleTranslateMode();
		assertTrue(CustomMessageUtil.isTranslateModeEnabled());
		Context.logout();
	}
	
	/**
	 * @see CustomMessageUtil#toggleTranslateMode()
	 */
	@Test
	@Verifies(value = "should toggle translate mode value", method = "toggleTranslateMode()")
	public void toggleTranslateMode_shouldToggleTranslateModeValue() {
		if (Context.isAuthenticated()) {
			Context.logout();
		}
		
		Context.authenticate("test_translator", "test");
		assertTrue(CustomMessageUtil.isTranslateModeEnabled());
		CustomMessageUtil.toggleTranslateMode();
		assertFalse(CustomMessageUtil.isTranslateModeEnabled());
		Context.logout();
	}
	
}
