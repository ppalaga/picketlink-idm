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

package org.picketlink.idm.api;

import java.util.Map;

/**
 * Provides API for encoding credentials/passwords before saving them to Identity storage. Implementations could provide password encoding
 * based on hashing passwords or hashing+salting
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface CredentialEncoder
{
   public static final String CREDENTIAL_ENCODER_OPTION_PREFIX = "credentialEncoder.";

   public static final String OPTION_CREDENTIAL_ENCODER_CLASS = CREDENTIAL_ENCODER_OPTION_PREFIX + "class";

   public static final String OPTION_CREDENTIAL_ENCODER_REGISTRY_NAME = CREDENTIAL_ENCODER_OPTION_PREFIX + "registryName";

   /**
    * Initialize encoder with properties
    *
    * @param credentialEncoderProps
    */
   public void initialize(Map<String, String> credentialEncoderProps);

   /**
    * Provide identity session object. Some encoder implementations may need it (For example if they need to access
    * Identity storage for obtaining salt for concrete user)
    *
    * @param identitySession
    */
   public void setIdentitySession(IdentitySession identitySession);

   /**
    * Encode credentials. Currently we support only encoding of text (String) credentials
    *
    * @param userName some implementations can use userName for obtain/compute password salt. For other implementations, parameter could be unused.
    * @param credentialToEncode password to encode
    * @return encoded password
    */
   public String encodeCredential(String userName, String credentialToEncode);
}
