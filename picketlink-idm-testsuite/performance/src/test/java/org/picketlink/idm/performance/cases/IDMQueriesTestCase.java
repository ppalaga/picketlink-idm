/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.cases;

import org.picketlink.idm.performance.*;
import java.util.Collection;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
//import org.junit.Ignore;
import org.junit.Test;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;

/**
 *
 * @author vrockai
 */
public class IDMQueriesTestCase extends DBTestBase {

    @Test
    // @Ignore
    public void testFindUser() throws Exception {
       // //System.out.println("testFindUser");
        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFnutestFindUser";
        String pfxGrp = "grpFnutestFindUser";
        String pfxRol = "rolFnutestFindUser";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //  logger.info(j+"");
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                User user = identitySession.getPersistenceManager().findUser(pfxUsr + i);
               // //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".testFindUser()", this.getClass().getName());
    }

    @Test
    // @Ignore
    public void testFindUsers() throws Exception {
        //System.out.println("testFindUsers");
        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFnutestFindUsers";
        String pfxGrp = "grpFnutestFindUsers";
        String pfxRol = "rolFnutestFindUsers";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //  logger.info(j+"");
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User user = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl();
                isc.page(0, 100);
                Collection<User> user = (Collection<User>) identitySession.getPersistenceManager().findUser(isc);
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".testFindUsers()", this.getClass().getName());
    }

    @Test
    public void testFindGroupByType() throws Exception {
        //System.out.println("testFindGroupByType");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngtestFindGroup";
        String pfxGrp = "grpFngtestFindGroup";
        String pfxRol = "rolFngtestFindGroup";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                identitySession.getPersistenceManager().findGroup(ORGANIZATION);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".testFindGroupByType()", this.getClass().getName());
    }

    @Test
    public void testFindGroupByName() throws Exception {
        //System.out.println("testFindGroupByName");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngtestFindGroupn";
        String pfxGrp = "grpFngtestFindGroupn";
        String pfxRol = "rolFngtestFindGroupn";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                identitySession.getPersistenceManager().findGroup(pfxGrp + i, ORGANIZATION);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".testFindGroupByName()", this.getClass().getName());
    }

    @Test
    public void testFindGroups() throws Exception {
        //System.out.println("testFindGroup");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngtest1FindGroups";
        String pfxGrp = "grpFngtest1FindGroups";
        String pfxRol = "rolFngtest1FindGroups";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                IdentitySearchCriteria ic = new IdentitySearchCriteriaImpl();
                ic.page(0, 100);
                startStopwatch();

                //System.out.println("-");
                Collection<Group> cg = identitySession.getPersistenceManager().findGroup(ORGANIZATION, ic);
                //System.out.println(cg.size());
                //System.out.println("+");
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".testFindGroups()", this.getClass().getName());
    }

    @Test
    //  @Ignore
    public void testfindGroupRoleTypes() throws Exception {
        //System.out.println("testfindGroupRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngfi3ndGroupRoleTypes";
        String pfxGrp = "grpFngfin3dGroupRoleTypes";
        String pfxRol = "rolFngfi3ndGroupRoleTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User user = identitySession.getPersistenceManager().createUser(pfxUsr);

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                identitySession.getRoleManager().createRole(role, u, group);

                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                Collection<RoleType> roleTypeCol = identitySession.getRoleManager().findGroupRoleTypes(group);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".findGroupRoleTypes()", this.getClass().getName());
    }

    @Test
    // @Ignore
    public void testfindGroupsWithRelatedRole() throws Exception {
        //System.out.println("testfindGroupsWithRelatedRole");


        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngfindGroup4sWithRelatedRole";
        String pfxGrp = "grpFngfindGroup4sWithRelatedRole";
        String pfxRol = "rolFngfindGroup4sWithRelatedRole";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User user = identitySession.getPersistenceManager().createUser(pfxUsr);
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();

                RoleType rt = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(rt, user, g);

                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                IdentitySearchCriteria ic = new IdentitySearchCriteriaImpl();
                ic.page(0, 20);
                startStopwatch();
                //System.out.println("-");
                Collection<Group> grps = identitySession.getRoleManager().findGroupsWithRelatedRole(user, ic);
                //System.out.println(grps.size());
                //System.out.println("+");
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx() + ".findGroupsWithRelatedRole()", this.getClass().getName());
    }

    @Test
    // @Ignore
    public void testfindRoleTypes() throws Exception {
        //System.out.println("testfindRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrtestfindRo5leTypes";
        String pfxGrp = "grptestfindRo5leTypes";
        String pfxRol = "roltestfindRo5leTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User user = identitySession.getPersistenceManager().createUser(pfxUsr);

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            Group g = null;
            User u = null;
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();

                RoleType rt = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
                identitySession.getRoleManager().createRole(rt, user, group);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                identitySession.getRoleManager().findRoleTypes(user, group);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx() + ".findRoleTypes()", this.getClass().getName());
    }

    //TODO
    @Test
    // @Ignore
    public void testfindRoles() throws Exception {
        //System.out.println("testfindRoles");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrFngfindRo6lestestfindRoles";
        String pfxGrp = "grpFngfindRo6lestestfindRoles";
        String pfxRol = "rolFngfindRo6lestestfindRoles";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User user = identitySession.getPersistenceManager().createUser(pfxUsr);
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * (2 * step); i < (j * 2 * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(roleType, u, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = (j * 2 * step) + step; i < (j + 1) * (2 * step); i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().findUser(pfxUsr + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                startStopwatch();
                identitySession.getRoleManager().findRoles(pfxRol, pfxRol);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx() + ".findRoles()", this.getClass().getName());
    }

    @Test
    // @Ignore
    public void testFindUserRoleTypes() throws Exception {
        //System.out.println("testFindUserRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "usrtestFin7dUserRoleTypes";
        String pfxGrp = "grptestFin7dUserRoleTypes";
        String pfxRol = "roltestFin7dUserRoleTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User user = identitySession.getPersistenceManager().createUser(pfxUsr);
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i + "GROUP", ORGANIZATION);
                identitySession.getRoleManager().createRole(roleType, user, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();

                startStopwatch();
                Collection<RoleType> roleTypeCol = identitySession.getRoleManager().findUserRoleTypes(user);
                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx() + ".testFindUserRoleTypes()", this.getClass().getName());
    }

    @Test
    // @Ignore
    public void testFindUserRoleTypesCol() throws Exception {
        //System.out.println("testFindUserRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "us9rtestFindUserRoleTypesCol";
        String pfxGrp = "gr9ptestFindUserRoleTypesCol";
        String pfxRol = "ro9ltestFindUserRoleTypesCol";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");
        User u = identitySession.getPersistenceManager().createUser(pfxUsr + "0x0");
        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                RoleType rt = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(rt, u, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();

                IdentitySearchCriteria ic = new IdentitySearchCriteriaImpl();
                ic.page(0, 20);
                startStopwatch();

                //System.out.println("-");

                startStopwatch();
                Collection<RoleType> roleTypeCol = identitySession.getRoleManager().findUserRoleTypes(u, ic);
                //System.out.println(roleTypeCol.size());
                stopStopwatch();
                //System.out.println("+");
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx() + ".testFindUserRoleTypesCol()", this.getClass().getName());
    }

    @Test
    //  @Ignore
    public void testfindGroupRoleTypesCol() throws Exception {
        //System.out.println("testfindGroupRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = "us7rFngfindGroupRoleTypesCol";
        String pfxGrp = "gr7pFngfindGroupRoleTypesCol";
        String pfxRol = "ro7lFngfindGroupRoleTypesCol";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession("realm::JBossIdentityExample_SampleRealm");


        identitySession.beginTransaction();
        User u = identitySession.getPersistenceManager().createUser(pfxUsr + "0x0");
        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                RoleType rt = identitySession.getRoleManager().createRoleType(pfxRol + "manager" + i);
                identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(rt, u, group);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                IdentitySearchCriteria ic = new IdentitySearchCriteriaImpl();
                ic.page(0, 100);
                startStopwatch();
                //System.out.println("-");
                Collection<RoleType> colg = identitySession.getRoleManager().findGroupRoleTypes(group, ic);
                //System.out.println(colg.size());
                //System.out.println("+");

                //System.out.println();
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx() + ".findGroupRoleTypesCol()", this.getClass().getName());
    }
}
