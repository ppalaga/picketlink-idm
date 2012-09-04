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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.jboss.picketlink.idm.model.User;

/**
 * LDAP Representation of an {@link User}
 * @author anil saldhana
 * @since Aug 30, 2012
 */
public class LDAPUser extends DirContextAdaptor implements User {

    protected String userid, firstName, lastName, fullName,email;

    public LDAPUser(){

        Attribute oc = new BasicAttribute("objectclass");
        oc.add("inetOrgPerson");
        oc.add("organizationalPerson");
        oc.add("person");
        oc.add("top");

        userAttributes.put(oc);
    }
    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Date getExpirationDate() {
        return null;
    }

    @Override
    public Date getCreationDate() {
        return null;
    }

    @Override
    public void setAttribute(String name, String value) {
        userAttributes.put(name, value);
    }

    @Override
    public void setAttribute(String name, String[] values) {
        userAttributes.put(name, values);
    }

    @Override
    public void removeAttribute(String name) {
        userAttributes.remove(name);
    }

    @Override
    public String getAttribute(String name) {
        try {
            Attribute theAttribute = userAttributes.get(name);
            return (String) theAttribute.get();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getAttributeValues(String name) {
        try {
            Attribute theAttribute = userAttributes.get(name);
            return (String[]) theAttribute.get();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String[]> getAttributes() {
        try {
            Map<String,String[]> map = new HashMap<String,String[]>();
            NamingEnumeration<? extends Attribute> theAttributes = userAttributes.getAll();
            while(theAttributes.hasMore()){
                Attribute anAttribute = theAttributes.next();
                map.put(anAttribute.getID(), (String[]) anAttribute.get());
            }
            return map;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        Attribute theAttribute = userAttributes.get("uid");
        if(theAttribute != null){
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
            if(firstName == null){
                Attribute theAttribute = userAttributes.get("givenname");
                if(theAttribute != null){
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
        Attribute theAttribute = userAttributes.get("givenname");
        if(theAttribute == null){
            userAttributes.put("givenname", firstName);
        } else {
            theAttribute.set(0, firstName);
        }
    }

    @Override
    public String getLastName() {
        try {
            if(lastName == null){
                Attribute theAttribute = userAttributes.get("sn");
                if(theAttribute != null){
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
        Attribute theAttribute = userAttributes.get("sn");
        if(theAttribute == null){
            userAttributes.put("sn", lastName);
        } else {
            theAttribute.set(0, lastName);
        }
    }

    @Override
    public String getFullName() {
        try {
            if(fullName == null){
                Attribute theAttribute = userAttributes.get("cn");
                if(theAttribute != null){
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
        Attribute theAttribute = userAttributes.get("cn");
        if(theAttribute == null){
            userAttributes.put("cn", fullName);
        } else {
            theAttribute.set(0, fullName);
        }
    } 

    @Override
    public String getEmail() {
        try {
            if(email == null){
                Attribute theAttribute = userAttributes.get("mail");
                if(theAttribute != null){
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
        Attribute theAttribute = userAttributes.get("mail");
        if(theAttribute == null){
            userAttributes.put("mail", email);
        } else {
            theAttribute.set(0, email);
        }
    }

    public static LDAPUser create(Attributes attributes){
        LDAPUser user = new LDAPUser();
        
        try{
            //Get the common name
            Attribute cn =  attributes.get("cn");
            user.setFullName((String) cn.get());
            
            //Get the first name
            Attribute fn = attributes.get("givenname");
            user.setFirstName((String) fn.get());
            
            Attribute sn = attributes.get("sn");
            user.setLastName((String) sn.get());
        } catch(NamingException e){
            e.printStackTrace();
        }
        return user;
    }
}