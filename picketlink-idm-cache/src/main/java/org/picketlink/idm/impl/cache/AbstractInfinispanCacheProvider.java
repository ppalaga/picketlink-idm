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
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.tree.IDMTreeCacheImpl;
import org.picketlink.idm.impl.tree.Node;
import org.picketlink.idm.impl.tree.TreeCache;

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

   private TreeCache cache;

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
      this.cache = new IDMTreeCacheImpl(infinispanCache, false, -1, -1);

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + infinispanCache.getName());
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
         this.cache = new IDMTreeCacheImpl(infinispanCache, false, -1, -1);
      }
      catch (IOException ioe)
      {
         throw new IllegalArgumentException("Failed to initialize cache due to IO error", ioe);
      }

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + cache.getCache().getName());
   }

   public void initialize(Cache infinispanCache, boolean attachLifespanToLeafNodes, long leafNodeLifespan, long staleNodesLinksCleanerDelay)
   {
      this.cache = new IDMTreeCacheImpl(infinispanCache, attachLifespanToLeafNodes, leafNodeLifespan, staleNodesLinksCleanerDelay);
      ComponentStatus status = infinispanCache.getStatus();

      if (status.startAllowed())
      {
         this.cache.getCache().start();
      }

      log.info("Infinispan cache for Picketlink IDM created successfuly. cache name: " + cache.getCache().getName());
   }

   public void invalidate(String ns)
   {

      boolean success = getCache().removeNode(getNamespacedFqn(ns).toString());

      if (log.isLoggable(Level.FINER))
      {
         log.finer(this.toString() + "Invalidating namespace:" + ns + "; success=" + success);
      }
   }

   public void invalidateAll()
   {
      boolean success = getCache().removeNode("/" + getRootNode());

      if (log.isLoggable(Level.FINER))
      {
         log.finer(this.toString() + "Invalidating whole cache - success=" + success);
      }
   }

   public String printContent()
   {
      return getCache().printTree();
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
   protected abstract TreeCache getCacheFromRegistry(Object registry, String registryName)  throws IdentityException;

   // Fqn.fromString is non-effective way of FQN parsing, but it's actually used only from invalidate operations
   protected StringBuilder getNamespacedFqn(String ns)
   {
      String namespace = getNamespaceForFqn(ns);
      return new StringBuilder('/').append(getRootNode()).append('/').append(namespace);
   }

   private String getNamespaceForFqn(String ns)
   {
      if (ns == null)
      {
         return NULL_NS_NODE;
      }
      else
      {
         // Better to check with indexOf first because of performance reasons, as replaceAll is expensive and '/' is used only in unit tests
         if (ns.indexOf('/') != -1)
         {
            ns = ns.replaceAll("/", "_");
         }
      }
      return ns;
   }

   protected Fqn getFqn(String ns, String node, Object o)
   {
      if (o == null)
      {
         o = "null";
      }

      Object[] fqnElements = new Object[] { getRootNode(), getNamespaceForFqn(ns), node, o};
      return Fqn.fromElements(fqnElements);
   }

   protected Fqn getFqn(String ns, String node)
   {
      Object[] fqnElements = new Object[] { getRootNode(), getNamespaceForFqn(ns), node};
      return Fqn.fromElements(fqnElements);
   }

   protected TreeCache getCache()
   {
      return cache;
   }

   /**
    * Add new node to cache
    * @param nodeFqn
    * @return new node
    */
   protected Node addNode(Fqn nodeFqn)
   {
      return getCache().getTransientLeafNode(nodeFqn);
   }

   /**
    * Get node from cache
    * @param nodeFqn
    * @return node from cache if exists, null if doesn't exist
    */
   protected Node getNode(Fqn nodeFqn)
   {
      return getCache().getNode(nodeFqn);
   }

   /**
    * Remove node from cache
    * @param nodeFqn
    */
   protected void removeNode(Fqn nodeFqn)
   {
      getCache().removeNode(nodeFqn);
   }
}
