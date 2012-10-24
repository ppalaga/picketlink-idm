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

package org.picketlink.idm.cache;

import junit.framework.TestCase;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.cache.AbstractInfinispanCacheProvider;
import org.picketlink.idm.impl.cache.InfinispanIdentityStoreCacheProviderImpl;
import org.picketlink.idm.impl.configuration.IdentityConfigurationContextImpl;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.impl.tree.IDMTreeCacheImpl;
import org.picketlink.idm.impl.tree.TreeCache;
import org.picketlink.idm.spi.cache.IdentityStoreCacheProvider;
import org.picketlink.idm.spi.configuration.IdentityConfigurationContextRegistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * test case for {@link IdentityStoreCacheProvider}
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StoreCacheProviderTestCase extends TestCase
{

   public void testIdentityObjectCount()
   {
      IdentityStoreCacheProvider cache = initCacheFromRegistry();

      String ns = "totoo";

      assertEquals(-1, cache.getIdentityObjectCount(ns, "type1"));
      assertEquals(-1, cache.getIdentityObjectCount(ns, "type2"));

      cache.putIdentityObjectCount(ns, "type2", 6);

      assertEquals(-1, cache.getIdentityObjectCount(ns, "type1"));
      assertEquals(6, cache.getIdentityObjectCount(ns, "type2"));

      cache.invalidateIdentityObjectCount(ns, "type2");

      assertEquals(-1, cache.getIdentityObjectCount(ns, "type1"));
      assertEquals(-1, cache.getIdentityObjectCount(ns, "type2"));
   }

   private IdentityStoreCacheProvider initCacheFromRegistry()
   {
      try
      {
         // Init tree cache first
         EmbeddedCacheManager manager = new DefaultCacheManager("infinispan.xml", true);
         Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");
         TreeCache treeCache = new IDMTreeCacheImpl(infinispanCache, false, -1, -1);

         // Register under key 'storeCacheProvider'
         IdentityConfigurationContextRegistry registry = new IdentityConfigurationImpl();
         IdentityConfigurationContextImpl context = new IdentityConfigurationContextImpl(null, registry);
         registry.register(treeCache, "storeCacheProvider");
         Map<String, String> props = new HashMap<String, String>();
         props.put(AbstractInfinispanCacheProvider.CONFIG_CACHE_REGISTRY_OPTION, "storeCacheProvider");

         // Initialize
         IdentityStoreCacheProvider cache = new InfinispanIdentityStoreCacheProviderImpl();
         cache.initialize(props, context);

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
