package org.jboss.picketlink.idm.model;

/**
 * Membership links User, Group and Role.
 */
public interface Membership
{
    //TODO: Javadocs
    //TODO: Exceptions

    User getUser();

    Group getGroup();

    Role getRole();
}
