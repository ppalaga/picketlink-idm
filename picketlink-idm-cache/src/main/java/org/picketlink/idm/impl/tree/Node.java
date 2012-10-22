package org.picketlink.idm.impl.tree;

import java.io.Serializable;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface Node
{
   public void put(String key, Serializable value);

   public Serializable get(String key);

   boolean removeChild(Object childName);
}
