package org.jboss.picketlink.idm.internal.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.jboss.picketlink.idm.model.Group;
import org.jboss.picketlink.idm.model.Membership;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.model.User;

@Entity
@NamedQuery(name = NamedQueries.MEMBERSHIP_LOAD_BY_KEY, query = "from DatabaseMembership where role = :role and user = :user and group = :group")
public class DatabaseMembership implements Membership {

    @Id
    @GeneratedValue
    private String id;

    @ManyToOne
    private DatabaseUser user;

    @ManyToOne
    private DatabaseGroup group;

    @ManyToOne
    private DatabaseRole role;

    public DatabaseMembership() {

    }

    public DatabaseMembership(Role role, User user, Group group) {
        setRole((DatabaseRole) role);
        setUser((DatabaseUser) user);
        setGroup((DatabaseGroup) group);
    }

    /**
     * @return
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the user
     */
    public DatabaseUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(DatabaseUser user) {
        this.user = user;
    }

    /**
     * @return the group
     */
    public DatabaseGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(DatabaseGroup group) {
        this.group = group;
    }

    /**
     * @return the role
     */
    public DatabaseRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(DatabaseRole role) {
        this.role = role;
    }

    // TODO: implement hashcode and equals methods
}
