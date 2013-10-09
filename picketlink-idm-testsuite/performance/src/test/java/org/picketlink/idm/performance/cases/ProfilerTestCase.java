/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.util.ArrayList;
import java.util.List;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.User;
import org.junit.Test;

/**
 *
 * @author vrockai
 */
public class ProfilerTestCase extends DBTestBase {

    

    @Test
    public void testAssociateUsersInManyTransactions() throws Exception {
        logger = null;

        int n = USER_NUM;
        String pfxUsr = "usrAsManyProfilerTestCase";
        String pfxGrp = "grpAsManyProfilerTestCase";

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
}
