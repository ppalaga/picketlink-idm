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

package org.picketlink.idm.impl.cache;

import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.picketlink.idm.common.exception.IdentityException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base subclass with common functionality, which can be shared for {@link org.picketlink.idm.cache.APICacheProvider} and
 * {@link org.picketlink.idm.spi.cache.IdentityStoreCacheProvider}
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class AbstractInfinispanCacheProvider
{
   private Logger log = Logger.getLogger(getClass().getName());

   private TreeCache<Object, Object> cache;

   public static final String CONFIG_FILE_OPTION = "cache.configFile";

   public static final String CONFIG_NAME_OPTION = "cache.configName";

   public static final String CONFIG_CACHE_REGISTRY_OPTION = "cache.cacheRegistryName";

   public static final String NULL_NS_NODE = "PL_COMMON_NS";

   public static final String NODE_COMMON_ROOT = "COMMON_ROOT";

   public void initialize(Map<String, String> properties, Object configurationRegistry)
   {
      Cache<Object, Object> infinispanCache;
      String registryName = properties.get(CONFIG_CACHE_REGISTRY_OPTION);

      // Get cache from registry
      if (registryName != null)
      {
         try
         {
            this.cache = getCacheFromRegistry(configurationRegistry, registryName);
            return;
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Cannot find Infinispan 'Cache' object in configuration registry with provided name: "
                  + registryName, e);
         }
      }
      else
      {
         String configFile = properties.get(CONFIG_FILE_OPTION);
         String configName = properties.get(CONFIG_NAME_OPTION);

         if (configFile == null)
         {
            throw new IllegalArgumentException("Cannot find '" + CONFIG_FILE_OPTION + "' in passed properties. Failed to initialize " +
                  "cache provider.");
         }

         if (configName == null)
         {
            throw new IllegalArgumentException("Cannot find '" + CONFIG_NAME_OPTION + "' in passed properties. Failed to initialize " +
                  "cache provider.");
         }

         try
         {
            EmbeddedCacheManager manager = new DefaultCacheManager(configFile, true);
            infinispanCache = manager.getCache(configName);
         }
         catch (IOException ioe)
         {
            throw new IllegalArgumentException("Failed to initialize cache due to IO error", ioe);
         }
      }

      // Now create tree cache
      this.cache = new TreeCacheFactory().createTreeCache(infinispanCache);

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + cache.getCache().getName());
   }

   public void initialize(InputStream cacheConfigStream, String configName)
   {
      if (cacheConfigStream == null)
      {
         throw new IllegalArgumentException("Infinispan configuration InputStream is null");
      }

      try
      {
         EmbeddedCacheManager manager = new DefaultCacheManager(cacheConfigStream, true);
         Cache<Object, Object> infinispanCache = manager.getCache(configName);

         // Now create tree cache
         this.cache = new TreeCacheFactory().createTreeCache(infinispanCache);
      }
      catch (IOException ioe)
      {
         throw new IllegalArgumentException("Failed to initialize cache due to IO error", ioe);
      }

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + cache.getCache().getName());
   }

   public void initialize(Cache infinispanCache)
   {
      this.cache = new TreeCacheFactory().createTreeCache(infinispanCache);
      ComponentStatus status = infinispanCache.getStatus();

      if (status.startAllowed())
      {
         this.cache.start();
      }

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + cache.getCache().getName());
   }

   public void invalidate(String ns)
   {

      boolean success = getCache().removeNode(getNamespacedFqn(ns));

      if (log.isLoggable(Level.FINER))
      {
         log.finer(this.toString() + "Invalidating namespace:" + ns + "; success=" + success);
      }
   }

   public void invalidateAll()
   {
      boolean success = getCache().removeNode(getRootNode());

      if (log.isLoggable(Level.FINER))
      {
         log.finer(this.toString() + "Invalidating whole cache - success=" + success);
      }
   }


   /**
    * @param commonId parameter is usually realmId in case of APICacheProvider or storeId in case of IdentityStoreCacheProvider
    * @return namespace
    */
   public String getNamespace(String commonId)
   {
      if (commonId == null)
      {
         return NODE_COMMON_ROOT;
      }
      return commonId;
   }

   /**
    *
    * @param commonId parameter is usually realmId in case of APICacheProvider or storeId in case of IdentityStoreCacheProvider
    * @param sessionId session id
    * @return namespace
    */
   public String getNamespace(String commonId, String sessionId)
   {
      if (sessionId == null)
      {
         return getNamespace(commonId);
      }
      return commonId + "/" + sessionId;
   }

   /**
    * Different root node name will be used for API cache and for Store cache
    */
   protected abstract String getRootNode();

   /**
    * Different registry type is used for API cache and for Store cache
    */
   protected abstract TreeCache<Object, Object> getCacheFromRegistry(Object registry, String registryName)  throws IdentityException;

   protected String getNamespacedFqn(String ns)
   {
      String namespace = ns != null ? ns : NULL_NS_NODE;
      return new StringBuilder(getRootNode()).append('/').append(namespace).toString();
   }

   protected Fqn getFqn(String ns, String node, Object o)
   {
      String fqnStr = new StringBuilder(getNamespacedFqn(ns)).append('/').append(node).append('/').append(o).toString();
      return Fqn.fromString(fqnStr);
   }

   protected Fqn getFqn(String ns, String node)
   {
      String fqnStr = new StringBuilder(getNamespacedFqn(ns)).append('/').append(node).toString();
      return Fqn.fromString(fqnStr);
   }

   protected TreeCache getCache()
   {
      return cache;
   }
}
