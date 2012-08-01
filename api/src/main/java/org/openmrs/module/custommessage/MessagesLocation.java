package org.openmrs.module.custommessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.util.OpenmrsUtil;

/**
 * Represents the location of custom messages
 */
public class MessagesLocation extends BaseOpenmrsObject {
	
	/** Sting identifier of this location */
	private String locationId;
	
	/** Descriptive name of this location */
	private String name;
	
	/** Collection of custom messages related to this location */
	private Collection<CustomMessage> customMessages;
	
	/**
	 * Creates empty instance of this class. Have to add this constructor in order to support
	 * creating instance of this class by third-party via reflection
	 */
	public MessagesLocation() {
	}
	
	/**
	 * Creates new instance of this class using values for corresponding fields
	 * 
	 * @param locationId the value to be used as location id
	 * @param name the value to be used as location name
	 */
	public MessagesLocation(String locationId, String name) {
		this.locationId = locationId;
		this.name = name;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Gets an identifier of this location
	 * 
	 * @return location id
	 */
	public String getLocationId() {
		return locationId;
	}
	
	/**
	 * Sets an identifier to this location
	 * 
	 * @param locationId the value to set
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	/**
	 * Gets descriptive name of this location
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets descriptive name to this location
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the list of custom messages related to this messages location, If corresponding field
	 * value is null, then it will be initialized with empty collection
	 * 
	 * @return the <em>customMessages</em> collection (will never be null, but can also be empty)
	 */
	public Collection<CustomMessage> getCustomMessages() {
		if (customMessages == null) {
			customMessages = new HashSet<CustomMessage>();
		}
		return customMessages;
	}
	
	/**
	 * Gets the list of custom messages related to this messages location in given locale
	 * 
	 * @param locale the locale of custom messages to filter by
	 * @return the <em>customMessages</em> collection that includes messages only for given locale
	 *         (will never be null, but can also be empty)
	 */
	public Collection<CustomMessage> getCustomMessages(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("Argument locale can not be null");
		}
		Collection<CustomMessage> customMessages = new HashSet<CustomMessage>();
		if (!getCustomMessages().isEmpty()) {
			for (CustomMessage customMessage : getCustomMessages()) {
				if (OpenmrsUtil.nullSafeEquals(customMessage.getLocale(), locale)) {
					customMessages.add(customMessage);
				}
			}
		}
		return customMessages;
	}
	
	/**
	 * Sets the collection of custom messages related to this location
	 * 
	 * @param customMessages the value to set
	 */
	public void setCustomMessages(Collection<CustomMessage> customMessages) {
		this.customMessages = customMessages;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
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
		MessagesLocation other = (MessagesLocation) obj;
		if (locationId == null) {
			if (other.locationId != null) {
				return false;
			}
		} else if (!locationId.equals(other.locationId)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessagesLocation [locationId=" + locationId + ", name=" + name + "]";
	}
	
}
