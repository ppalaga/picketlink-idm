package org.jboss.picketlink.idm.model;


/**
 * Group representation
 */
public interface Group extends IdentityType
{
    //TODO: Javadocs
    //TODO: Exceptions

    //TODO: getId() -> getPath()? Should it stick to natural Id(path) or have non meaningful one

    // Self related

    /**
     * Groups are stored in tree hierarchy and therefore ID represents a path. ID string always
     * begins with "/" element that represents root of the tree
     * <p/>
     * Example: Valid IDs are "/acme/departments/marketing", "/security/administrator" or "/administrator".
     * Where "acme", "departments", "marketing", "security" and "administrator" are group names.
     *
     * @return Group Id in String representation.
     */
    String getId();

    /**
     * Group name is unique identifier in specific group tree branch. For example
     * group with id "/acme/departments/marketing" will have name "marketing" and
     * parent group of id "/acme/departments"
     *
     * @return name
     */
    String getName();

    // Sub groups

    /**
     * @return parent group or null if it refers to root ("/") in a group tree.
     */
    Group getParentGroup();

}
