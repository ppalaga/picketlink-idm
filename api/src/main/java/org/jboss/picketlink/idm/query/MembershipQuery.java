package org.jboss.picketlink.idm.query;

import java.util.List;

import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.Membership;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;

/**
 * MembershipQuery. All applied conditions will be resolved with logical AND.
 */
public interface MembershipQuery
{
    //TODO: Javadocs
    //TODO: Exceptions

    // Operations

    MembershipQuery reset();

    MembershipQuery immutable();

    List<Membership> executeQuery(MembershipQuery query);


    // Conditions

    MembershipQuery setUser(User user);

    MembershipQuery setUser(String user);

    User getUser();

    MembershipQuery setGroup(Group group);

    MembershipQuery setGroup(String groupId);

    Group getGroup();

    MembershipQuery setRole(Role role);

    MembershipQuery setRole(String role);

    Role getRole();

    void setRange(Range range);

    Range getRange();

}
