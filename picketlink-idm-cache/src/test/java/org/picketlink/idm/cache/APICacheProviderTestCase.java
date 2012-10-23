/*
* JBoss, a division of Red Hat
* Copyright 2009, Red Hat Middleware, LLC, and individual contributors as indicated
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

package org.picketlink.idm.cache;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.cfg.IdentityConfigurationRegistry;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.model.SimpleGroup;
import org.picketlink.idm.impl.api.model.SimpleUser;
import org.picketlink.idm.api.User;
import org.picketlink.idm.impl.cache.AbstractInfinispanCacheProvider;
import org.picketlink.idm.impl.cache.InfinispanAPICacheProviderImpl;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.impl.tree.IDMTreeCacheImpl;
import org.picketlink.idm.impl.tree.TreeCache;


public class APICacheProviderTestCase extends TestCase
{

   public void testUsers() throws Exception
   {
      Map<String, String> props = new HashMap<String, String>();
      props.put(AbstractInfinispanCacheProvider.CONFIG_FILE_OPTION, "infinispan.xml");
      props.put(AbstractInfinispanCacheProvider.CONFIG_NAME_OPTION, "xml-configured-cache");
      APICacheProvider cache = new InfinispanAPICacheProviderImpl();
      cache.initialize(props, null);

      String ns = "toto";

      User u1 = new SimpleUser("u1");
      User u3 = new SimpleUser("u3");

      assertNull(cache.getUser(ns, "u1"));
      assertNull(cache.getUser(ns, "u2"));
      assertNull(cache.getUser(ns, "u3"));

      cache.putUser(ns, u1);
      cache.putUser(ns, u3);

      assertNotNull(cache.getUser(ns, "u1"));
      assertNull(cache.getUser(ns, "u2"));
      assertNotNull(cache.getUser(ns, "u3"));

      cache.invalidateUsers(ns);

      assertNull(cache.getUser(ns, "u1"));
      assertNull(cache.getUser(ns, "u2"));
      assertNull(cache.getUser(ns, "u3"));

   }

   public void testGroups()
   {
      APICacheProvider cache = initCacheFromRegistry();

      String ns = "toto";

      Group g1 = new SimpleGroup("g1", "t1");
      Group g2 = new SimpleGroup("g2", "t2");

      cache.putGroup(ns, g1);
      cache.putGroup(ns, g2);

      assertNotNull(cache.getGroup(ns, "t1", "g1"));
      assertNotNull(cache.getGroup(ns, "t2", "g2"));
      assertNull(cache.getGroup(ns, "g1", "t1"));
      assertNull(cache.getGroup(ns, "t2", "g1"));

      cache.invalidateGroups(ns);

      assertNull(cache.getGroup(ns, "t1", "g1"));
      assertNull(cache.getGroup(ns, "t2", "g2"));
      assertNull(cache.getGroup(ns, "g1", "t1"));
   }

   public void testEviction()
   {
      APICacheProvider cache = initCacheFromRegistry();

      String ns = "toto";

      Group g1 = new SimpleGroup("g1", "t1");
      Group g2 = new SimpleGroup("g2", "t2");

      long startTime = System.currentTimeMillis();
      System.out.println("start time: " + startTime);
      cache.putGroup(ns, g1);
      cache.putGroup(ns, g2);

      assertNotNull(cache.getGroup(ns, "t1", "g1"));
      assertNotNull(cache.getGroup(ns, "t2", "g2"));

      try { Thread.currentThread().sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

      assertNotNull(cache.getGroup(ns, "t1", "g1"));
      assertNotNull(cache.getGroup(ns, "t2", "g2"));

      try { Thread.currentThread().sleep(500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

      assertNull(cache.getGroup(ns, "t1", "g1"));
      assertNull(cache.getGroup(ns, "t2", "g2"));
   }

   private APICacheProvider initCacheFromRegistry()
   {
      try
      {
         // Init tree cache first
         EmbeddedCacheManager manager = new DefaultCacheManager("infinispan.xml", true);
         Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");
         TreeCache treeCache = new IDMTreeCacheImpl(infinispanCache);

         // Register under key 'apiCacheProvider'
         IdentityConfigurationRegistry registry = new IdentityConfigurationImpl();
         registry.register(treeCache, "apiCacheProvider");
         Map<String, String> props = new HashMap<String, String>();
         props.put(AbstractInfinispanCacheProvider.CONFIG_CACHE_REGISTRY_OPTION, "apiCacheProvider");

         // Initialize
         APICacheProvider cache = new InfinispanAPICacheProviderImpl();
         cache.initialize(props, registry);

         return cache;
      }
      catch (IOException ioe)
      {
         throw new IllegalArgumentException("Failed to initialize cache due to IO error", ioe);
      }
      catch (IdentityException ide)
      {
         throw new IllegalArgumentException("Failed to initialize cache due to IdentityException", ide);
      }
   }

}
