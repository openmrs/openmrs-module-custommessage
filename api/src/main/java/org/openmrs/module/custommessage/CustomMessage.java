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

import java.util.Date;
import java.util.Locale;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;
import org.openmrs.messagesource.PresentationMessage;

/**
 * Represents a single custom message for a particular locale
 */
public class CustomMessage extends BaseOpenmrsObject {
	
	private Integer id;
	
	private String code;
	
	private Locale locale;
	
	private String message;
	
	private User updatedBy;
	
	private Date updatedDatetime;
	
	/**
	 * The location from where came origin message that is customized by this message
	 */
	private MessagesLocation messageLocation;
	
	/**
	 * Default Constructor
	 */
	public CustomMessage() {
	}
	
	/**
	 * @return this CustomMessage as a PresentationMessage
	 */
	public PresentationMessage toPresentationMessage() {
		return new PresentationMessage(code, locale, message, null);
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the updatedBy
	 */
	public User getUpdatedBy() {
		return updatedBy;
	}
	
	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	/**
	 * @return the updatedDatetime
	 */
	public Date getUpdatedDatetime() {
		return updatedDatetime;
	}
	
	/**
	 * @param updatedDatetime the updatedDatetime to set
	 */
	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}
	
	/**
	 * Gets the location to which this message belongs.
	 * 
	 * @return the location of origin message that is being customized by this message
	 */
	public MessagesLocation getMessageLocation() {
		return messageLocation;
	}
	
	/**
	 * Sets the location of this message.
	 * 
	 * @param messageLocation the value to set
	 */
	public void setMessageLocation(MessagesLocation messageLocation) {
		this.messageLocation = messageLocation;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	    return "CustomMessage [id=" + id + ", code=" + code + ", locale=" + locale + ", message=" + message + ", updatedBy="
	            + updatedBy + ", updatedDatetime=" + updatedDatetime + ", messageLocation=" + messageLocation + "]";
    }

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	    if (this == obj) {
		    return true;
	    }
	    if (!super.equals(obj)) {
		    return false;
	    }
	    if (getClass() != obj.getClass()) {
		    return false;
	    }
	    CustomMessage other = (CustomMessage) obj;
	    if (id == null) {
		    if (other.id != null) {
			    return false;
		    }
	    } else if (!id.equals(other.id)) {
		    return false;
	    }
	    return true;
    }
}
