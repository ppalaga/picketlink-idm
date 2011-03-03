package org.picketlink.idm.impl.model.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.picketlink.idm.impl.store.jpa.IdentityProperty;
import org.picketlink.idm.impl.store.jpa.PropertyType;

/**
 * A lookup table containing identity object types
 *  
 * @author Shane Bryzak
 */
@Entity
public class IdentityObjectType implements Serializable
{
   private static final long serialVersionUID = -8333008038699510742L;
   
   private Long id;
   private String name;
   
   @Id @GeneratedValue
   public Long getId()
   {
      return id;
   }
   
   public void setId(Long id)
   {
      this.id = id;
   }
   
   @IdentityProperty(PropertyType.NAME)
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
}