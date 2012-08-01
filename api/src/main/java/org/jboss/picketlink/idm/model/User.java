package org.jboss.picketlink.idm.model;

/**
 * User representation
 */
public interface User extends IdentityType
{
    //TODO: Javadocs
    //TODO: Exceptions

    //TODO: minimal set of "hard-coded" attributes that make sense:
    //TODO: Personal - First/Last/Full Name, Phone, Email, Organization, Created Date, Birthdate; Too much??

    //TODO: separate UserProfile?

    //TODO: for some of those builtin attributes like email proper validation (dedicated exception?) is needed

    //TODO: authentication - password/token validation

    //TODO: non human identity - another interface?


    // Built in attributes

    String getId();

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    //TODO: this one could be configurable with some regex
    String getFullName();

    String getEmail();

    void setEmail(String email);
}
