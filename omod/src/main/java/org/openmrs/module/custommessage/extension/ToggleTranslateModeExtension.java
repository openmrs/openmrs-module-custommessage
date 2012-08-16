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
package org.openmrs.module.custommessage.extension;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.custommessage.CustomMessageConstants;
import org.openmrs.module.custommessage.util.CustomMessageUtil;
import org.openmrs.module.web.extension.HeaderIncludeExt;
import org.openmrs.web.WebConstants;

/**
 * This class defines the button that will appear on the each page within footer for toggling
 * translate mode on/off. This extension is enabled by defining it in the config.xml file.
 */
public class ToggleTranslateModeExtension extends HeaderIncludeExt {
	
	private static final Log LOG = LogFactory.getLog(ToggleTranslateModeExtension.class);
	
	/** Velocity engine to be used for generating extension point content from template */
	private VelocityEngine velocityEngine;
	
	/** Contains velocity template string for rendering extension point content */
	private static String contentTemplate;
	
	/** The name of file with velocity template for extension point content */
	private static final String CONTENT_TEMPLATE_NAME = "extension-point-content.vm";
	
	static {
		// We need to initialize velocity template at class loading time 
		// in order to reduce I/O during runtime because this template will
		// be used relatively often
		try {
	        contentTemplate = IOUtils.toString(ToggleTranslateModeExtension.class.getClassLoader().getResourceAsStream(CONTENT_TEMPLATE_NAME));
        }
        catch (IOException e) {
	        LOG.error("Unable to read template to be used for rendering extension point content", e);
	        // as fallback use empty string for content template
	        contentTemplate = StringUtils.EMPTY;
        }
	}

	/**
	 * @see org.openmrs.module.Extension#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 */
	public String getRequiredPrivilege() {
		return CustomMessageConstants.PRIVILEGE_MANAGE_CUSTOM_MESSAGES;
	}

	/**
	 * @see org.openmrs.module.Extension#getOverrideContent(java.lang.String)
	 */
	@Override
	public String getOverrideContent(String bodyContent) {
		User currentUser = Context.getAuthenticatedUser();
		// if in-place customization is being globally enabled and 
		// current user is granted to do messages customization
		if (CustomMessageUtil.isInplaceCustomizationEnabled() && currentUser != null
		        && currentUser.hasPrivilege(getRequiredPrivilege())) {
			
			initializeVelocity();
			
			VelocityContext velocityContext = new VelocityContext();
			velocityContext.put("contextPath", WebConstants.WEBAPP_NAME);
			velocityContext.put("translateModeStatus", currentUser.getUserProperty(
			    CustomMessageConstants.USER_PROPERTY_TRANSLATE_MODE_ENABLED, "false"));
			
			String overridenContent = null;
			try {
				StringWriter writer = new StringWriter();
				velocityEngine.evaluate(velocityContext, writer, this.getClass().getName(), contentTemplate);
				overridenContent = writer.toString();
			} catch (Exception e) {
				LOG.error("Error evaluating template when getting overwritten content for extesion point, because of error:", e);
			}
		    return overridenContent;
		} else {
			return bodyContent;
		}
	}

	/**
	 * @see org.openmrs.module.web.extension.HeaderIncludeExt#getHeaderFiles()
	 */
	@Override
    public List<String> getHeaderFiles() {
	    return new ArrayList<String>(0);
    }
	
	/**
	 * A utility method to initialize Velocity. This could be called in the constructor, but putting
	 * it in a separate method like this allows for late-initialization only when actual using of
	 * this extension point occurred at first time.
	 */
	private void initializeVelocity() {
		if (velocityEngine == null) {
			velocityEngine = new VelocityEngine();
			
			velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			    "org.apache.velocity.runtime.log.CommonsLogLogChute");
			velocityEngine.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "custommessage_velocity");
			try {
				velocityEngine.init();
			}
			catch (Exception e) {
				LOG.error("velocity init failed", e);
			}
		}
	}

}