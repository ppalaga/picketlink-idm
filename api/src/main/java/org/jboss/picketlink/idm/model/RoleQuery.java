package org.jboss.picketlink.idm.model;

import java.util.List;
import java.util.Map;

/**
 * RoleQuery. All applied conditions will be resolved with logical AND.
 */
public interface RoleQuery
{
    //TODO: Javadocs
    //TODO: Exceptions

    // Operations

    RoleQuery reset();

    RoleQuery getImmutable();

    List<Role> executeQuery(RoleQuery query);

    // Conditions

    RoleQuery setName(String name);

    RoleQuery setOwner(IdentityType owner);

    RoleQuery setGroup(Group group);

    RoleQuery setGroup(String groupId);

    RoleQuery setAttributeFilter(String name, String[] values);

    Map<String, String[]> getAttributeFilters();

    RoleQuery sort(boolean ascending);

    void setRange(Range range);

    Range getRange();

}
