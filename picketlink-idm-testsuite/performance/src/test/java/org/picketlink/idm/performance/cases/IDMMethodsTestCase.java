/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashSet;
import java.util.Set;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;
import org.junit.Ignore;
import org.junit.Test;

/*
 *
 * @author vrockai
 */
public class IDMMethodsTestCase extends DBTestBase {

    private boolean triggerGc = false;

    @Test    
    public void testCreateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrUsr";
        String pfxGrp = "grpUsr";
        String pfxRol = "rolUsr";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        for (int i = 0; i < n; i++) {
            startStopwatch();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            stopStopwatch();
            storeStopwatch(i);
            if (triggerGc) {
                System.gc();
            }
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph(getGraphNamePfx() + ".testCreateUsers()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testRemoveUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrUre";
        String pfxGrp = "grpUre";
        String pfxRol = "rolUre";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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

        generateGraph(getGraphNamePfx() + ".testRemoveUsers()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testCreateGroups() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrGrp";
        String pfxGrp = "grpGrp";
        String pfxRol = "rolGrp";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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

        generateGraph(getGraphNamePfx() + ".testCreateGroups()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testRemoveGroups() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrGrpRem";
        String pfxGrp = "grpGrpRem";
        String pfxRol = "rolGrpRem";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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

        generateGraph(getGraphNamePfx() + ".testRemoveGroups()", this.getClass().getName());
    }

    @Test
    public void testAssociateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsG";
        String pfxGrp = "grpAsG";
        String pfxRol = "rolAsG";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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

        
        generateGraph(getGraphNamePfx() + ".testAssociateUsers()", this.getClass().getName());
    }


    @Test
    public void testAssociateUsersContinue() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsG";
        String pfxGrp = "grpAsG";
        String pfxRol = "rolAsG";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().findGroup(pfxGrp + "GROUP" + n, ORGANIZATION);

        Collection<User> userList = new ArrayList<User>();

        for (int i = n+n+1; i < n+n+n+1; i++) {
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


        generateGraph(getGraphNamePfx() + ".testAssociateUsers2()", this.getClass().getName());
    }

    @Test    
    public void testAssociateUsers2() throws Exception {

        //resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsG";
        String pfxGrp = "grpAsG";
        String pfxRol = "rolAsG";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();
        
        Group group = identitySession.getPersistenceManager().findGroup(pfxGrp + "GROUP" + n, ORGANIZATION);

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
      //      if (i==0){
      //          System.out.println("Start 10");
      //      }//
            User user = identitySession.getPersistenceManager().findUser(pfxUsr + i);
            identitySession.getRelationshipManager().associateUser(group, user);
            identitySession.getTransaction().commit();
            identitySession.close();
        }

    }
   
    
    @Test
    //@Ignore
    public void testDisassociateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrDAsG";
        String pfxGrp = "grpDAsG";
        String pfxRol = "rolDAsG";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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
        storeStopwatch(0);
        storeStopwatch(1);

        identitySession.getTransaction().commit();
        identitySession.close();
        generateGraph(getGraphNamePfx() + ".testDisassociateUsers()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testCreateRoleTypes() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrCrR";
        String pfxGrp = "grpCrR";
        String pfxRol = "rolCrR";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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
        generateGraph(getGraphNamePfx() + ".testCreateRoleTypes()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testRemoveRoleTypes() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrCrRRem";
        String pfxGrp = "grpCrRRem";
        String pfxRol = "rolCrRRem";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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
        generateGraph(getGraphNamePfx() + ".testRemoveRoleTypes()", this.getClass().getName());
    }

    @Test
//    @Ignore
    public void testAssociateRoles() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsR";
        String pfxGrp = "grpAsR";
        String pfxRol = "rolAsR";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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
        generateGraph(getGraphNamePfx() + ".testAssociateRoles()", this.getClass().getName());
    }

    @Test
    //  @Ignore
    public void testRemoveRoles() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsRRem";
        String pfxGrp = "grpAsRRem";
        String pfxRol = "rolAsRRem";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
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
        generateGraph(getGraphNamePfx() + ".testDisassociateRoles()", this.getClass().getName());
    }

    @Test
    //@Ignore
    public void testNothing() throws IdentityException {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrAsRRem";
        String pfxGrp = "grpAsRRem";
        String pfxRol = "rolAsRRem";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


    }
 
}
