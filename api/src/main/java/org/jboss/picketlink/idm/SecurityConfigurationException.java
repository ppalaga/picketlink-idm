package org.jboss.picketlink.idm;

/**
 * This exception is thrown when a problem is found with the Security API configuration   
 *
 */
public class SecurityConfigurationException extends SecurityException
{
    private static final long serialVersionUID = -8895836939958745981L;
    
    public SecurityConfigurationException() 
    {
        super();
    }

    public SecurityConfigurationException(String message, Throwable cause) 
    {
        super(message, cause);
    }

    public SecurityConfigurationException(String message) 
    {
        super(message);
    }

    public SecurityConfigurationException(Throwable cause) 
    {
        super(cause);
    }

}
