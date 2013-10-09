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

package org.picketlink.idm.impl.store.hibernate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import junit.framework.Assert;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.SecureRandomProvider;
import org.picketlink.idm.api.User;
import org.picketlink.idm.api.cfg.IdentityConfiguration;
import org.picketlink.idm.api.cfg.IdentityConfigurationRegistry;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.impl.credential.DatabaseReadingSaltEncoder;
import org.picketlink.idm.test.support.hibernate.HibernateTestPOJO;

/**
 * Test for hashing+salting of passwords based on database salts
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class SaltCredentialsEncoderTestCase extends HibernateTestPOJO
{

   IdentitySessionFactory identitySessionFactory;

   public void setUp() throws Exception
   {
      super.start();
      setIdentityConfig("salt-test-config.xml");
      setRealmName("realm::SaltTestRealm");
   }

   public void tearDown() throws Exception
   {
      super.stop();
   }

   /**
    * Unit test for {@link DatabaseReadingSaltEncoder} without usage of SecureRandomProvider
    */
   public void testDatabaseSaltEncoder() throws Exception
   {
       identitySessionFactory = new IdentityConfigurationImpl().
               configure(getIdentityConfig()).buildIdentitySessionFactory();

       _testImpl();
   }

   /**
    * Unit test for {@link DatabaseReadingSaltEncoder} without usage of SecureRandomProvider
    */
   public void testDatabaseSaltEncoderWithRegisteredSecureRandom() throws Exception
   {
      // Add SecureRandomProvider into IdentityRegistry
      IdentityConfiguration identityConfiguration = new IdentityConfigurationImpl().configure(getIdentityConfig());
      IdentityConfigurationRegistry registry = identityConfiguration.getIdentityConfigurationRegistry();
      registry.register(new TestSecureRandomProvider(), DatabaseReadingSaltEncoder.DEFAULT_SECURE_RANDOM_PROVIDER_REGISTRY_NAME);

      this.identitySessionFactory = identityConfiguration.buildIdentitySessionFactory();

      _testImpl();
   }

   private void _testImpl() throws Exception
   {
      begin();

      IdentitySession session = identitySessionFactory.createIdentitySession(getRealmName());

      // Add some users
      User theduke = session.getPersistenceManager().createUser("theduke");
      User demo = session.getPersistenceManager().createUser("demo");

      // Create some non-random salt for john
      session.getAttributesManager().addAttribute(theduke, DatabaseReadingSaltEncoder.PASSWORD_SALT_USER_ATTRIBUTE, "aaabbbcccdddtheduke");

      // Assert password update and validation
      session.getAttributesManager().updatePassword(theduke, "gtn");
      Assert.assertTrue(session.getAttributesManager().validatePassword(theduke, "gtn"));

      // Assert encoded password. We can compute based on previously set salt
      Assert.assertEquals("57eb0c5a143899270f49bf82fd6db49809b765c138b6752a3bfc4862cdc4d73d", ((IdentitySessionImpl) session).getCredentialEncoder().encodeCredential(theduke.getKey(), "gtn"));

      // Assert random generation of salt for user demo
      Assert.assertNull(session.getAttributesManager().getAttribute(demo, DatabaseReadingSaltEncoder.PASSWORD_SALT_USER_ATTRIBUTE));
      session.getAttributesManager().updatePassword(demo, "password123");
      Assert.assertNotNull(session.getAttributesManager().getAttribute(demo, DatabaseReadingSaltEncoder.PASSWORD_SALT_USER_ATTRIBUTE));

      // Some checks for user demo
      Assert.assertFalse(session.getAttributesManager().validatePassword(demo, "gtn"));
      Assert.assertFalse(session.getAttributesManager().validatePassword(demo, "password321"));
      Assert.assertTrue(session.getAttributesManager().validatePassword(demo, "password123"));

      commit();
   }

   private class TestSecureRandomProvider implements SecureRandomProvider
   {

      public SecureRandom getSecureRandom()
      {
         try
         {
            return SecureRandom.getInstance("SHA1PRNG");
         }
         catch (NoSuchAlgorithmException nsae)
         {
            throw new RuntimeException(nsae);
         }
      }
   }
}
