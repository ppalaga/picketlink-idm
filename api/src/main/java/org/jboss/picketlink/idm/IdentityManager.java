package org.jboss.picketlink.idm;

import java.util.Collection;
import java.util.Date;

import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.GroupQuery;
import org.jboss.picketlink.idm.model.IdentityType;
import org.jboss.picketlink.idm.model.MembershipQuery;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.RoleQuery;
import org.jboss.picketlink.idm.model.User;
import org.jboss.picketlink.idm.model.UserQuery;

/**
 * IdentityManager
 */
public interface IdentityManager
{
    //TODO: Javadocs

    //TODO: Exceptions

    //TODO: control hooks & events

    //TODO: linking identities


    // User

    User createUser(String name);

    void removeUser(User user);

    void removeUser(String name);

    User getUser(String name);

    Collection<User> getAllUsers();


    // Group

    Group createGroup(String id);

    Group createGroup(String id, Group parent);

    Group createGroup(String id, String parent);

    void removeGroup(Group group);

    void removeGroup(String groupId);

    Group getGroup(String groupId);

    Group getGroup(String groupId, Group parent);

    Collection<Group> getAllGroups();
    
    void addToGroup(IdentityType identityType, Group group);
    
    void removeFromGroup(IdentityType identityType, Group group);

    Collection<IdentityType> getGroupMembers(Group group);   

    // Roles

    Role createRole(String name);

    void removeRole(Role role);

    void removeRole(String name);

    Role getRole(String name);

    Collection<Role> getAllRoles();

    Collection<Role> getRoles(IdentityType identityType, Group group);

    boolean hasRole(Role role, IdentityType identityType, Group group);
    
    void grantRole(Role role, IdentityType identityType, Group group);
    
    void revokeRole(Role role, IdentityType identityType, Group group);

    // Queries

    UserQuery createUserQuery();

    GroupQuery createGroupQuery();

    RoleQuery createRoleQuery();

    MembershipQuery createMembershipQuery();

    // Password Management
    
    boolean validatePassword(String password);

    void updatePassword(String password);
    
    // User / Role / Group enablement / expiry

    void setEnabled(IdentityType identityType, boolean enabled);    

    void setExpirationDate(IdentityType identityType, Date expirationDate);    
}
