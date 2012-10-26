package org.picketlink.idm.tree;

import junit.framework.TestCase;
import org.infinispan.Cache;
import org.infinispan.atomic.AtomicMap;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.tree.Fqn;
import org.picketlink.idm.impl.cache.InfinispanAPICacheProviderImpl;
import org.picketlink.idm.impl.tree.IDMTreeCacheImpl;
import org.picketlink.idm.impl.tree.Node;
import org.picketlink.idm.impl.tree.TreeCache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Test case for {@link IDMTreeCacheImpl} operations and functionality
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class IDMTreeCacheTestCase extends TestCase
{
   public void testBasicOperations() throws IOException
   {
      // Init cache
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-tree.xml", true);
      Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");
      TreeCache cache = new IDMTreeCacheImpl(infinispanCache, true, 120000, 120000);

      // Add some nodes
      Node c = cache.addLeafNode(Fqn.fromString("/a/b/c"));
      Node d = cache.addLeafNode(Fqn.fromString("/a/b/d"));
      Node f = cache.addLeafNode(Fqn.fromString("/a/b/e/f"));
      Node g = cache.addLeafNode(Fqn.fromString("/a/b/e/g"));
      Node i1 = cache.addLeafNode(Fqn.fromString("/a/b/e/ch/i1"));
      Node i2 = cache.addLeafNode(Fqn.fromString("/a/b/e/ch/i2"));

      // Put values to nodes
      c.put("", "C_CONTENT");
      d.put("", "D_CONTENT");
      f.put("", "F_CONTENT");
      g.put("", "G_CONTENT");

      // Some "exists" operations
      assertTrue(cache.exists(Fqn.fromString("/a")));
      assertTrue(cache.exists(Fqn.fromString("/a/b")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/c")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/f")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/g")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/h")));

      // Some "get" operations
      assertEquals("C_CONTENT", cache.getNode(Fqn.fromString("/a/b/c")).get(""));
      assertEquals("D_CONTENT", cache.getNode(Fqn.fromString("/a/b/d")).get(""));
      assertEquals("F_CONTENT", cache.getNode(Fqn.fromString("/a/b/e/f")).get(""));
      assertEquals("G_CONTENT", cache.getNode(Fqn.fromString("/a/b/e/g")).get(""));
      assertNull(cache.getNode(Fqn.fromString("/a/b/e/h")));

      // Try to remove some leaf node
      cache.removeNode("/a/b/e/g");
      assertNull(cache.getNode(Fqn.fromString("/a/b/e/g")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/h")));

      // Try some exists operations, then remove non-child node and then check that children are removed too
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/ch")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/ch/i1")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/ch/i2")));
      cache.removeNode("/a/b/e/ch");
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/ch/i1")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/ch/i2")));

      // This node still exists as removeNode is removing only leaf nodes
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/ch")));

      // Now remove whole "e" subtree and verify that "/a/b/e" is still here but everything under it is removed
      cache.removeNode("/a/b/e");
      assertTrue(cache.exists(Fqn.fromString("/a/b/e")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/ch")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/ch/i1")));

      // Try removeChild now
      Node b = cache.getNode(Fqn.fromString("/a/b"));
      assertTrue(b.removeChild("c"));
      assertFalse(cache.exists(Fqn.fromString("/a/b/c")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/d")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e")));

      // Try removeChildren and verify that all children of node "b" are removed
      b.removeChildren();
      assertTrue(cache.exists(Fqn.fromString("/a/b")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/d")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e")));
   }

   public void testPutUniqueQuery() throws IOException
   {
      // Init cache
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-tree.xml", true);
      Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");
      TreeCache cache = new IDMTreeCacheImpl(infinispanCache, true, 120000, 120000);

      // Try to add something under "query_unique" key
      Node c = cache.addLeafNode(Fqn.fromString("/a/b/c"));
      c.put(InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY, "VALUE1");

      // Verify that value is in the cache and we can obtain it from node.get("query_unique")
      c = cache.getNode(Fqn.fromString("/a/b/c"));
      assertEquals("VALUE1", c.get(InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY));

      // Verify that value is wrapped in collection if obtained through node.get("query")
      Object collection = c.get(InfinispanAPICacheProviderImpl.NODE_QUERY_KEY);
      assertFalse("VALUE1".equals(collection));
      assertTrue(collection instanceof Collection);
      Collection coll = (Collection)collection;
      assertEquals(1, coll.size());
      assertEquals("VALUE1", coll.iterator().next());

      // Now put it under key
      c.put(InfinispanAPICacheProviderImpl.NODE_QUERY_KEY, Arrays.asList("VALUE2"));

      // Verify again that it's wrapped in collection when obtained through node.get("query")
      c = cache.getNode(Fqn.fromString("/a/b/c"));
      collection = c.get(InfinispanAPICacheProviderImpl.NODE_QUERY_KEY);
      assertTrue(collection instanceof Collection);
      coll = (Collection)collection;
      assertEquals(1, coll.size());
      assertEquals("VALUE2", coll.iterator().next());

      // Verify again that it's directly available when obtained through node.get("query_unique")
      assertEquals("VALUE2", c.get(InfinispanAPICacheProviderImpl.NODE_QUERY_UNIQUE_KEY));
   }

   public void testEviction() throws IOException
   {
      // Init cache
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-tree.xml", true);
      Cache<Object, Object> infinispanCache = manager.getCache("xml-configured-cache");
      TreeCache cache = new IDMTreeCacheImpl(infinispanCache, true, 250, 700);

      Node c = cache.addLeafNode(Fqn.fromString("/a/b/c"));
      Node d = cache.addLeafNode(Fqn.fromString("/a/b/d"));
      Node f = cache.addLeafNode(Fqn.fromString("/a/b/e/f"));
      Node g = cache.addLeafNode(Fqn.fromString("/a/b/e/g"));

      // Some "exists" operations
      assertTrue(cache.exists(Fqn.fromString("/a/b/c")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/f")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e/g")));

      // This will wait until eviction is finished, but not cleaner thread
      try { Thread.currentThread().sleep(500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

      // Assert that leaf nodes are not here but "path" node is still here
      assertFalse(cache.exists(Fqn.fromString("/a/b/c")));
      assertTrue(cache.exists(Fqn.fromString("/a/b/e")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/f")));
      assertFalse(cache.exists(Fqn.fromString("/a/b/e/g")));

      // Assert that references to child nodes are still in AtomicMap of "e" node
      Node e = cache.getNode(Fqn.fromString("/a/b/e"));
      Object value = e.get("");
      assertTrue(value instanceof AtomicMap);
      AtomicMap map = (AtomicMap)value;
      assertTrue(map.containsKey("f"));
      assertTrue(map.containsKey("g"));

      // This will wait until cleaner thread is finished. References to deleted child nodes are now removed from AtomicMap
      try { Thread.currentThread().sleep(400); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
      e = cache.getNode(Fqn.fromString("/a/b/e"));
      value = e.get("");
      assertTrue(value instanceof AtomicMap);
      map = (AtomicMap)value;
      assertFalse(map.containsKey("f"));
      assertFalse(map.containsKey("g"));
   }
}
