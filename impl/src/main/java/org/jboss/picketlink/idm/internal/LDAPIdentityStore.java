package org.jboss.picketlink.idm.internal;

import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.CN;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.EMAIL;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.GIVENNAME;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.OBJECT_CLASS;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.SN;
import static org.jboss.picketlink.idm.internal.ldap.LDAPConstants.UID;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.jboss.picketlink.idm.internal.ldap.LDAPUser;
import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.Membership;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.query.GroupQuery;
import org.jboss.picketlink.idm.query.MembershipQuery;
import org.jboss.picketlink.idm.query.Range;
import org.jboss.picketlink.idm.query.RoleQuery;
import org.jboss.picketlink.idm.query.UserQuery;
import org.jboss.picketlink.idm.spi.IdentityStore;

/**
 * An IdentityStore implementation backed by an LDAP directory 
 *
 * @author Shane Bryzak
 * @author Anil Saldhana
 */
public class LDAPIdentityStore implements IdentityStore
{
    public final String COMMA = ",";
    
    protected DirContext ctx = null;
    protected String userDNSuffix, roleDNSuffix;

    /**
     * WE NEED A PROPER BUILDER TO REPLACE THIS
     * @param config
     */
    public void config(Map<String,String> config){
        userDNSuffix = config.get("userDNSuffix");
        roleDNSuffix = config.get("roleDNSuffix");

        //Construct the dir ctx
        Properties env = new Properties();

        String factoryName = config.get("factory");
        if(factoryName == null){
            factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
        }
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);   

        String authType = config.get("securityAuth");
        if(authType == null){
            authType = "simple";
        }

        String protocol = config.get("protocol");
        if (protocol != null) {
            env.setProperty(Context.SECURITY_PROTOCOL, protocol);
        }

        String bindDN = config.get("bindDN");
        char[] bindCredential = null;

        if (config.get("password") != null) {
            bindCredential = config.get("password").toCharArray();
        }

        if (bindDN != null) {
            env.setProperty(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindCredential);
        }

        if (bindDN != null) {
            // Rebind the ctx to the bind dn/credentials for the roles searches
            env.setProperty(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindCredential);

        }

        String url =  config.get("url");
        if(url == null){
            throw new RuntimeException("url");
        }

        env.setProperty(Context.PROVIDER_URL,url);

        try {
            ctx = new InitialLdapContext(env, null);
        } catch (NamingException e1) {
            throw new RuntimeException(e1);
        }
    }

    @Override
    public User createUser(String name)
    {
        LDAPUser user = new LDAPUser();
        user.setFullName(name); 
        String firstName = getFirstName(name);
        String lastName = getLastName(name);
        
        user.setFirstName(firstName);
        user.setLastName(lastName);

        //TODO: How do we get the userid?
        String userid = generateUserID(firstName, lastName);
        
        try {
            ctx.bind(UID + "="+ userid + COMMA + userDNSuffix, user);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void removeUser(User user)
    { 
        try {
            ctx.destroySubcontext(UID + "="+ user.getId() + COMMA + userDNSuffix);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(String name)
    { 
        LDAPUser user  = null;
        try {
            Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
            matchAttrs.put(new BasicAttribute(CN, name));
            
            NamingEnumeration<SearchResult> answer = ctx.search(userDNSuffix, matchAttrs);
            while (answer.hasMore()) {
                SearchResult sr = answer.next();
                Attributes attributes = sr.getAttributes();
                user = LDAPUser.create(attributes);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Group createGroup(String name, Group parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeGroup(Group group)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Group getGroup(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Role createRole(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeRole(Role role)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Role getRole(String role)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Membership createMembership(Role role, User user, Group group)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeMembership(Role role, User user, Group group)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Membership getMembership(Role role, User user, Group group)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<User> executeQuery(UserQuery query, Range range)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Group> executeQuery(GroupQuery query, Range range)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Role> executeQuery(RoleQuery query, Range range)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Membership> executeQuery(MembershipQuery query, Range range)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAttribute(User user, String name, String[] values)
    {
        if(user instanceof LDAPUser == false){
            throw new RuntimeException("Wrong type:" + user);
        }
        LDAPUser ldapUser = (LDAPUser) user;
        ldapUser.setAttribute(name, values);
    }

    @Override
    public void removeAttribute(User user, String name)
    { 
        if(user instanceof LDAPUser == false){
            throw new RuntimeException("Wrong type:" + user);
        }
        LDAPUser ldapUser = (LDAPUser) user;
        ldapUser.removeAttribute(name);
    }

    @Override
    public String[] getAttributeValues(User user, String name)
    { 
        if(user instanceof LDAPUser == false){
            throw new RuntimeException("Wrong type:" + user);
        }
        LDAPUser ldapUser = (LDAPUser) user;
        return ldapUser.getAttributeValues(name);
    }

    @Override
    public Map<String, String[]> getAttributes(User user)
    {
        if(user instanceof LDAPUser == false){
            throw new RuntimeException("Wrong type:" + user);
        }
        LDAPUser ldapUser = (LDAPUser) user;
        return ldapUser.getAttributes();
    }

    @Override
    public void setAttribute(Group group, String name, String[] values)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAttribute(Group group, String name)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getAttributeValues(Group group, String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String[]> getAttributes(Group group)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAttribute(Role role, String name, String[] values)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAttribute(Role role, String name)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getAttributeValues(Role role, String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String[]> getAttributes(Role role)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected String getFirstName(String name){
        String[] tokens = name.split("\\ ");
        int length = tokens.length;
        String firstName = null;
        
        if(length > 0){
            firstName = tokens[0];
        }
        return firstName;
    }
    
    protected String getLastName(String name){
        String[] tokens = name.split("\\ ");
        int length = tokens.length;
        String lastName = null;
         
        if(length > 2){
            lastName = tokens[2];
        } else {
            lastName = tokens[1];
        }
        return lastName;
    }

    protected String generateUserID(String firstName, String lastName){
        char f = firstName.charAt(0);
        StringBuilder builder = new StringBuilder();
        builder.append(f).append(lastName);

        String userID = builder.toString();
        int length = userID.length();
        if(length > 7){
            return userID.substring(0,7);
        }else {
            return userID;
        }
    }
}