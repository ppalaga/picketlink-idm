package org.jboss.picketlink.idm.query;

import java.util.List;
import java.util.Map;

import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;

/**
 * GroupQuery. All applied conditions will be resolved with logical AND.
 */
public interface GroupQuery
{
    //TODO: Javadocs
    //TODO: Exceptions

    // Operations

    GroupQuery reset();

    GroupQuery immutable();

    List<Group> executeQuery(GroupQuery query);


    // Conditions

    GroupQuery setName(String name);

    String getName();

    GroupQuery setId(String id);

    String getId();

    GroupQuery setParentGroup(Group group);

    GroupQuery setParentGroup(String groupId);

    Group getParentGroup();

    GroupQuery setRole(Role role);

    GroupQuery setRole(String role);

    Role getRole();

    GroupQuery setRelatedUser(User user);

    GroupQuery setRelatedUser(String user);

    User getRelatedUser();

    GroupQuery addAttributeFilter(String name, String[] values);

    Map<String, String[]> getAttributeFilters();

    GroupQuery sort(boolean ascending);

    void setRange(Range range);

    Range getRange();

}
