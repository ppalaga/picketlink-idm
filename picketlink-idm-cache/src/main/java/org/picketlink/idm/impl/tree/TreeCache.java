package org.picketlink.idm.impl.tree;

import org.infinispan.Cache;
import org.infinispan.tree.Fqn;

/**
 * Implementation of tree cache
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface TreeCache
{
   /**
    * Verify if item exists in cache
    *
    * @param f FQN, which acts as a key
    * @return true if item exists in cache
    */
   public boolean exists(Fqn f);

   /**
    * Add leaf node (and alternatively all it's supernodes needed for the path)
    *
    * @param nodeFqn FQN of node to add
    * @return newly created node
    */
   public Node addLeafNode(Fqn nodeFqn);

   /**
    * @param nodeFqn FQN, which acts as a key
    * @return Node object related to cache value under given FQN
    */
   public Node getNode(Fqn nodeFqn);

   /**
    * Remove node from cache and all it's subnodes (In case that node is path node, it's not removed but only all it's children are removed)
    *
    * @param nodeFqn
    * @return true if node was successfully removed
    */
   public boolean removeNode(Fqn nodeFqn);

   /**
    * See {@link #removeNode(Fqn)}
    */
   public boolean removeNode(String fqnString);

   /**
    * @return String with whole cache printed in nice tree
    */
   public String printTree();

   /**
    * @return underlying infinispan cache
    */
   public Cache getCache();
}
