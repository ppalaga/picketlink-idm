package org.jboss.picketlink.idm.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Abstract base class for IdentityType implementations 
 */
public abstract class AbstractIdentityType implements IdentityType
{
    private String key;
    private boolean enabled = true;
    private Date creationDate = null;
    private Date expirationDate = null;
    private Map<String,String[]> attributes = new HashMap<String,String[]>();
    
    public String getKey()
    {
        return this.key;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public Date getExpirationDate()
    {
        return this.expirationDate;
    }

    public Date getCreationDate()
    {
        return this.creationDate;
    }

    public void setAttribute(String name, String value)
    {
        attributes.put(name, new String[]{value});        
    }

    public void setAttribute(String name, String[] values)
    {
        attributes.put(name,  values);
    }

    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    public String getAttribute(String name)
    {
        String[] vals = attributes.get(name);
        return null == vals ? null : ((vals.length != 0) ? vals[0] : null);
    }

    public String[] getAttributeValues(String name)
    {
        return attributes.get(name);
    }

    public Map<String, String[]> getAttributes()
    {
        return java.util.Collections.unmodifiableMap(attributes);
    }

}
