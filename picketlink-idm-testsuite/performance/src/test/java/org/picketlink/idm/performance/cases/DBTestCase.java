/*
 * JBoss, a division of Red Hat
 * Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.util.ArrayList;
import java.util.List;

import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.junit.*;

/**
 * @author <a href="mailto:boleslaw.dawidowicz at redhat.com">Boleslaw
 *         Dawidowicz</a>
 * @version : 0.1 $
 */
public class DBTestCase extends DBTestBase {

    

    @Test
    @Ignore
    public void testPopulateIdentityInOneTransaction() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrtestPopulateIdentityInOneTransaction";
        String pfxGrp = "grptestPopulateIdentityInOneTransaction";
        String pfxRol = "roltestPopulateIdentityInOneTransaction";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        for (int i = 0; i < n; i++) {
            startStopwatch();
            createUser(pfxUsr, identitySession, group, role, i);
            stopStopwatch();
            storeStopwatch(i);
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph("testPopulateIdentityInOneTransaction", this.getClass().getName());
    }

    @Test
    @Ignore
    public void testPopulateIdentity() throws Exception {

        resetMeasure();

        int n = 200;

        String pfxUsr = "usrXtestPopulateIdentity";
        String pfxGrp = "grpXtestPopulateIdentity";
        String pfxRol = "rolXtestPopulateIdentity";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        for (int i = 0; i < n; i++) {
            startStopwatch();
            createUser(pfxUsr, identitySession, group, role, i);
            stopStopwatch();
            storeStopwatch(i);
        }

        identitySession.getTransaction().commit();

        identitySession.close();

        generateGraph("populateIdentity", this.getClass().getName());
    }

    @Test
    @Ignore
    public void testPopulateIdentity1() throws Exception {

        String pfxUsr = "usrXtestPopulateIdentity1";
        String pfxGrp = "grpXtestPopulateIdentity1";
        String pfxRol = "rolXtestPopulateIdentity1";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP", ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
    //    logger.info("start creating first user!");
        identitySession.getPersistenceManager().createUser("FirstUser");
    //    logger.info("stop creating first user!");
        identitySession.getTransaction().commit();

        identitySession.close();

    }

    @Test
    @Ignore
    public void testPopulateAndassociateIdentity1() throws Exception {
        String pfxUsr = "usrXtestPopulateAndassociateIdentity1";
        String pfxGrp = "grpXtestPopulateAndassociateIdentity1";
        String pfxRol = "rolXtestPopulateAndassociateIdentity1";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        identitySession.beginTransaction();

     //   logger.info("start creating first group!");
        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP", ORGANIZATION);
     //   logger.info("stop creating first group!");

        //RoleType role = identitySession.getRoleManager().createRoleType(pfxRol+"manager");

    //    logger.info("start creating first user!");
        User user1 = identitySession.getPersistenceManager().createUser("FirsttestPopulateAndassociateIdentity1User");
    //    logger.info("------------------");
        User user2 = identitySession.getPersistenceManager().createUser("SecondtestPopulateAndassociateIdentity1User");
    //    logger.info("------------------");
        User user3 = identitySession.getPersistenceManager().createUser("ThirdtestPopulateAndassociateIdentity1User");
    //    logger.info("stop creating first user!");

    //    logger.info("start associating first user!");
        identitySession.getRelationshipManager().associateUser(group, user1);
    //    logger.info("------------------");
        identitySession.getRelationshipManager().associateUser(group, user2);
   //     logger.info("------------------");
        identitySession.getRelationshipManager().associateUser(group, user3);
    //    logger.info("stop associating first user!");
        identitySession.getTransaction().commit();

        identitySession.close();


    }

    @Test
    @Ignore
    public void testPopulateIdentityInManyTransactions() throws Exception {

        resetMeasure();

        int n = USER_NUM;
        String pfxUsr = "usrManytestPopulateIdentityInManyTransactions";
        String pfxGrp = "grpManytestPopulateIdentityInManyTransactions";
        String pfxRol = "rolManytestPopulateIdentityInManyTransactions";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

        identitySession.beginTransaction();
        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "managerMany");
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {

            identitySession.beginTransaction();

            startStopwatch();
            createUser(pfxUsr, identitySession, group, role, i);
            stopStopwatch();
            storeStopwatch(i);

            identitySession.getTransaction().commit();

        }

        identitySession.close();

        generateGraph("testPopulateIdentityInManyTransactions", this.getClass().getName());
    }

    @Test
    @Ignore
    public void testPopulateIdentityInManySessions() throws Exception {

        resetMeasure();

        int n = USER_NUM;
        String pfxUsr = "usrManyStestPopulateIdentityInManySessions";
        String pfxGrp = "grpManyStestPopulateIdentityInManySessions";
        String pfxRol = "rolManyStestPopulateIdentityInManySessions";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

        identitySession.beginTransaction();
        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "managerMany");
        identitySession.getTransaction().commit();
        identitySession.close();


        for (int i = 0; i < n; i++) {
            identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

            identitySession.beginTransaction();

            startStopwatch();
            createUser(pfxUsr, identitySession, group, role, i);
            stopStopwatch();
            storeStopwatch(i);

            identitySession.getTransaction().commit();
            identitySession.close();
            identitySession = null;
        }

        generateGraph("testPopulateIdentityInManySessions", this.getClass().getName());
    }

    @Test
    @Ignore
    public void testCreateUsersInManyTransactions() throws Exception {
       // logger.info("testCreateUsersInManyTransactions");
        resetMeasure();

        int n = USER_NUM;
        String pfxUsr = "usrCrManytestCreateUsersInManyTransactions";
        String pfxGrp = "grpCrManytestCreateUsersInManyTransactions";
        String pfxRol = "rolCrManytestCreateUsersInManyTransactions";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

        identitySession.beginTransaction();
        identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        identitySession.getRoleManager().createRoleType(pfxRol + "managerMany");
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {

            identitySession.beginTransaction();

            startStopwatch();
            identitySession.getPersistenceManager().createUser(pfxUsr + i);
            stopStopwatch();
            storeStopwatch(i);

            identitySession.getTransaction().commit();
            identitySession.close();
        }

        generateGraph("testCreateUsersInManyTransactions", this.getClass().getName());
    }

    @Test
    public void testAssociateUsersInManyTransactions() throws Exception {
       // logger = null;

        int n = 500;
        String pfxUsr = "usrAsManytestAssociateUsersInManyTransactions";
        String pfxGrp = "grpAsManytestAssociateUsersInManyTransactions";

        List<User> userList = new ArrayList<User>();

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");

        identitySession.beginTransaction();
        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int i = 0; i < n; i++) {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
            userList.add(user);
            identitySession.getTransaction().commit();
            identitySession.close();
        }
        identitySession.beginTransaction();

        System.gc();
        for (User u : userList) {
            identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
            identitySession.beginTransaction();
            identitySession.getRelationshipManager().associateUser(group, u);
            identitySession.getTransaction().commit();
            identitySession.close();
            System.gc();
        }

        identitySession.close();

    }

    public void createUser(String pfxUsr, IdentitySession identitySession, Group group, RoleType role, int i) throws IdentityException,
            FeatureNotSupportedException {
        User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
        identitySession.getAttributesManager().updatePassword(user, "oldPassword");
        identitySession.getAttributesManager().updatePassword(user, "newPassword");
        identitySession.getRelationshipManager().associateUser(group, user);
        identitySession.getRoleManager().createRole(role, user, group);
    }
}
