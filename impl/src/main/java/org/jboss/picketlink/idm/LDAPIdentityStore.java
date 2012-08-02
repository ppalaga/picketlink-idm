package org.jboss.picketlink.idm;

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

import java.util.List;
import java.util.Map;

/**
 * An IdentityStore implementation backed by an LDAP directory 
 *
 * @author Shane Bryzak
 */
public class LDAPIdentityStore implements IdentityStore
{

    @Override
    public User createUser(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeUser(User user)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public User getUser(String name)
    {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeAttribute(User user, String name)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String[] getAttributeValues(User user, String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String[]> getAttributes(User user)
    {
        // TODO Auto-generated method stub
        return null;
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

}
