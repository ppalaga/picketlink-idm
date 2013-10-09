/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent.worker;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;

/**
 *
 * @author vrockai
 */
public class InsertDBWorker extends PerformanceWorker {

    private static final String REALM_NAME = "realm::JBossIdentityExample_SampleRealm";

    public enum TestMethod {

        CREATE_USERS, ASSOCIATE_ROLES, ASSOCIATE_USERS, CREATE_GROUPS, CREATE_ROLE_TYPES, DISSASSOCIATE_USERS, REMOVE_GROUPS, REMOVE_ROLES_TYPES, REMOVE_ROLES, REMOVE_USERS
    }
    protected TestMethod testType;

    public InsertDBWorker(IdentitySessionFactory identitySessionFactory) {
        super(identitySessionFactory);
    }

    public InsertDBWorker(IdentitySessionFactory identitySessionFactory, int size) {
        super(identitySessionFactory, size);
    }

    public InsertDBWorker(IdentitySessionFactory identitySessionFactory, int size, TestMethod testType) {
        super(identitySessionFactory, size);
        this.testType = testType;
    }

    @Override
    public void run() {

        Collection<Exception> exList = new ArrayList<Exception>();

        try {
            timeStart = System.currentTimeMillis();

            switch (testType) {
                case CREATE_USERS:
                    testCreateUsers();
                    break;
                case ASSOCIATE_ROLES:
                    testAssociateRoles();
                    break;
                case ASSOCIATE_USERS:
                    testAssociateUsers();
                    break;
                case CREATE_GROUPS:
                    testCreateGroups();
                    break;
                case CREATE_ROLE_TYPES:
                    testCreateRoleTypes();
                    break;
                case DISSASSOCIATE_USERS:
                    testDisassociateUsers();
                    break;
                case REMOVE_GROUPS:
                    testRemoveGroups();
                    break;
                case REMOVE_ROLES_TYPES:
                    testRemoveRoleTypes();
                    break;
                case REMOVE_ROLES:
                    testRemoveRoles();
                    break;
                case REMOVE_USERS:
                    testRemoveUsers();
                    break;
                default:
                    break;
            }

            timeEnd = System.currentTimeMillis();
        } catch (Exception ex) {
            exList.add(ex);
        }

        if (exList.size() > 0) {
            this.eList = exList;
        }
    }

    public void testCreateUsers() throws Exception {
        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrUsrworker" + worker_id + ".";
        String pfxGrp = "grpUsrworker" + worker_id + ".";
        String pfxRol = "rolUsrworker" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        String grpName = pfxGrp + "GROUP" + n;
        String manName = pfxRol + "manager";

        Group group = identitySession.getPersistenceManager().createGroup(grpName, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(manName);

        for (int i = 0; i < n; i++) {
            startStopwatch();
            String name = pfxUsr + i;
            User user = identitySession.getPersistenceManager().createUser(name);
            stopStopwatch();
            storeStopwatch(i);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph(getGraphNamePfx() + "." + worker_id + ".", this.getClass().getName() + ".testCreateUsers()");
    }

    public void testRemoveUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrUre" + worker_id + ".";
        String pfxGrp = "grpUre" + worker_id + ".";
        String pfxRol = "rolUre" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        identitySession.beginTransaction();

        for (int i = 0; i < n; i++) {

            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            if (triggerGc) {
                System.gc();
            }

        }

        identitySession.getTransaction().commit();
        identitySession.close();

        identitySession.beginTransaction();

        for (int i = 0; i < n; i++) {
            startStopwatch();
            identitySession.getPersistenceManager().removeUser(pfxUsr + i, true);
            stopStopwatch();
            storeStopwatch(i);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();
        identitySession.close();

        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testRemoveUsers()");
    }

    public void testCreateGroups() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrGrp" + worker_id + ".";
        String pfxGrp = "grpGrp" + worker_id + ".";
        String pfxRol = "rolGrp" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        for (int i = 0; i < n; i++) {
            startStopwatch();
            Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + i, ORGANIZATION);
            stopStopwatch();
            storeStopwatch(i);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testCreateGroups()");
    }

    public void testRemoveGroups() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrGrpRem" + worker_id + ".";
        String pfxGrp = "grpGrpRem" + worker_id + ".";
        String pfxRol = "rolGrpRem" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Collection<Group> groupCol = new ArrayList<Group>();

        for (int i = 0; i < n; i++) {
            Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + i, ORGANIZATION);
            groupCol.add(group);
        }

        int c = 0;
        for (Group group : groupCol) {
            startStopwatch();
            identitySession.getPersistenceManager().removeGroup(group, true);
            stopStopwatch();
            storeStopwatch(c++);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testRemoveGroups()");
    }

    public void testAssociateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsG" + worker_id + ".";
        String pfxGrp = "grpAsG" + worker_id + ".";
        String pfxRol = "rolAsG" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
        }

        identitySession.getTransaction().commit();
        identitySession.close();


        int c = 0;
        for (User user : userList) {
            identitySession.beginTransaction();

            startStopwatch();
            identitySession.getRelationshipManager().associateUser(group, user);
            stopStopwatch();

            storeStopwatch(c++);
            if (triggerGc) {
                System.gc();
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        }


        generateGraph(getGraphNamePfx() + "." + worker_id + ".", this.getClass().getName() + ".testAssociateUsers()");
    }

    public void testDisassociateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrDAsG" + worker_id + ".";
        String pfxGrp = "grpDAsG" + worker_id + ".";
        String pfxRol = "rolDAsG" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        identitySession.beginTransaction();

        int c = 0;
        for (User user : userList) {
            identitySession.getRelationshipManager().associateUser(group, user);
        }

        startStopwatch();
        identitySession.getRelationshipManager().disassociateUsers(group, userList);
        stopStopwatch();
        storeStopwatch(c++);

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testDisassociateUsers()");
    }

    public void testCreateRoleTypes() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrCrR" + worker_id + ".";
        String pfxGrp = "grpCrR" + worker_id + ".";
        String pfxRol = "rolCrR" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);


        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            startStopwatch();
            RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
            stopStopwatch();
            storeStopwatch(i);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testCreateRoleTypes()");
    }

    public void testRemoveRoleTypes() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrCrRRem" + worker_id + ".";
        String pfxGrp = "grpCrRRem" + worker_id + ".";
        String pfxRol = "rolCrRRem" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);


        Collection<RoleType> roleTypeList = new ArrayList<RoleType>();

        for (int i = 0; i < n; i++) {
            RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
            roleTypeList.add(role);
            if (triggerGc) {
                System.gc();
            }
        }

        int c = 0;
        for (RoleType roleType : roleTypeList) {
            startStopwatch();
            identitySession.getRoleManager().removeRoleType(roleType);
            stopStopwatch();
            storeStopwatch(c++);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testRemoveRoleTypes()");
    }

    public void testAssociateRoles() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsR" + worker_id + ".";
        String pfxGrp = "grpAsR" + worker_id + ".";
        String pfxRol = "rolAsR" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        identitySession.beginTransaction();

        int c = 0;
        for (User user : userList) {
            startStopwatch();
            identitySession.getRoleManager().createRole(role, user, group);
            stopStopwatch();
            storeStopwatch(c++);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testAssociateRoles()");
    }

    public void testRemoveRoles() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsRRem" + worker_id + ".";
        String pfxGrp = "grpAsRRem" + worker_id + ".";
        String pfxRol = "rolAsRRem" + worker_id + ".";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        Collection<User> userList = new ArrayList<User>();

        for (int i = 0; i < n; i++) {
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        identitySession.beginTransaction();


        for (User user : userList) {
            identitySession.getRoleManager().createRole(roleType, user, group);
            if (triggerGc) {
                System.gc();
            }
        }

        int c = 0;
        for (User user : userList) {
            startStopwatch();
            identitySession.getRoleManager().removeRole(roleType, user, group);
            stopStopwatch();
            storeStopwatch(c++);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx()  + "." + worker_id + ".", this.getClass().getName()+ ".testDisassociateRoles()");
    }
}
