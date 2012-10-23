package org.picketlink.idm.impl.tree;

import org.infinispan.tree.Fqn;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface TreeCache
{
   public boolean exists(Fqn f);

   public Node addLeafNode(Fqn nodeFqn);

   public Node getNode(Fqn nodeFqn);

   public boolean removeNode(Fqn nodeFqn);

   public String printTree();
}
