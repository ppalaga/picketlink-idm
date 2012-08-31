/*
 * JBoss, a division of Red Hat
 * Copyright 2012, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
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

package org.picketlink.idm.impl.cache;

import junit.framework.TestCase;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentityType;
import org.picketlink.idm.api.SortOrder;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.api.User;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;
import org.picketlink.idm.impl.api.model.SimpleGroup;
import org.picketlink.idm.impl.api.model.SimpleRoleType;
import org.picketlink.idm.impl.api.model.SimpleUser;

import java.util.ArrayList;

/**
 * Simple test for contract of "equals" and "hashCode" of search objects
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SearchEqualityTestCase extends TestCase
{

   public void testRoleTypeSearchEquality() throws UnsupportedCriterium
   {
      User user1 = new SimpleUser("john");
      Group group1 = new SimpleGroup("platform", "simpleType");
      IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl().page(0,2).sort(SortOrder.ASCENDING);

      RoleTypeSearchImpl rts1 = new RoleTypeSearchImpl();
      rts1.setUser(user1);
      rts1.setGroup(group1);
      rts1.setSearchCriteria(criteria);

      RoleTypeSearchImpl rts2 = new RoleTypeSearchImpl();
      rts2.setUser(user1);
      rts2.setGroup(group1);

      RoleTypeSearchImpl rts3 = new RoleTypeSearchImpl();
      rts3.setUser(user1);
      rts3.setGroup(group1);

      assertFalse(rts1.equals(rts2));
      assertFalse(rts1.hashCode() == rts2.hashCode());

      assertTrue(rts3.equals(rts2));
      assertTrue(rts3.hashCode() == rts2.hashCode());
   }

   public void testUserSearchEquality() throws UnsupportedCriterium
   {
      IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl().page(0,2).sort(SortOrder.ASCENDING);

      UserSearchImpl us1 = new UserSearchImpl();
      us1.setUserId("someone");
      us1.addRelatedGroupId("somegroup");
      us1.setSearchCriteria(criteria);

      UserSearchImpl us2 = new UserSearchImpl();
      us2.setUserId("someone");
      us2.addRelatedGroupId("somegroup");

      UserSearchImpl us3 = new UserSearchImpl();
      us3.setUserId("someone");
      us3.addRelatedGroupId("somegroup");

      UserSearchImpl us4 = new UserSearchImpl();
      us4.setUserId("someone");
      us4.addRelatedGroupId("somegroup");
      us4.setCascade(false);

      assertFalse(us1.equals(us2));
      assertFalse(us1.hashCode() == us2.hashCode());

      assertTrue(us3.equals(us2));
      assertTrue(us3.hashCode() == us2.hashCode());

      assertFalse(us2.equals(us4));
      assertFalse(us2.hashCode() == us4.hashCode());
   }

   public void testGroupSearchEquality() throws UnsupportedCriterium
   {
      IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl().page(0, 2);

      GroupSearchImpl gs1 = new GroupSearchImpl();
      gs1.addRelatedGroupId("somegroup");
      gs1.setSearchCriteria(criteria);

      GroupSearchImpl gs2 = new GroupSearchImpl();
      gs2.addRelatedGroupId("somegroup");

      GroupSearchImpl gs3 = new GroupSearchImpl();
      gs3.addRelatedGroupId("somegroup");
      gs3.setSearchCriteria(new IdentitySearchCriteriaImpl().page(0,2));

      assertFalse(gs1.equals(gs2));
      assertFalse(gs1.hashCode() == gs2.hashCode());

      assertTrue(gs3.equals(gs1));
      assertTrue(gs3.hashCode() == gs1.hashCode());
   }

   public void testRoleSearchEquality() throws UnsupportedCriterium
   {
      IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl().page(0,2);
      SimpleRoleType rt = new SimpleRoleType("simple");

      RoleSearchImpl rs1 = new RoleSearchImpl();
      rs1.setRoleType(rt);
      rs1.setSearchCriteria(criteria);

      RoleSearchImpl rs2 = new RoleSearchImpl();
      rs2.setRoleType(rt);

      RoleSearchImpl rs3 = new RoleSearchImpl();
      rs3.setRoleType(rt);
      rs3.setSearchCriteria(criteria);

      assertFalse(rs1.equals(rs2));
      assertFalse(rs1.hashCode() == rs2.hashCode());

      assertTrue(rs3.equals(rs1));
      assertTrue(rs3.hashCode() == rs1.hashCode());
   }

   public void testRelationshipSearchEquality() throws UnsupportedCriterium
   {
      IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl().page(0,2);

      RelationshipSearchImpl rs1 = new RelationshipSearchImpl();
      rs1.setMembers(new ArrayList<IdentityType>());
      rs1.setSearchCriteria(criteria);

      RelationshipSearchImpl rs2 = new RelationshipSearchImpl();
      rs2.setMembers(new ArrayList<IdentityType>());

      RelationshipSearchImpl rs3 = new RelationshipSearchImpl();
      rs3.setMembers(new ArrayList<IdentityType>());
      rs3.setSearchCriteria(new IdentitySearchCriteriaImpl().page(0,3));

      assertFalse(rs1.equals(rs2));
      assertFalse(rs1.hashCode() == rs2.hashCode());

      assertFalse(rs3.equals(rs1));
      assertFalse(rs3.hashCode() == rs1.hashCode());

      // Test class equality
      AbstractSearch rs4 = new AbstractSearch()
      {
      };
      rs4.setSearchCriteria(criteria);

      RelationshipSearchImpl rs5 = new RelationshipSearchImpl();
      rs5.setSearchCriteria(criteria);

      assertFalse(rs4.equals(rs5));
      assertFalse(rs4.hashCode() == rs5.hashCode());

   }
}
