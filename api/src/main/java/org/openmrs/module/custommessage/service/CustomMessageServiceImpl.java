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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.messagesource.PresentationMessageMap;
import org.openmrs.module.custommessage.CustomMessage;
import org.openmrs.module.custommessage.service.db.CustomMessageDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for managing custom messages
 */
@Transactional
public class CustomMessageServiceImpl extends BaseOpenmrsService implements CustomMessageService {
	
	private CustomMessageDAO dao;
	
	/**
	 * @return the CustomMessage with the given id
	 */
	public CustomMessage getCustomMessage(Integer id) {
		return dao.getCustomMessage(id);
	}
	
	/**
	 * @return the CustomMessage with the given uuid
	 */
	public CustomMessage getCustomMessageByUuid(String uuid) {
		return dao.getCustomMessageByUuid(uuid);
	}
	
	/**
	 * @return all custom messages defined
	 */
	public List<CustomMessage> getAllCustomMessages() {
		return dao.getAllCustomMessages();
	}
	
	/**
	 * @return all custom messages for the associated code
	 */
	public List<CustomMessage> getCustomMessagesForCode(String code) {
		return dao.getCustomMessagesForCode(code);
	}
	
	/**
	 * @param customMessage the Custom Message to save to the database
	 */
	public void saveCustomMessage(CustomMessage customMessage) {
		customMessage.setUpdatedBy(Context.getAuthenticatedUser());
		customMessage.setUpdatedDatetime(new Date());
		dao.saveCustomMessage(customMessage);
	}
	
	/**
	 * @param customMessage the Custom Message to delete from the database
	 */
	public void deleteCustomMessage(CustomMessage customMessage) {
		if (customMessage == null) {
			throw new IllegalArgumentException("Can not delete null entity");
		}
		dao.deleteCustomMessage(customMessage);
	}
	
	
	/**
	 * @return all custom messages as presentation messages by locale
	 */
	public Map<Locale, PresentationMessageMap> getPresentationMessagesByLocale() {
		Map<Locale, PresentationMessageMap> ret = new HashMap<Locale, PresentationMessageMap>();
		for (CustomMessage m : dao.getAllCustomMessages()) {
			PresentationMessageMap pmm = ret.get(m.getLocale());
			if (pmm == null) {
				pmm = new PresentationMessageMap(m.getLocale());
				ret.put(m.getLocale(), pmm);
			}
			pmm.put(m.getCode(), m.toPresentationMessage());
		}
		return ret;
	}
	
	/**
	 * @return all custom messages by code
	 */
	public Map<String, Map<Locale, CustomMessage>> getCustomMessagesByCode() {
		Map<String, Map<Locale, CustomMessage>> ret = new TreeMap<String, Map<Locale, CustomMessage>>();
		for (CustomMessage m : dao.getAllCustomMessages()) {
			Map<Locale, CustomMessage> pmm = ret.get(m.getCode());
			if (pmm == null) {
				pmm = new HashMap<Locale, CustomMessage>();
				ret.put(m.getCode(), pmm);
			}
			pmm.put(m.getLocale(), m);
		}		
		return ret;
	}

	/**
	 * @return the dao
	 */
	public CustomMessageDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(CustomMessageDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.module.custommessage.service.CustomMessageService#getCustomMessagesForCodeAndLocale(java.lang.String, java.util.Locale)
	 */
	@Override
    public CustomMessage getCustomMessagesForCodeAndLocale(String code, Locale locale) {
	    return dao.getCustomMessagesForCode(code, locale);
    }
}
