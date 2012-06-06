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

import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.CustomMessageUtil;

/**
 * DWR based service class that exposes functionality for managing custom messages to be available
 * via AJAX calls
 */
public class DWRCustomMessageService {
	
	/**
	 * This method simply turns on/off translate mode by changing value of user property
	 * identified by {@link CustomMessageConstants#USER_PROPERTY_TRANSLATE_MODE_ENABLED} constant
	 */
	public void toggleTranslateMode() {
		CustomMessageUtil.toggleTranslateMode();
	}
}
