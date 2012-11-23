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

import org.picketlink.idm.api.CredentialEncoder;
import org.picketlink.idm.common.io.IOTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Implementation based on hashing+salting of passwords. Salt value is computed from username and from content of file, which
 * needs to be provided from parameter {@link #OPTION_CREDENTIAL_ENCODER_FILE_LOCATION}
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FileReadingSaltEncoder extends AbstractHashingWithSaltEncoder
{
   /**
    * Location on classpath or fileSystem
    */
   public static final String OPTION_CREDENTIAL_ENCODER_FILE_LOCATION = CredentialEncoder.CREDENTIAL_ENCODER_OPTION_PREFIX + "fileLocation";

   private String saltPrefix;

   @Override
   protected void afterInitialize()
   {
      super.afterInitialize();
      String fileLocation = getEncoderProperty(OPTION_CREDENTIAL_ENCODER_FILE_LOCATION);

      if (fileLocation == null)
      {
         throw new IllegalStateException("Property " + fileLocation + " needs to be provided");
      }

      // Try to load from classpath first
      InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileLocation);

      // Fallback to File if classpath not available
      if (inputStream == null)
      {
         try
         {
            inputStream = new FileInputStream(new File(fileLocation));
         }
         catch (FileNotFoundException fnfe)
         {
            throw new RuntimeException("File not found in classpath or filesystem. File location: " + fileLocation, fnfe);
         }
      }

      // Read salt from input stream
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
      try
      {
         String line;
         StringBuilder sb = new StringBuilder();
         while ((line = br.readLine()) != null)
         {
            sb.append(line);
         }
         saltPrefix = sb.toString();

         log.info("Salt successfully read from file " + fileLocation);
      }
      catch (IOException io)
      {
         throw new RuntimeException("Error reading salt from file " + fileLocation);
      }
      finally
      {
         IOTools.safeClose(br);
      }
   }

   public String getSalt(String username)
   {
      return saltPrefix + username;
   }
}
