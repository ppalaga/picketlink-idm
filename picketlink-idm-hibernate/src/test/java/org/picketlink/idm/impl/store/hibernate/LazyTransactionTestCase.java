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

import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.repository.RepositoryIdentityStoreSessionImpl;

/**
* Test for configuration with lazyStartOfHibernateTransaction enabled
*
* @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
*/
public class LazyTransactionTestCase extends MinimalFlexibleConfigTestCase
{

   public void setUp() throws Exception
   {
      setIdentityConfigLocation("lazy-tx-identity-config.xml");
      super.setUp();

   }

   public void testLazyTransaction() throws IdentityException
   {
      // Obtain some needed PL objects
      IdentitySessionImpl identitySession = (IdentitySessionImpl)identitySessionFactory.getCurrentIdentitySession("realm://FlexibleRealm");
      RepositoryIdentityStoreSessionImpl repositoryStoreSession = (RepositoryIdentityStoreSessionImpl)identitySession.
            getSessionContext().resolveStoreInvocationContext().getIdentityStoreSession();
      HibernateIdentityStoreSessionImpl hbStoreSession = (HibernateIdentityStoreSessionImpl)repositoryStoreSession.
            getIdentityStoreSession("Hibernate Identity Store");


      // Assert not-active transaction and null status
      assertNull(hbStoreSession.getHibernateTxStatus());
      assertFalse(identitySession.getTransaction().isActive());

      // Lazy start of picketlink transaction. But hibernate transaction is not started yet
      identitySession.beginTransaction();
      assertTrue(identitySession.getTransaction().isActive());
      assertFalse(hbStoreSession.getHibernateTxStatus());

      // Sync with global transaction
      //this.begin();

      // Call some API operation and assert that Hibernate transaction is started
      identitySession.getPersistenceManager().findUser("someone");
      assertTrue(hbStoreSession.getHibernateTxStatus());

      // Commit Hibernate transaction and assert that ThreadLocal is cleaned and picketlink transaction is not active
      identitySession.getTransaction().commit();
      assertNull(hbStoreSession.getHibernateTxStatus());
      assertFalse(identitySession.getTransaction().isActive());

      // Same workflow but now with rollback
      identitySession.beginTransaction();
      assertTrue(identitySession.getTransaction().isActive());
      assertFalse(hbStoreSession.getHibernateTxStatus());
      identitySession.getPersistenceManager().findGroup("someGroup");
      assertTrue(hbStoreSession.getHibernateTxStatus());
      identitySession.getTransaction().rollback();
      assertNull(hbStoreSession.getHibernateTxStatus());
      assertFalse(identitySession.getTransaction().isActive());

      // Sync with global transaction
      this.getHibernateSupport().rollbackTransaction();
   }

   @Override
   public void begin()
   {
      //
   }

   @Override
   public void commit()
   {
      //
   }
}
