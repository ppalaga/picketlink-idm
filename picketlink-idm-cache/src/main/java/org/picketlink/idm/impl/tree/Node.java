package org.picketlink.idm.impl.tree;

import org.infinispan.tree.Fqn;

import java.io.Serializable;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface Node
{
   public void put(String key, Object value);

   public Object get(String key);

   boolean removeChild(Object childName);

   void removeChildren();

   Fqn getFqn();
}
