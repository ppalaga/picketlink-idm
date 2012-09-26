package org.picketlink.idm.impl.store.hibernate;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.api.RelationshipManager;
import org.picketlink.idm.api.RoleManager;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.api.User;
import org.picketlink.idm.api.cfg.IdentityConfiguration;
import org.picketlink.idm.cache.APICacheProvider;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;
import org.picketlink.idm.impl.api.SimpleAttribute;
import org.picketlink.idm.impl.api.model.SimpleGroup;
import org.picketlink.idm.impl.api.model.SimpleRoleType;
import org.picketlink.idm.impl.api.model.SimpleUser;
import org.picketlink.idm.impl.cache.GroupSearchImpl;
import org.picketlink.idm.impl.cache.InfinispanAPICacheProviderImpl;
import org.picketlink.idm.impl.cache.RelationshipSearchImpl;
import org.picketlink.idm.impl.cache.RoleSearchImpl;
import org.picketlink.idm.impl.cache.RoleTypeSearchImpl;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.test.support.hibernate.HibernateTestPOJO;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Test case for {@link APICacheProvider} operations integrated with real IDM api managers on real HibernateIdentityStore
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class APICacheIntegrationTestCase extends HibernateTestPOJO
{
   private IdentitySession identitySession;
   private APICacheProvider apiCacheProvider;

   // Realm name is used also as cache namespace
   private static final String REALM_NAME = "realm://FlexibleRealm";

   public void setUp() throws Exception
   {
      super.start();

      setIdentityConfig("cache-identity-config.xml");
      setRealmName(REALM_NAME);

      IdentityConfiguration identityConfiguration = new IdentityConfigurationImpl().
            configure(getIdentityConfig());

      // Init infinispan first
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan.xml", true);
      Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");

      // Init and register apiCacheProvider
      InfinispanAPICacheProviderImpl cacheProvider = new InfinispanAPICacheProviderImpl();
      cacheProvider.initialize(infinispanCache);
      this.apiCacheProvider = cacheProvider;
      identityConfiguration.getIdentityConfigurationRegistry().register(apiCacheProvider, "apiCacheProvider");

      // Build identity session
      IdentitySessionFactory identitySessionFactory = identityConfiguration.buildIdentitySessionFactory();
      this.identitySession =  identitySessionFactory.createIdentitySession(REALM_NAME);
   }

   public void tearDown() throws Exception
   {
      super.stop();
   }

   public void testUsers() throws IdentityException, UnsupportedCriterium, FeatureNotSupportedException
   {
      PersistenceManager pm = identitySession.getPersistenceManager();

      // Some user operations
      pm.createUser("john");
      IdentitySearchCriteria searchCriteria = new IdentitySearchCriteriaImpl().nameFilter("john");
      pm.findUser(searchCriteria);
      pm.getUserCount();

      // Test that data are in cache now
      assertNotNull(apiCacheProvider.getUser(REALM_NAME, "john"));
      assertNotNull(apiCacheProvider.getUsers(REALM_NAME, searchCriteria));
      assertEquals(1, apiCacheProvider.getUserCount(REALM_NAME));

      // Creating user will invalidate everything except that user
      pm.createUser("demo");
      assertNotNull(apiCacheProvider.getUser(REALM_NAME, "demo"));
      assertNull(apiCacheProvider.getUser(REALM_NAME, "john"));
      assertNull(apiCacheProvider.getUsers(REALM_NAME, searchCriteria));
      assertEquals(-1, apiCacheProvider.getUserCount(REALM_NAME));

      // Some group operations
      pm.createGroup("mygroup", "mygrouptype");
      pm.findGroup("mygrouptype");
      pm.findGroup("nonExistingGroupType");
      pm.getGroupTypeCount("nonExistingGroupType");

      // Test that data are in cache now
      GroupSearchImpl search = new GroupSearchImpl();
      search.setGroupType("mygrouptype");
      GroupSearchImpl searchNonExisting = new GroupSearchImpl();
      searchNonExisting.setGroupType("nonExistingGroupType");
      assertNotNull(apiCacheProvider.getGroupSearch(REALM_NAME, search));
      assertNotNull(apiCacheProvider.getGroupSearch(REALM_NAME, searchNonExisting));
      assertNotNull(apiCacheProvider.getGroup(REALM_NAME, "mygrouptype", "mygroup"));
      assertEquals(0, apiCacheProvider.getGroupCount(REALM_NAME, "nonExistingGroupType"));
      // Not in cache
      assertEquals(-1, apiCacheProvider.getGroupCount(REALM_NAME, "mygrouptype"));
      // User 'demo' is not in cache as he was invalidated because of group creation
      assertNull(apiCacheProvider.getUser(REALM_NAME, "demo"));

      // Remove group and assert that nothing is in cache now
      pm.removeGroup(new SimpleGroup("mygroup", "mygrouptype"), true);
      assertNull(apiCacheProvider.getGroupSearch(REALM_NAME, searchNonExisting));
      assertNull(apiCacheProvider.getGroup(REALM_NAME, "mygrouptype", "mygroup"));
      assertEquals(-1, apiCacheProvider.getGroupCount(REALM_NAME, "nonExistingGroupType"));


      // Some testing with attributeManager
      AttributesManager am = identitySession.getAttributesManager();
      am.getAttributes("demo");
      am.getAttributes("john");
      assertEquals(0, apiCacheProvider.getAttributes(REALM_NAME, "demo").size());
      assertEquals(0, apiCacheProvider.getAttributes(REALM_NAME, "john").size());
      assertNull(apiCacheProvider.getAttributes(REALM_NAME, "nonExisting"));

      am.addAttribute("john", "surname", "Anthony");
      assertEquals(1, apiCacheProvider.getAttributes(REALM_NAME, "john").size());

      Attribute antoninSurname = new SimpleAttribute("surname", "Antonin");
      am.updateAttributes("john", new Attribute[] { antoninSurname });
      Map<String, Attribute> johnAttrsFromCache = apiCacheProvider.getAttributes(REALM_NAME, "john");
      assertEquals(1,johnAttrsFromCache.size());
      Attribute surnameFromCache = johnAttrsFromCache.get("surname");
      assertNotNull(surnameFromCache);
      assertEquals(antoninSurname.getValue(), surnameFromCache.getValue());

      am.removeAttributes("john", new String[] { "surname" });
      assertEquals(0, apiCacheProvider.getAttributes(REALM_NAME, "john").size());


      // Some testing with relationshipManager
      Group group1 = pm.createGroup("mygroup1", "mygrouptype");
      Group group2 = pm.createGroup("mygroup2", "mygrouptype");
      Group group3 = pm.createGroup("mygroup3", "mygrouptype");
      RelationshipManager relm = identitySession.getRelationshipManager();
      relm.associateGroups(group1, group2);
      assertTrue(relm.isAssociated(group1, group2));
      assertFalse(relm.isAssociated(group2, group3));

      RelationshipSearchImpl search12 = new RelationshipSearchImpl();
      search12.addParent(group1);
      search12.addMember(group2);
      RelationshipSearchImpl search23 = new RelationshipSearchImpl();
      search23.addParent(group2);
      search23.addMember(group3);
      RelationshipSearchImpl search13 = new RelationshipSearchImpl();
      search13.addParent(group1);
      search13.addMember(group3);

      assertTrue(apiCacheProvider.getRelationshipSearch(REALM_NAME, search12));
      assertFalse(apiCacheProvider.getRelationshipSearch(REALM_NAME, search23));
      assertNull(apiCacheProvider.getRelationshipSearch(REALM_NAME, search13));

      IdentitySearchCriteria emptyCriteria = new IdentitySearchCriteriaImpl();
      assertEquals(1, relm.findAssociatedGroups(group1, "mygrouptype", true, true, emptyCriteria).size());

      GroupSearchImpl groupSearch = new GroupSearchImpl();
      groupSearch.addAssociatedGroupId(group1.getKey());
      groupSearch.setGroupType("mygrouptype");
      groupSearch.setParent(true);
      groupSearch.setCascade(true);
      groupSearch.setSearchCriteria(emptyCriteria);
      assertTrue(apiCacheProvider.getGroupSearch(REALM_NAME, groupSearch).contains(group2));

      // All searches should be invalidated after associating user
      User john = new SimpleUser("john");
      relm.associateUser(group1, john);
      assertNull(apiCacheProvider.getRelationshipSearch(REALM_NAME, search12));
      assertNull(apiCacheProvider.getRelationshipSearch(REALM_NAME, search23));
      assertNull(apiCacheProvider.getRelationshipSearch(REALM_NAME, search13));
      assertNull(apiCacheProvider.getGroupSearch(REALM_NAME, groupSearch));

      // Additional testing with user associations
      assertTrue(relm.isAssociated(group1, john));
      RelationshipSearchImpl search1j = new RelationshipSearchImpl();
      search1j.addParent(group1);
      search1j.addMember(john);
      assertTrue(apiCacheProvider.getRelationshipSearch(REALM_NAME, search1j));
      relm.disassociateGroups(group1, Arrays.asList(new Group[] { group2 }));
      assertNull(apiCacheProvider.getRelationshipSearch(REALM_NAME, search1j));


      // Some testing with role manager
      RoleManager rolman = identitySession.getRoleManager();
      RoleType rolType1 = rolman.createRoleType("roleType1");
      RoleType rolType2 = rolman.createRoleType("roleType2");
      assertNotNull(apiCacheProvider.getRoleType(REALM_NAME, rolType2));
      assertNull(apiCacheProvider.getRoleType(REALM_NAME, new SimpleRoleType("roleTypeNonExisting")));

      rolman.createRole(rolType1, john, group1);
      rolman.createRole(rolType2, john, group2);

      rolman.findGroupRoleTypes(group1, emptyCriteria);
      rolman.findUserRoleTypes(john, emptyCriteria);
      rolman.findGroupsWithRelatedRole("john", "mygrouptype", emptyCriteria);
      rolman.findRoles(group1, rolType1);

      RoleTypeSearchImpl rts1 = new RoleTypeSearchImpl();
      rts1.setGroup(group1);
      rts1.setSearchCriteria(emptyCriteria);
      RoleTypeSearchImpl rtsUser = new RoleTypeSearchImpl();
      rtsUser.setUser(john);
      rtsUser.setSearchCriteria(emptyCriteria);
      assertEquals(1, apiCacheProvider.getRoleTypeSearch(REALM_NAME, rts1).size());
      assertEquals(2, apiCacheProvider.getRoleTypeSearch(REALM_NAME, rtsUser).size());

      GroupSearchImpl searchRelatedRole = new GroupSearchImpl();
      searchRelatedRole.addRelatedUserId(john.getKey());
      searchRelatedRole.setGroupType("mygrouptype");
      searchRelatedRole.setSearchCriteria(emptyCriteria);
      Collection<Group> groupsFromRS = apiCacheProvider.getGroupSearch(REALM_NAME, searchRelatedRole);
      assertEquals(2, groupsFromRS.size());
      assertTrue(groupsFromRS.contains(group1));
      assertTrue(groupsFromRS.contains(group2));


      RoleSearchImpl roleSearch = new RoleSearchImpl();
      roleSearch.setIdentityTypeId(group1.getKey());
      roleSearch.setRoleType(rolType1);
      assertEquals(1, apiCacheProvider.getRoleSearch(REALM_NAME, roleSearch).size());

      // Assert everything invalidated after creating role type
      rolman.createRoleType("roleType3");
      assertNull(apiCacheProvider.getRoleTypeSearch(REALM_NAME, rts1));
      assertNull(apiCacheProvider.getRoleTypeSearch(REALM_NAME, rtsUser));
      assertNull(apiCacheProvider.getGroupSearch(REALM_NAME, searchRelatedRole));
      assertNull(apiCacheProvider.getRoleSearch(REALM_NAME, roleSearch));
   }


}
