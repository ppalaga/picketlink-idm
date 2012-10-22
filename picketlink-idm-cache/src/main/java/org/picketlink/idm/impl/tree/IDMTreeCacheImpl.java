package org.picketlink.idm.impl.tree;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.atomic.AtomicMapLookup;
import org.infinispan.config.ConfigurationException;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.NodeKey;
import org.infinispan.util.Immutables;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IDMTreeCacheImpl
{
   private final AdvancedCache<Fqn, Object> cache;

   private static final Log log = LogFactory.getLog(IDMTreeCacheImpl.class);

   private static final Integer PLACEHOLDER = 123456;

   public IDMTreeCacheImpl(Cache<?, ?> cache)
   {
      this(cache.getAdvancedCache());
   }

   public IDMTreeCacheImpl(AdvancedCache<?, ?> cache)
   {
      this.cache = (AdvancedCache<Fqn, Object>)cache;
      if (cache.getConfiguration().isIndexingEnabled())
      {
         throw new ConfigurationException("TreeCache cannot be used with a Cache instance configured to use indexing!");
      }
      createRoot();
   }

   public boolean exists(Fqn f)
   {
      return cache.containsKey(f);
   }

   public Node addLeafNode(Fqn nodeFqn)
   {
      createNodeInCache(nodeFqn, true);
      return new IDMNodeImpl(nodeFqn, cache, this);
   }

   public Node getNode(Fqn nodeFqn)
   {
      if (exists(nodeFqn))
      {
         return new IDMNodeImpl(nodeFqn, cache, this);
      }
      else
      {
         return null;
      }
   }

   public boolean removeNode(Fqn nodeFqn)
   {
      if (nodeFqn.isRoot())
      {
         return false;
      }

      Node parentNode = getNode(nodeFqn.getParent());
      return (parentNode != null && parentNode.removeChild(nodeFqn.getLastElement()));
   }

   private void createRoot()
   {
      if (!exists(Fqn.ROOT))
      {
         createNodeInCache(Fqn.ROOT, false);
      }
   }

   private boolean createNodeInCache(Fqn fqn, boolean isLeafNode)
   {
      if (cache.containsKey(fqn))
      {
         return false;
      }

      Fqn parent = fqn.getParent();
      if (!fqn.isRoot())
      {
         if (!exists(parent))
         {
            createNodeInCache(parent, false);
         }
         AtomicMap<Object, Fqn> parentStructure = getStructure(parent);
         parentStructure.put(fqn.getLastElement(), fqn);
      }

      if (isLeafNode)
      {
         // TODO: is this needed?
         cache.put(fqn, PLACEHOLDER);
      }
      else
      {
         getStructure(fqn);
      }

      if (log.isTraceEnabled())
      {
         log.tracef("Created node %s", fqn);
      }

      return true;
   }

   AtomicMap<Object, Fqn> getStructure(Fqn fqn)
   {
      return AtomicMapLookup.getAtomicMap(cache, fqn);
   }

}
