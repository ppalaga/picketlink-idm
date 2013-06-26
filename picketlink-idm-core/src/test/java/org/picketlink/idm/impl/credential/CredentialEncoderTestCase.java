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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picketlink.idm.api.CredentialEncoder;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Test for encoding of passwords based on {@link CredentialEncoder} implementations
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CredentialEncoderTestCase extends TestCase
{
   /**
    * Unit test for {@link HashingEncoder}
    */
   public void testHashingOnlyEncoder()
   {
      CredentialEncoder encoder = new HashingEncoder();
      String userName = "theduke";

      // Let's try MD5 encoder
      HashMap<String, String> parameters = new HashMap<String, String>();
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "MD5");
      encoder.initialize(parameters, null);
      Assert.assertEquals("b9cd9b73428cda4a83651ad1658b439c", encoder.encodeCredential(userName, "gtn"));
      Assert.assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", encoder.encodeCredential(userName, "password"));
      Assert.assertEquals("7c6a180b36896a0a8c02787eeafb0e4c", encoder.encodeCredential(userName, "password1"));
      Assert.assertEquals("95a5b49a1f092b442ff63a837b548431", encoder.encodeCredential(userName, "theduke"));
      Assert.assertEquals("b9cd9b73428cda4a83651ad1658b439c", encoder.encodeCredential("someOtherDuke", "gtn"));

      // Let's try SHA1 encoder
      parameters.clear();
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA1");
      encoder.initialize(parameters, null);
      Assert.assertEquals("04c501fa7469d1aaad50ba59fa9672629601c125", encoder.encodeCredential(userName, "gtn"));

      // Let's try SHA-256 encoder
      parameters.clear();
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-256");
      encoder.initialize(parameters, null);
      Assert.assertEquals("30fba9ec34d4372a85d4fa253d4d7c02f8d96de5e90a01b8f6686f6a448207da", encoder.encodeCredential(userName, "gtn"));

      // MD5 is default encoder if not provided via configuration
      parameters.clear();
      encoder.initialize(parameters, null);
      Assert.assertEquals("b9cd9b73428cda4a83651ad1658b439c", encoder.encodeCredential(userName, "gtn"));

      // Try some non-existing encoder
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-64879");
      encoder.initialize(parameters, null);
      try
      {
         encoder.encodeCredential(userName, "gtn");
         fail("Exception should be thrown but it's not");
      }
      catch (RuntimeException re)
      {
         Assert.assertNotNull(re.getCause());
         Assert.assertEquals(re.getCause().getClass(), NoSuchAlgorithmException.class);
      }
      catch (Exception e)
      {
         fail("Not-expected exception " + e);
      }
   }

   /**
    * Unit test for {@link FileReadingSaltEncoder}
    */
   public void testFileReadingSaltEncoder()
   {
      CredentialEncoder encoder = new FileReadingSaltEncoder();
      String userName = "theduke";

      // Let's try SHA-256 encoder with salt from salt1.txt
      HashMap<String, String> parameters = new HashMap<String, String>();
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-256");
      parameters.put(FileReadingSaltEncoder.OPTION_CREDENTIAL_ENCODER_FILE_LOCATION, "salt/salt1.txt");
      encoder.initialize(parameters, null);
      Assert.assertEquals("57eb0c5a143899270f49bf82fd6db49809b765c138b6752a3bfc4862cdc4d73d", encoder.encodeCredential(userName, "gtn"));
      Assert.assertEquals("7642c2b34bcafafedecaaa9bce1812a6b76f902ebf0265313e7623edde80905f", encoder.encodeCredential(userName, "password"));
      Assert.assertEquals("d01c61872852742d4a83b75cf243e8a50220e006567babc60a075757decfcb00", encoder.encodeCredential("someOtherDuke", "gtn"));
      Assert.assertEquals("57eb0c5a143899270f49bf82fd6db49809b765c138b6752a3bfc4862cdc4d73d", encoder.encodeCredential(userName, "gtn"));

      // Using salt2.txt
      parameters.clear();
      parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-256");
      parameters.put(FileReadingSaltEncoder.OPTION_CREDENTIAL_ENCODER_FILE_LOCATION, "salt/salt2.txt");
      encoder.initialize(parameters, null);
      Assert.assertEquals("01ada1f0772bd7665875160e62ebf9679c9404e225b709eaede532b3210f9c48", encoder.encodeCredential(userName, "gtn"));
      Assert.assertEquals("523d43e1f4e697766ca5d230eb2a7d7bcd2fb2399c0d49965a532885dfc10ca1", encoder.encodeCredential(userName, "password"));

      // Assert failing without specified fileLocation
      try
      {
         parameters.clear();
         parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-256");
         encoder.initialize(parameters, null);
         Assert.fail("Test should fail during initialization because non-existing location");
      }
      catch (IllegalStateException re)
      {
      }
      catch (Exception e)
      {
         Assert.fail("Not-expected exception " + e);
      }

      // Assert failing with non-existing fileLocation
      try
      {
         parameters.clear();
         parameters.put(HashingEncoder.OPTION_CREDENTIAL_ENCODER_HASH_ALGORITHM, "SHA-256");
         parameters.put(FileReadingSaltEncoder.OPTION_CREDENTIAL_ENCODER_FILE_LOCATION, "/opt/nonexistingg/salt2.txt");
         encoder.initialize(parameters, null);
         Assert.fail("Test should fail during initialization because non-existing location");
      }
      catch (RuntimeException re)
      {
         Assert.assertNotNull(re.getCause());
         Assert.assertEquals(FileNotFoundException.class, re.getCause().getClass());
      }
      catch (Exception e)
      {
         Assert.fail("Not-expected exception " + e);
      }
   }

}
