package org.jboss.picketlink.idm.internal;

import java.util.Collection;
import java.util.Date;

import org.jboss.picketlink.idm.IdentityManager;
import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.IdentityType;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.query.GroupQuery;
import org.jboss.picketlink.idm.query.MembershipQuery;
import org.jboss.picketlink.idm.query.RoleQuery;
import org.jboss.picketlink.idm.query.UserQuery;

/**
 * Default implementation of the IdentityManager interface 
 *
 * @author Shane Bryzak
 */
public class DefaultIdentityManager implements IdentityManager
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
    public void removeUser(String name)
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
    public Collection<User> getAllUsers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group createGroup(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group createGroup(String id, Group parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group createGroup(String id, String parent)
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
    public void removeGroup(String groupId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Group getGroup(String groupId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group getGroup(String groupId, Group parent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Group> getAllGroups()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToGroup(IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeFromGroup(IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Collection<IdentityType> getGroupMembers(Group group)
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
    public void removeRole(String name)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Role getRole(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Role> getAllRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Role> getRoles(IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasRole(Role role, IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void grantRole(Role role, IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void revokeRole(Role role, IdentityType identityType, Group group)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UserQuery createUserQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GroupQuery createGroupQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RoleQuery createRoleQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MembershipQuery createMembershipQuery()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean validatePassword(String password)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updatePassword(String password)
    {
        // TODO Auto-generated method stub
        
    }

    public void setEnabled(IdentityType identityType, boolean enabled)
    {
        // TODO Auto-generated method stub
        
    }

    public void setExpirationDate(IdentityType identityType, Date expirationDate)
    {
        // TODO Auto-generated method stub
        
    }

}
