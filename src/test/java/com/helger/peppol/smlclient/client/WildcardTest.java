/**
 * Copyright (C) 2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package com.helger.peppol.smlclient.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.helger.peppol.identifier.CIdentifier;
import com.helger.peppol.identifier.ParticipantIdentifierType;
import com.helger.peppol.identifier.participant.SimpleParticipantIdentifier;
import com.helger.peppol.smlclient.AbstractSMLClientTest;
import com.helger.peppol.smlclient.ManageParticipantIdentifierServiceCaller;
import com.helger.peppol.smlclient.ManageServiceMetadataServiceCaller;
import com.helger.peppol.smlclient.participant.BadRequestFault;
import com.helger.peppol.smlclient.participant.UnauthorizedFault;
import com.helger.peppol.smlclient.smp.NotFoundFault;
import com.helger.peppol.smlclient.smp.PublisherEndpointType;
import com.helger.peppol.smlclient.smp.ServiceMetadataPublisherServiceType;

/**
 * This class is used for generating test data.
 *
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class WildcardTest extends AbstractSMLClientTest
{
  private static final String BUSINESS_IDENTIFIER_SCHEME = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String WILDCARD_PI = "0088:1111100001111";
  private static final String WILDCARD_ACTORID_ALLOWED_SCHEME = "wildcard-actorid-allowed";

  /*
   * Wildcard user.
   */
  private static final String SMP_ID = "wildcard-user1";
  private static final String ADDRESS_LOGICAL = "http://doesnotexist.xx";
  private static final String ADDRESS_PHYSICAL = "198.18.0.0";

  private static final String UNAUTHRIZED_SML_ID = "SMP-ID2";

  private ManageServiceMetadataServiceCaller m_aSMClient;

  @Before
  public void initialize () throws Exception
  {
    m_aSMClient = new ManageServiceMetadataServiceCaller (SML_INFO);
    m_aSMClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
    try
    {
      m_aSMClient.delete (SMP_ID);
    }
    catch (final NotFoundFault e)
    {
      // This is fine, since we are just cleaning
    }

    // Create SMP
    final ServiceMetadataPublisherServiceType aServiceMetadataCreate = new ServiceMetadataPublisherServiceType ();
    aServiceMetadataCreate.setServiceMetadataPublisherID (SMP_ID);
    final PublisherEndpointType aEndpoint = new PublisherEndpointType ();
    aEndpoint.setLogicalAddress (ADDRESS_LOGICAL);
    aEndpoint.setPhysicalAddress (ADDRESS_PHYSICAL);
    aServiceMetadataCreate.setPublisherEndpoint (aEndpoint);

    m_aSMClient.create (aServiceMetadataCreate);
  }

  @After
  public void deleteSMP () throws Exception
  {
    m_aSMClient.delete (SMP_ID);
  }

  @Test
  public void createWildcardUnauthorizedFault_WrongScheme () throws Exception
  {
    try
    {
      final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
      aPIClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
      aPIClient.create (SMP_ID, new SimpleParticipantIdentifier (BUSINESS_IDENTIFIER_SCHEME, "*"));
      fail ("User should not be allowed to register wild card for this scheme : " + BUSINESS_IDENTIFIER_SCHEME);
    }
    catch (final UnauthorizedFault e)
    {
      assertTrue (e.getMessage ().contains ("The user is not allowed to register Wildcard for this scheme"));
    }
  }

  @Test
  public void createWildcardUnauthorizedFault_WrongUser () throws Exception
  {
    try
    {
      final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
      aPIClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
      aPIClient.create (UNAUTHRIZED_SML_ID, new SimpleParticipantIdentifier (WILDCARD_ACTORID_ALLOWED_SCHEME,
                                                                             WILDCARD_PI));
      fail ("The user should not be authorized to insert PI when wildcard is on for scheme.");
    }
    catch (final UnauthorizedFault e)
    {
      assertTrue (e.getMessage (),
                  e.getMessage ()
                   .contains ("The user is not allowed to register ParticipantIdentifiers for this scheme"));
    }
  }

  @Test
  public void createWildcardBadRequestFault_MustBeWildcard () throws Exception
  {
    try
    {
      final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
      aPIClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
      aPIClient.create (SMP_ID, new SimpleParticipantIdentifier (WILDCARD_ACTORID_ALLOWED_SCHEME, WILDCARD_PI));
      fail ("User should not be allowed to register wild card for this scheme : " + BUSINESS_IDENTIFIER_SCHEME);
    }
    catch (final BadRequestFault e)
    {
      assertTrue (e.getMessage ().contains ("Only ParticipantIdentifier Wildcards can be registered for this scheme"));
    }
  }

  @Test
  public void createDeleteWildcard () throws Exception
  {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    aPIClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (WILDCARD_ACTORID_ALLOWED_SCHEME, "*");
    aPIClient.create (SMP_ID, aPI);

    // try to delete with un-authorized user!
    try
    {
      final ManageParticipantIdentifierServiceCaller unAuthorizedClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
      unAuthorizedClient.setSSLSocketFactory (createConfiguredSSLSocketFactory (SML_INFO));
      unAuthorizedClient.delete (aPI);
      fail ("The user does not own the identifier : *");
    }
    catch (final UnauthorizedFault e)
    {
      assertTrue (e.getMessage (), e.getMessage ().contains ("The user does not own the identifier."));
    }

    aPIClient.delete (aPI);
  }
}
