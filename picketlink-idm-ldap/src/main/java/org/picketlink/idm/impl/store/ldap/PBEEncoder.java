/*
 * JBoss, a division of Red Hat
 * Copyright 2012, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketlink.idm.impl.store.ldap;

import org.infinispan.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
class PBEEncoder
{
   /** The secret key that corresponds to the keystore password */
   private final SecretKey cipherKey;

   /** The encode/decode cipher algorithm */
   private final String cipherAlgorithm;

   /** Cipher specification, which specifies info about salt and iterationsCount **/
   private final PBEParameterSpec cipherSpec;

   PBEEncoder(char[] keyStorePassword, String cipherAlgorithm, byte[] salt, int iterationCount)
   {
      this.cipherAlgorithm = cipherAlgorithm;

      try
      {
         this.cipherSpec = new PBEParameterSpec(salt, iterationCount);
         PBEKeySpec keySpec = new PBEKeySpec(keyStorePassword);
         SecretKeyFactory factory = SecretKeyFactory.getInstance(cipherAlgorithm);
         this.cipherKey = factory.generateSecret(keySpec);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public String encode64(String secret) throws Exception
   {
      byte[] secretBytes = secret.getBytes("UTF-8");
      Cipher cipher = Cipher.getInstance(cipherAlgorithm);
      cipher.init(Cipher.ENCRYPT_MODE, cipherKey, cipherSpec);
      byte[] encoding = cipher.doFinal(secretBytes);
      return Base64.encodeBytes(encoding);
   }

   public String decode64(String secret) throws Exception
   {
      byte[] encoding = Base64.decode( secret );
      Cipher cipher = Cipher.getInstance(cipherAlgorithm);
      cipher.init(Cipher.DECRYPT_MODE, cipherKey, cipherSpec);
      byte[] decode = cipher.doFinal(encoding);
      return new String(decode, "UTF-8");
   }
}
