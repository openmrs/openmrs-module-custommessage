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

import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.util.CustomMessageUtil;
import org.openmrs.web.taglib.behavior.TagMessageWriterBehavior;

/**
 * Basic implementation of {@link TagMessageWriterBehavior} interface that encloses all rendered
 * messages with &lt;span class="translate"&gt; and &lt;/span&gt; tags, in case if user property
 * specified by name {@link CustomMessageConstants#USER_PROPERTY_TRANSLATE_MODE_ENABLED} set to true, so produced
 * message becomes available for in-place edit
 */
public class CustomTagMessageWriterBehavior implements TagMessageWriterBehavior {
	
	/**
	 * @see org.openmrs.web.taglib.behavior.TagMessageWriterBehavior#renderMessage(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 *      
	 * @should not enclose text if in-place customization disabled
	 * @should not enclose text if there is no authenticated user
	 * @should not enclose text if translate mode is disabled
	 * @should enclose text if translate mode is enabled
	 */
	@Override
	public String renderMessage(String resolvedText, String code, String locale, String fallbackText) {
		return CustomMessageUtil.makeMessageTranslatable(code, resolvedText);
	}
	
}
