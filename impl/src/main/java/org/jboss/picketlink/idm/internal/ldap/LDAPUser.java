/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.picketlink.idm.internal.ldap;

import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.CN;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.EMAIL;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.GIVENNAME;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.OBJECT_CLASS;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.SN;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.UID;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.jboss.picketlink.idm.model.User;

/**
 * LDAP Representation of an {@link User}
 *
 * @author anil saldhana
 * @since Aug 30, 2012
 */
public class LDAPUser extends DirContextAdaptor implements User {

    protected String userid, firstName, lastName, fullName, email, userDNSuffix;

    public LDAPUser() {
        Attribute oc = new BasicAttribute(OBJECT_CLASS);
        oc.add("inetOrgPerson");
        oc.add("organizationalPerson");
        oc.add("person");
        oc.add("top");

        attributes.put(oc);
    }

    public void setUserDNSuffix(String udn) {
        this.userDNSuffix = udn;
    }

    public String getDN() {
        return UID + EQUAL + userid + COMMA + userDNSuffix;
    }

    public void setId(String id) {
        this.userid = id;
        Attribute theAttribute = attributes.get(UID);
        if (theAttribute == null) {
            attributes.put(UID, id);
        } else {
            theAttribute.set(0, id);
        }
    }

    @Override
    public String getId() {
        Attribute theAttribute = attributes.get(UID);
        if (theAttribute != null) {
            try {
                return (String) theAttribute.get();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String getFirstName() {
        try {
            if (firstName == null) {
                Attribute theAttribute = attributes.get(GIVENNAME);
                if (theAttribute != null) {
                    firstName = (String) theAttribute.get();
                }
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        Attribute theAttribute = attributes.get(GIVENNAME);
        if (theAttribute == null) {
            attributes.put(GIVENNAME, firstName);
        } else {
            theAttribute.set(0, firstName);
        }
    }

    @Override
    public String getLastName() {
        try {
            if (lastName == null) {
                Attribute theAttribute = attributes.get(SN);
                if (theAttribute != null) {
                    lastName = (String) theAttribute.get();
                }
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
        Attribute theAttribute = attributes.get(SN);
        if (theAttribute == null) {
            attributes.put(SN, lastName);
        } else {
            theAttribute.set(0, lastName);
        }
    }

    @Override
    public String getFullName() {
        try {
            if (fullName == null) {
                Attribute theAttribute = attributes.get(CN);
                if (theAttribute != null) {
                    fullName = (String) theAttribute.get();
                }
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        Attribute theAttribute = attributes.get(CN);
        if (theAttribute == null) {
            attributes.put(CN, fullName);
        } else {
            theAttribute.set(0, fullName);
        }
    }

    @Override
    public String getEmail() {
        try {
            if (email == null) {
                Attribute theAttribute = attributes.get(EMAIL);
                if (theAttribute != null) {
                    email = (String) theAttribute.get();
                }
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
        Attribute theAttribute = attributes.get(EMAIL);
        if (theAttribute == null) {
            attributes.put(EMAIL, email);
        } else {
            theAttribute.set(0, email);
        }
    }

    public static LDAPUser create(Attributes attributes, String userDNSuffix) {
        LDAPUser user = new LDAPUser();
        user.setUserDNSuffix(userDNSuffix);

        try {
            // Get the UID
            Attribute uid = attributes.get(UID);
            user.setId((String) uid.get());

            // Get the common name
            Attribute cn = attributes.get(CN);
            user.setFullName((String) cn.get());

            // Get the first name
            Attribute fn = attributes.get(GIVENNAME);
            user.setFirstName((String) fn.get());

            Attribute sn = attributes.get(SN);
            user.setLastName((String) sn.get());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}