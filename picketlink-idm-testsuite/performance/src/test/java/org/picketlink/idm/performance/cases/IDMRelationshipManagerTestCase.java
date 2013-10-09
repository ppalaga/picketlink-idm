/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.junit.Test;

/**
 *
 * @author vrockai
 */
public class IDMRelationshipManagerTestCase extends DBTestBase {

    

    @Test
    public void testCreateUsers() throws Exception {

        resetMeasure();

        int n = USER_NUM;

        String pfxUsr = "usrsUsrRelMan";
        String pfxGrp = "grpsUsrRelMan";
        String pfxRol = "rolsUsrRelMan";

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
}
