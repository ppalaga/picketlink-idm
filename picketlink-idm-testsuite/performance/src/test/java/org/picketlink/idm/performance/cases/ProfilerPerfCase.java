/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.User;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author vrockai
 */
public class ProfilerPerfCase extends TestBase {

    private static int USER_NUM = 1000;

    @Before
    public void setUp() throws Exception {
        if (identitySessionFactory == null) {
            identitySessionFactory = new IdentityConfigurationImpl().configure(new File("src/test/resources/example-db-config.xml")).buildIdentitySessionFactory();
            logger.info("creating sess factory");
        }

        test_id = System.currentTimeMillis();
    }

    @After
    public void tearDown() throws Exception {
        if (identitySessionFactory != null) {
            identitySessionFactory.close();
            logger.info("closing sess factory");
        }
    }
    /*
    @Test
    @Ignore
    public void testAssociateUsersInManyTransactions() throws Exception {
    logger = null;

    int n = USER_NUM;
    String pfxUsr = "usrAsMany";
    String pfxGrp = "grpAsMany";

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


    for (User u : userList) {
    identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
    identitySession.beginTransaction();
    identitySession.getRelationshipManager().associateUser(group, u);
    identitySession.getTransaction().commit();
    identitySession.close();
    }

    identitySession.close();

    }
     */

    @Test
    @Ignore
    public void testFindUsers() throws Exception {


        int n = USER_NUM;
        String pfxUsr = "usrAsMany";
        String pfxGrp = "grpAsMany";

        List<User> userList = new ArrayList<User>();

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");
        /*
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
         */
        //      resetMeasure();
        logger.info("here");
        for (int i = 0; i < n; i++) {

            identitySession.beginTransaction();
            //startStopwatch();
            identitySession.getPersistenceManager().findUser(pfxUsr + i);
            //stopStopwatch();
            identitySession.getTransaction().commit();
            identitySession.close();
            //storeStopwatch(i);
            System.gc();
        }

        identitySession.close();
        generateGraph(getGraphNamePfx() + ".perf()", this.getClass().getName());
    }
}
