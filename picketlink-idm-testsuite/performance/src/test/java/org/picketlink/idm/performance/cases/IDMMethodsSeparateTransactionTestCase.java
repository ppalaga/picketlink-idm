/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.util.ArrayList;
import java.util.Collection;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.junit.Test;

/**
 *
 * @author vrockai
 */
public class IDMMethodsSeparateTransactionTestCase extends DBTestBase {

    

    @Test
    public void testCreateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsUsr";
        String pfxGrp = "grpsUsr";
        String pfxRol = "rolsUsr";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            startStopwatch();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            stopStopwatch();
            storeStopwatch(i);
            System.gc();
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        generateGraph(getGraphNamePfx() + ".testCreateUsers()", this.getClass().getName());
    }

    @Test
    public void testRemoveUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsUre";
        String pfxGrp = "grpsUre";
        String pfxRol = "rolsUre";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            System.gc();
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            startStopwatch();
            identitySession.getPersistenceManager().removeUser(pfxUsr + i, true);
            stopStopwatch();
            storeStopwatch(i);
            System.gc();
            identitySession.getTransaction().commit();
            identitySession.close();
        }


        generateGraph(getGraphNamePfx() + ".testRemoveUsers()", this.getClass().getName());
    }

    @Test
    public void testCreateGroups() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsGrp";
        String pfxGrp = "grpsGrp";
        String pfxRol = "rolsGrp";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            startStopwatch();
            Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + i, ORGANIZATION);
            stopStopwatch();
            storeStopwatch(i);
            System.gc();
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        generateGraph(getGraphNamePfx() + ".testCreateGroups()", this.getClass().getName());
    }

    @Test
    public void testAssociateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsAsG";
        String pfxGrp = "grpsAsG";
        String pfxRol = "rolsAsG";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        int c = 0;
        for (User user : userList) {
            identitySession.beginTransaction();
            startStopwatch();
            identitySession.getRelationshipManager().associateUser(group, user);
            stopStopwatch();
            storeStopwatch(c++);
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        generateGraph(getGraphNamePfx() + ".testAssociateUsers()", this.getClass().getName());
    }

    @Test
    public void testCreateRoleTypes() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsCrR";
        String pfxGrp = "grpsCrR";
        String pfxRol = "rolsCrR";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        Collection<User> userList = new ArrayList<User>();

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            startStopwatch();
            RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
            stopStopwatch();
            storeStopwatch(i);
            identitySession.getTransaction().commit();
            identitySession.close();
        }


        generateGraph(getGraphNamePfx() + ".testCreateRoleTypes()", this.getClass().getName());
    }

    @Test
    public void testAssociateRoles() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsAsR";
        String pfxGrp = "grpsAsR";
        String pfxRol = "rolsAsR";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        int c = 0;
        for (User user : userList) {
            identitySession.beginTransaction();
            startStopwatch();
            identitySession.getRoleManager().createRole(role, user, group);
            stopStopwatch();
            storeStopwatch(c++);
            identitySession.getTransaction().commit();
            identitySession.close();
        }

        generateGraph(getGraphNamePfx() + ".testAssociateRoles()", this.getClass().getName());
    }
}
