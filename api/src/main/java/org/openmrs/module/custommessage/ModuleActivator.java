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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleException;
import org.openmrs.web.taglib.OpenmrsMessageTag;

/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
public class ModuleActivator implements Activator {

	private static final Log LOG = LogFactory.getLog(ModuleActivator.class);

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	@Override
	public void startup() {
		
		// try to define if there is a support for openmrs:message
		// by instantiating org.openmrs.web.taglib.OpenmrsMessageTag
		try {
			Class.forName("org.openmrs.web.taglib.OpenmrsMessageTag");
		} catch(ClassNotFoundException e) {
			LOG.error("Unable to start module because openmrs:message tag is not supported by the system");
			throw new ModuleException("Unable to start module because openmrs:message tag is not supported by this version of system");
		}

		// customize tag writer behavior for openmrs:message tag if openmrs
		// messaging support is provided via used version of OpenMRS framework
		OpenmrsMessageTag.setTagWriterBehavior(new CustomTagMessageWriterBehavior());

		LOG.info("Started custommessage module");
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	@Override
	public void shutdown() {

		// reset openmrs:message tag writer behavior when module is stopped
		OpenmrsMessageTag.setTagWriterBehavior(OpenmrsMessageTag.DEFAULT_WRITER_BEHAVIOUR);

		LOG.info("Shut down custommessage module");
	}	
}
