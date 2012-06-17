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
package org.openmrs.module.custommessage.service.db;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.custommessage.CustomMessage;

/**
 * Core implementation of the DAO
 */
public class HibernateCustomMessageDAO implements CustomMessageDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
    private SessionFactory sessionFactory;

	/**
	 * @see CustomMessageDAO#getCustomMessage(Integer)
	 */
	@Override
	public CustomMessage getCustomMessage(Integer id) {
		return (CustomMessage)sessionFactory.getCurrentSession().get(CustomMessage.class, id);
	}

	/**
	 * @see org.openmrs.module.custommessage.service.db.CustomMessageDAO#getCustomMessageByUuid(java.lang.String)
	 */
	@Override
	public CustomMessage getCustomMessageByUuid(String uuid) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CustomMessage.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (CustomMessage)criteria.uniqueResult();
	}

	/**
	 * @see CustomMessageDAO#getAllCustomMessages()
	 */
	@SuppressWarnings("unchecked")
	public List<CustomMessage> getAllCustomMessages() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CustomMessage.class);
		return criteria.list();
	}
	
	/**
	 * @see CustomMessageDAO#getCustomMessagesForCode(String)
	 */
	@SuppressWarnings("unchecked")
	public List<CustomMessage> getCustomMessagesForCode(String code) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CustomMessage.class);
		criteria.add(Restrictions.eq("code", code));
		return criteria.list();
	}
	
	/**
	 * 
	 * @see CustomMessageDAO#saveCustomMessage(CustomMessage)
	 */
	public void saveCustomMessage(CustomMessage customMessage) {
		sessionFactory.getCurrentSession().saveOrUpdate(customMessage);
	}
	
	/**
	 * @param customMessage the Custom Message to delete from the database
	 */
	public void deleteCustomMessage(CustomMessage customMessage) {
		sessionFactory.getCurrentSession().delete(customMessage);
	}
    
	/**
	 * @param sessionFactory
	 */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @see org.openmrs.module.custommessage.service.db.CustomMessageDAO#getCustomMessagesForCode(java.lang.String, java.util.Locale)
     */
	@SuppressWarnings("unchecked")
    @Override
    public CustomMessage getCustomMessagesForCode(String code, Locale locale) {
		CustomMessage customMessage = null;
		// build criteria for fetching custom messages with
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CustomMessage.class);
		criteria.add(Restrictions.eq("code", code));
		criteria.add(Restrictions.eq("locale", locale));
		List<CustomMessage> result = criteria.list();
		
		// if list is not null and not empty get the first element
		if (result != null && !result.isEmpty()) {
			customMessage = result.get(0);
		}
	    return customMessage;
    }
}