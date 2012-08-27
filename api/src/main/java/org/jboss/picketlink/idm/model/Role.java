package org.jboss.picketlink.idm.model;


/**
 * Role representation
 */
public interface Role extends IdentityType
{
    static final String KEY_PREFIX = "ROLE://";
    // Self

    String getName();
}
