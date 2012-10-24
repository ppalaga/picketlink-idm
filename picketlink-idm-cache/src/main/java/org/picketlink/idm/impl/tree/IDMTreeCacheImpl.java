package org.picketlink.idm.impl.tree;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.atomic.AtomicMapLookup;
import org.infinispan.batch.AutoBatchSupport;
import org.infinispan.config.ConfigurationException;
import org.infinispan.executors.DefaultScheduledExecutorFactory;
import org.infinispan.tree.Fqn;
import org.infinispan.util.Immutables;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IDMTreeCacheImpl extends AutoBatchSupport implements TreeCache
{
   private final AdvancedCache<Fqn, Object> cache;

   private static final Log log = LogFactory.getLog(IDMTreeCacheImpl.class);

   private static final Integer PLACEHOLDER = 123456;

   private final boolean attachLifespanToLeafNodes;
   private final long leafNodeLifespan;

   public IDMTreeCacheImpl(Cache<?, ?> cache, boolean attachLifespanToLeafNodes, long leafNodeLifespan, long staleNodesLinksCleanerDelay)
   {
      this(cache.getAdvancedCache(), attachLifespanToLeafNodes, leafNodeLifespan, staleNodesLinksCleanerDelay);
   }

   private IDMTreeCacheImpl(AdvancedCache<?, ?> cache, boolean attachLifespanToLeafNodes, long leafNodeLifespan, long staleNodesLinksCleanerDelay)
   {
      this.cache = (AdvancedCache<Fqn, Object>)cache;
      this.batchContainer = cache.getBatchContainer();
      if (cache.getConfiguration().isIndexingEnabled())
      {
         throw new ConfigurationException("TreeCache cannot be used with a Cache instance configured to use indexing!");
      }

      this.attachLifespanToLeafNodes = attachLifespanToLeafNodes;
      this.leafNodeLifespan = leafNodeLifespan;

      createRoot();

      if (staleNodesLinksCleanerDelay > 0)
      {
         startStaleNodesLinkCleaner(staleNodesLinksCleanerDelay);
      }
   }

   public boolean exists(Fqn f)
   {
      return cache.containsKey(f);
   }

   public Node addLeafNode(Fqn nodeFqn)
   {
      startAtomic();
      try
      {
         createNodeInCache(nodeFqn, true);
         return new IDMNodeImpl(nodeFqn, cache, this, null);
      }
      finally
      {
         endAtomic();
      }
   }

   public Node getNode(Fqn nodeFqn)
   {
      Object value = cache.get(nodeFqn);
      if (value != null)
      {
         return new IDMNodeImpl(nodeFqn, cache, this, value);
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

      startAtomic();
      try
      {
         Object cacheObject = cache.get(nodeFqn);
         if (cacheObject != null && cacheObject instanceof AtomicMap)
         {
            // Don't remove node itself, but remove only it's child nodes
            Node myNode = getNode(nodeFqn);
            myNode.removeChildren();
            return true;
         }
         else
         {
            Node parentNode = getNode(nodeFqn.getParent());
            return (parentNode != null && parentNode.removeChild(nodeFqn.getLastElement()));
         }
      }
      finally
      {
         endAtomic();
      }
   }

   public boolean removeNode(String fqnString)
   {
      return removeNode(Fqn.fromString(fqnString));
   }

   public Cache getCache()
   {
      return cache;
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
         putValueToCacheLeafNode(fqn, PLACEHOLDER);
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

   void putValueToCacheLeafNode(Fqn key, Object value)
   {
      if (attachLifespanToLeafNodes)
      {
         cache.put(key, value, leafNodeLifespan, TimeUnit.MILLISECONDS);
         if (log.isTraceEnabled())
         {
            log.tracef("Added record %s with leafNodeLifespan " + leafNodeLifespan + "ms", key);
         }
      }
      else
      {
         cache.put(key, value);
         if (log.isTraceEnabled())
         {
            log.tracef("Added record %s with infinite leafNodeLifespan", key);
         }
      }
   }

   /**
    * Visual representation of a tree
    *
    * @return String rep
    */
   public String printTree()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("\n\n");

      // walk tree
      sb.append("+ ").append(Fqn.SEPARATOR);
      Object rootNodeContent = cache.get(Fqn.ROOT);

      if (rootNodeContent == null)
      {
         sb.append("NULL_CONTENT\n\n");
         return sb.toString();
      }

      if (rootNodeContent instanceof AtomicMap)
      {
         sb.append("  NO_DATA");
      }
      else
      {
         sb.append("  ").append(rootNodeContent);
      }
      sb.append("\n");
      printChildren(getNode(Fqn.ROOT), 1, sb);
      return sb.toString();
   }

   private void printChildren(Node node, int depth, StringBuilder sb)
   {
      AtomicMap<Object, Fqn> structure = getStructure(node.getFqn());

      for (Fqn childFqn : structure.values())
      {
         for (int i = 0; i < depth; i++) sb.append("  "); // indentations
         sb.append("+ ");
         sb.append(childFqn.getLastElementAsString()).append(Fqn.SEPARATOR);

         Object cacheValue = cache.get(childFqn);
         if (cacheValue instanceof AtomicMap)
         {
            sb.append("  NO_DATA\n");
            Node n = new IDMNodeImpl(childFqn, cache, this, cacheValue);
            printChildren(n, depth + 1, sb);
         }
         else
         {
            sb.append("  ").append(cache.get(childFqn));
         }
         sb.append("\n");
      }
   }

   private void startStaleNodesLinkCleaner(long staleNodesLinksCleanerDelay)
   {
      Properties props = new Properties();
      props.put("threadNamePrefix", "StaleNodesLinksCleaner");
      ScheduledExecutorService executorService = new DefaultScheduledExecutorFactory().getScheduledExecutor(props);
      executorService.scheduleWithFixedDelay(new StaleNodesLinksCleaner(), staleNodesLinksCleanerDelay, staleNodesLinksCleanerDelay, TimeUnit.MILLISECONDS);
      log.info("StaleNodesCleaner started successfully with delay " + staleNodesLinksCleanerDelay);
   }

   private class StaleNodesLinksCleaner implements Runnable
   {
      public void run()
      {
         Node root = getNode(Fqn.ROOT);
         if (root != null)
         {
            log.debug("Going to process root node in StaleNodesLinksCleaner");
            processNode(root);
         }
         else
         {
            // Clear whole cache if root is missing
            log.debug("Root is missing. Going to clear whole cache in StaleNodesLinksCleaner");
            cache.clear();

         }
      }

      private void processNode(Node node)
      {
         if (node.get("") instanceof AtomicMap)
         {
            Fqn nodeFqn = node.getFqn();
            // Do we really need to refresh the state like this?
            AtomicMap<Object, Fqn> structure = getStructure(node.getFqn());
            for (Object key : Immutables.immutableSetCopy(structure.keySet()))
            {
               Fqn childFqn = structure.get(key);
               Object cacheValue = cache.get(childFqn);
               if (cacheValue == null)
               {
                  // If child node doesn't exist, we need to remove it from our own structure
                  if (log.isTraceEnabled())
                  {
                     log.tracef("Removing node link %s from parent structure", childFqn);
                  }
                  structure.remove(key);
               }
               else if (cacheValue instanceof AtomicMap)
               {
                  Node child = new IDMNodeImpl(childFqn, cache, IDMTreeCacheImpl.this, cacheValue);
                  processNode(child);
               }
            }
         }
      }
   }

}
