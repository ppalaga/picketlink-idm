/*
* JBoss, a division of Red Hat
* Copyright 2012, Red Hat Middleware, LLC, and individual contributors as indicated
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

package org.picketlink.idm.impl.helper;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ToolsTestCase extends TestCase
{

   public void testDnComparison()
   {
      String root = "uid=root, ou=Organization, o=gatein,dc=example,dc=com ";
      String john = "uid=john, ou=My Big Organization Unit,o=gatein org,dc= example ,dc=com ";
      String mary = "uid=mary,ou=OrganizationUnit,o=gatein,dc=example,dc=com";
      String organization1 = " ou=Organization,o=gatein,  dc=example ,dc=com";
      String organization2 = "ou=My Big Organization Unit,o=gatein org,dc= example ,dc=com";
      String organization3 = "uid=mary,ou=OrganizationUnit,o=gatein,dc=example,dc=com";

      String escapeCharsDn = "cn= some\\,\\,thin\\=g , ou= pl\\ at\\.form ,o=gr\\=oup\\=,o=gatein ";

      assertEquals("uid=root,ou=organization,o=gatein,dc=example,dc=com", Tools.dnFormatWhitespaces(root));
      assertEquals("uid=john,ou=my big organization unit,o=gatein org,dc=example,dc=com", Tools.dnFormatWhitespaces(john));
      assertEquals("uid=mary,ou=organizationunit,o=gatein,dc=example,dc=com", Tools.dnFormatWhitespaces(mary));
      assertTrue(Tools.dnEndsWith(root, organization1));
      assertTrue(Tools.dnEndsWith(john, organization2));
      assertTrue(Tools.dnEndsWith(mary, organization3));
      assertFalse(Tools.dnEndsWith(root, organization3));

      assertEquals("cn=some\\,\\,thin\\=g,ou=pl\\ at\\.form,o=gr\\=oup\\=,o=gatein", Tools.dnFormatWhitespaces(escapeCharsDn));
   }

   public void testConvertToInt()
   {
      Integer i1 = 1456;
      Long l1 = 14567897l;

      assertTrue(Tools.convertToInt(i1) == 1456);
      assertTrue(Tools.convertToInt(l1) == 14567897);
   }

}
