package org.picketlink.idm.impl.tree;


import org.infinispan.AdvancedCache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.tree.Fqn;
import org.infinispan.util.Immutables;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;
import org.picketlink.idm.impl.cache.InfinispanAPICacheProviderImpl;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IDMNodeImpl implements Node
{
   private static final Log log = LogFactory.getLog(IDMNodeImpl.class);

   private final Fqn nodeFqn;
   private final AdvancedCache<Fqn, Object> cache;
   private final IDMTreeCacheImpl treeCache;

   private final Object value;

   public IDMNodeImpl(Fqn nodeFqn, AdvancedCache<Fqn, Object> cache, IDMTreeCacheImpl treeCache, Object value)
   {
      this.nodeFqn = nodeFqn;
      this.cache = cache;
      this.treeCache = treeCache;
      this.value = value;
   }

   public void put(String key, Object value)
   {
      // Workaround to cover unique query case
      if (InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY.equals(key))
      {
         ArrayList<Object> list = new ArrayList<Object>();
         list.add(value);
         value = list;
      }

      treeCache.putValueToCacheLeafNode(nodeFqn, value);
   }

   public Object get(String key)
   {
      Object result;
      if (value == null)
      {
         result = cache.get(nodeFqn);
      }
      else
      {
         result = value;
      }

      // Workaround to cover unique query case
      if (InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY.equals(key))
      {
         Collection<Object> collection = (Collection<Object>)result;
         return collection.iterator().next();
      }
      else
      {
         return result;
      }
   }

   public boolean removeChild(Object childName)
   {
      AtomicMap<Object, Fqn> structure = treeCache.getStructure(nodeFqn);
      Fqn childFqn = structure.remove(childName);

      if (childFqn != null)
      {
         Object child = cache.get(childFqn);

         // null checks... TODO: is it needed?
         if (child == null)
         {
            return false;
         }

         // We are trying to remove non-leaf node. So we need to recursively remove children
         if (child instanceof AtomicMap)
         {
            Node childNode = new IDMNodeImpl(childFqn, cache, treeCache, child);
            childNode.removeChildren();

         }

         // Now real removal
         Object o = cache.remove(childFqn);
         if (log.isTraceEnabled())
         {
            log.tracef("Removed node %s", childFqn);
         }
         return o!=null;
      }

      return false;

   }

   public void removeChildren()
   {
      AtomicMap atomicMap = treeCache.getStructure(nodeFqn);
      for (Object o : Immutables.immutableSetCopy(atomicMap.keySet()))
      {
         removeChild(o);
      }
   }

   public Fqn getFqn()
   {
      return nodeFqn;
   }
}
