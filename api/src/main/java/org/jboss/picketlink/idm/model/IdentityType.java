package org.jboss.picketlink.idm.model;

import java.util.Date;
import java.util.Map;

/**
 * IdentityObject
 *
 */
public interface IdentityType
{

    String getKey();
    
    boolean isEnabled();

    Date getExpirationDate();

    Date getCreationDate();   

    // Attributes

    /**
     * Set attribute with given name and value. Operation will overwrite any previous value.
     * Null value will remove attribute.
     *
     * @param name  of attribute
     * @param value to be set
     */
    void setAttribute(String name, String value);

    /**
     * Set attribute with given name and values. Operation will overwrite any previous values.
     * Null value or empty array will remove attribute.
     *
     * @param name   of attribute
     * @param values to be set
     */
    void setAttribute(String name, String[] values);

    /**
     * Remove attribute with given name
     *
     * @param name of attribute
     */
    void removeAttribute(String name);

    /**
     * @param name of attribute
     * @return attribute values or null if attribute with given name doesn't exist. If given attribute has many values
     *         method will return first one
     */
    String getAttribute(String name);

    /**
     * @param name of attribute
     * @return attribute values or null if attribute with given name doesn't exist
     */
    String[] getAttributeValues(String name);

    /**
     * @return map of attribute names and their values
     */
    Map<String, String[]> getAttributes();

}
