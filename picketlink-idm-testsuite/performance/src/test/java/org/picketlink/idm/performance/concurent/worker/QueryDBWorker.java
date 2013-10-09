/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.performance.concurent.worker;

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
public class QueryDBWorker extends PerformanceWorker {

    private static final String REALM_NAME = "realm::JBossIdentityExample_SampleRealm";

    public enum TestMethod {

        FIND_GROUP, FIND_USER, FIND_USER_ROLETYPES, FIND_GROUP_ROLETYPES, FIND_GROUP_WITH_RELATED_ROLE, FIND_ROLE_TYPES, FIND_ROLES
    }
    protected TestMethod testType;

    public QueryDBWorker(IdentitySessionFactory identitySessionFactory) {
        super(identitySessionFactory);
    }

    public QueryDBWorker(IdentitySessionFactory identitySessionFactory, int size) {
        super(identitySessionFactory, size);
    }

    public QueryDBWorker(IdentitySessionFactory identitySessionFactory, int size, TestMethod testType) {
        super(identitySessionFactory, size);
        this.testType = testType;
    }

    @Override
    public void run() {

        Collection<Exception> exList = new ArrayList<Exception>();

        try {
            timeStart = System.currentTimeMillis();

            switch (testType) {
                case FIND_GROUP:
                    testFindGroup();
                    break;
                case FIND_USER:
                    testFindUser();
                    break;
                case FIND_USER_ROLETYPES:
                    testFindUserRoleTypes();
                    break;
                case FIND_GROUP_ROLETYPES:
                    testfindGroupRoleTypes();
                    break;
                case FIND_GROUP_WITH_RELATED_ROLE:
                    testfindGroupsWithRelatedRole();
                    break;
                case FIND_ROLE_TYPES:
                    testfindRoleTypes();
                    break;
                case FIND_ROLES:
                    testfindRoles();
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

     public void testFindUser() throws Exception {
        //logger.info("testFindUser");
        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id + "usrFnutestFindUser";
        String pfxGrp = worker_id + "grpFnutestFindUser";
        String pfxRol = worker_id + "rolFnutestFindUser";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


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
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName()+ ".testFindUser()");
    }

    public void testFindGroup() throws Exception {
        //logger.info("testFindGroup");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usrFngtestFindGroup";
        String pfxGrp = worker_id+"grpFngtestFindGroup";
        String pfxRol = worker_id+"rolFngtestFindGroup";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


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
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName()+ ".testFindGroup()");
    }

    public void testfindGroupRoleTypes() throws Exception {
        //logger.info("testfindGroupRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usrFngfindGroupRoleTypes";
        String pfxGrp = worker_id+"grpFngfindGroupRoleTypes";
        String pfxRol = worker_id+"rolFngfindGroupRoleTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


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
                Collection<RoleType> roleTypeCol = identitySession.getRoleManager().findGroupRoleTypes(group);
                identitySession.getRoleManager().findGroupsWithRelatedRole(pfxRol, null);
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }


        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName()+ ".findGroupRoleTypes()");
    }

    public void testfindGroupsWithRelatedRole() throws Exception {
        //      logger.info("testfindGroupsWithRelatedRole");


        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usrFngfindGroupsWithRelatedRole";
        String pfxGrp = worker_id+"grpFngfindGroupsWithRelatedRole";
        String pfxRol = worker_id+"rolFngfindGroupsWithRelatedRole";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType role = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
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
                identitySession.getRoleManager().findGroupsWithRelatedRole(pfxUsr + i, null);

                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName()+ ".findGroupsWithRelatedRole()");
    }

    public void testfindRoleTypes() throws Exception {
        //logger.info("testfindRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usrtestfindRoleTypes";
        String pfxGrp = worker_id+"grptestfindRoleTypes";
        String pfxRol = worker_id+"roltestfindRoleTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            Group g = null;
            User u = null;
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                u = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(roleType, u, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                startStopwatch();
                identitySession.getRoleManager().findRoleTypes(u, g);
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName() + ".findRoleTypes()");
    }

    public void testfindRoles() throws Exception {
        //logger.info("testfindRoles");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usx1rFngfindRoles";
        String pfxGrp = worker_id+"grx1pFngfindRoles";
        String pfxRol = worker_id+"rox1lFngfindRoles";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "njGROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(roleType, u, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().findUser(pfxUsr + i);
                //Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                startStopwatch();
                identitySession.getRoleManager().findRoles(pfxRol, pfxRol);
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName() + ".findRoles()");
    }

    public void testFindUserRoleTypes() throws Exception {
        //logger.info("testFindUserRoleTypes");

        resetMeasure();

        int n = USER_NUM;
        int step = n / 10;

        String pfxUsr = worker_id+"usrtestFindUserRoleTypes";
        String pfxGrp = worker_id+"grptestFindUserRoleTypes";
        String pfxRol = worker_id+"roltestFindUserRoleTypes";

        String ORGANIZATION = "ORGANIZATION";

        IdentitySession identitySession = identitySessionFactory.createIdentitySession(REALM_NAME);


        identitySession.beginTransaction();

        Group group = identitySession.getPersistenceManager().createGroup(pfxGrp + "GROUP" + n, ORGANIZATION);
        RoleType roleType = identitySession.getRoleManager().createRoleType(pfxRol + "manager");

        identitySession.getTransaction().commit();
        identitySession.close();

        for (int j = 0; j < 10; j++) {
            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().createUser(pfxUsr + i);
                Group g = identitySession.getPersistenceManager().createGroup(pfxGrp + i, ORGANIZATION);
                identitySession.getRoleManager().createRole(roleType, u, g);
                identitySession.getTransaction().commit();
                identitySession.close();
            }

            //
            for (int i = j * step; i < (j * step) + step; i++) {
                identitySession.beginTransaction();
                User u = identitySession.getPersistenceManager().findUser(pfxUsr + i);
                startStopwatch();
                Collection<RoleType> roleTypeCol = identitySession.getRoleManager().findUserRoleTypes(u);
                stopStopwatch();
                storeStopwatch(i);
                identitySession.close();
            }
        }

        generateGraph(getGraphNamePfx()  + worker_id, this.getClass().getName()+ ".testFindUserRoleTypes()");
    }
}
