package org.picketlink.idm.impl.tree;


import org.infinispan.AdvancedCache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.NodeKey;
import org.infinispan.util.Immutables;
import org.picketlink.idm.impl.cache.InfinispanAPICacheProviderImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IDMNodeImpl implements Node
{
   private final Fqn nodeFqn;
   private final AdvancedCache<Fqn, Object> cache;
   private final IDMTreeCacheImpl treeCache;

   public IDMNodeImpl(Fqn nodeFqn, AdvancedCache<Fqn, Object> cache, IDMTreeCacheImpl treeCache)
   {
      this.nodeFqn = nodeFqn;
      this.cache = cache;
      this.treeCache = treeCache;
   }

   public void put(String key, Serializable value)
   {
      // Workaround to cover unique query case
      if (InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY.equals(key))
      {
         ArrayList<Serializable> list = new ArrayList<Serializable>();
         list.add(value);
         value = list;
      }

      cache.put(nodeFqn, value);
   }

   public Serializable get(String key)
   {
      Serializable result = (Serializable)cache.get(nodeFqn);

      // Workaround to cover unique query case
      if (InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY.equals(key))
      {
         Collection<Serializable> collection = (Collection<Serializable>)result;
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
         Object fromCache = cache.get(childFqn);

         // null checks... TODO: is it needed?
         if (fromCache == null)
         {
            return false;
         }

         // We are trying to remove non-leaf node. So we need to recursively remove children
         if (fromCache instanceof AtomicMap)
         {
            // TODO: investigate if it's really needed to refresh the state this way
            AtomicMap atomicMap = treeCache.getStructure(childFqn);
            Node childNode = new IDMNodeImpl(childFqn, cache, treeCache);
            for (Object o : Immutables.immutableSetCopy(atomicMap.keySet()))
            {
               childNode.removeChild(o);
            }
         }

         // Now real removal
         Object o = cache.remove(childFqn);
         return o!=null;
      }

      return false;

   }
}
