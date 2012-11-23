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

package org.picketlink.idm.impl.credential;

import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.CredentialEncoder;
import java.security.SecureRandom;

/**
 * Implementation based on hashing+salting of passwords. Salt value is saved in database in attribute "passwordSalt" of
 * concrete user and it's randomly generated when used first time for the user
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DatabaseReadingSaltEncoder extends AbstractHashingWithSaltEncoder
{
   public static final String PASSWORD_SALT_USER_ATTRIBUTE = "passwordSalt";

   private static final String OPTION_CREDENTIAL_ENCODER_SECURE_RANDOM_ALGORITHM = CredentialEncoder.CREDENTIAL_ENCODER_OPTION_PREFIX + "secureRandomAlgorithm";

   private static final String OPTION_DEFAULT_SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

   private String secureRandomAlgorithm;

   @Override
   protected void afterInitialize()
   {
      super.afterInitialize();
      secureRandomAlgorithm = getEncoderProperty(OPTION_CREDENTIAL_ENCODER_SECURE_RANDOM_ALGORITHM);
      if (secureRandomAlgorithm == null)
      {
         secureRandomAlgorithm = OPTION_DEFAULT_SECURE_RANDOM_ALGORITHM;
      }

      log.info("Algorithm " + secureRandomAlgorithm + " will be used for random generating of password salts");
   }

   @Override
   protected String getSalt(String username)
   {
      try
      {
         AttributesManager am = getIdentitySession().getAttributesManager();
         Attribute salt = am.getAttribute(username, PASSWORD_SALT_USER_ATTRIBUTE);

         // User does not have salt attribute in DB. Let's generate a fresh one and save it to DB.
         if (salt == null)
         {
            SecureRandom pseudoRng = SecureRandom.getInstance(secureRandomAlgorithm);
            String saltStr = String.valueOf(pseudoRng.nextLong());
            am.addAttribute(username, PASSWORD_SALT_USER_ATTRIBUTE, saltStr);

            log.fine("Salt has been randomly generated for user " + username);

            return saltStr;
         }
         else
         {
            return (String)salt.getValue();
         }
      }
      catch (Exception ie)
      {
         throw new RuntimeException(ie);
      }
   }
}
