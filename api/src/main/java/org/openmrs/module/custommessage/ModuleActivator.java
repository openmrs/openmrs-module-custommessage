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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;

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
			Class<?> tagClass = Class.forName("org.openmrs.web.taglib.OpenmrsMessageTag");

			// still no errors ? good, then support for openmrs:message is, go ahead Mrs. JVM
			Class<?> writerBehaviourClass = Class.forName("org.openmrs.web.taglib.behavior.TagMessageWriterBehavior");
			Method method = tagClass.getMethod("setTagWriterBehavior", writerBehaviourClass);
			
			Class<?> writerBehaviourmplClass = Class.forName("org.openmrs.module.custommessage.CustomTagMessageWriterBehavior");
			method.invoke(null, writerBehaviourmplClass.newInstance());

			LOG.info("Started custommessage module");
			
			// indicate that openmrs:message tag is supported so, it's not needed to enable
			// limited functionality support for spring:message tag 
			CustomMessageSource.isOpenmrsMessageTagAvailable = Boolean.TRUE;
			
			return;

		} catch (ClassNotFoundException e) {
		} catch (NoClassDefFoundError e) {
		} catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (InstantiationException e) {
        }
		
		LOG.warn("Module custommessage started with limited features, because openmrs:message tag is not supported by the system");

	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	@Override
	public void shutdown() {

		// reset openmrs:message tag writer behavior when module is stopped
		try {
			Class<?> clazz = Class.forName("org.openmrs.web.taglib.OpenmrsMessageTag");
	
			// still no errors ? good, then support for openmrs:message is, go ahead Mrs. JVM
			Method method = clazz.getMethod("setTagWriterBehavior", String.class);
			method.invoke(null, clazz.getDeclaredField("DEFAULT_WRITER_BEHAVIOUR").get(null));
			
			LOG.info("Shut down custommessage module");
		} catch (NoClassDefFoundError e) {
		} catch (ClassNotFoundException e) {
        } catch (SecurityException e) {
	    } catch (NoSuchMethodException e) {
	    } catch (IllegalArgumentException e) { 
	    } catch (IllegalAccessException e) {
	    } catch (InvocationTargetException e) {
	    } catch (NoSuchFieldException e) {
	    }
	}	
}
