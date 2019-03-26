/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import controllers.predicates.{AuthPredicate, AuthoriseAsAgentWithClient}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core._

import scala.concurrent.Future

trait MockAuth extends TestUtils with MockitoSugar {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

  val mockAuthAsAgentWithClient = new AuthoriseAsAgentWithClient(
    mockEnrolmentsAuthService,
    mockErrorHandler,
    messagesApi,
    appConfig,
    ec
  )

  val mockAuthPredicate = new AuthPredicate(
    mockEnrolmentsAuthService,
    messagesApi,
    mockErrorHandler,
    mockAuthAsAgentWithClient,
    appConfig,
    ec
  )

  def setupAuthResponse(authResult: Future[~[Option[AffinityGroup], Enrolments]]): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] = {
    when(mockAuthConnector.authorise(
      any(), any[Retrieval[~[Option[AffinityGroup], Enrolments]]]())(any(), any())
    ).thenReturn(authResult)
  }

  def mockIndividualAuthorised(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(Some(AffinityGroup.Individual),
        Enrolments(Set(Enrolment("HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("VRN", "123456789")),
          "Activated"
        )))
      )
    ))

  def mockAgentAuthorised(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(Some(AffinityGroup.Agent),
        Enrolments(Set(Enrolment("HMRC-AS-AGENT",
          Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
          "Activated",
          Some("mtd-vat-auth")
        )))
      )
    ))

  def mockAgentWithoutEnrolment(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(Some(AffinityGroup.Agent),
        Enrolments(Set(Enrolment("OTHER_ENROLMENT",
          Seq(EnrolmentIdentifier("", "")),
          "Activated"
        )))
      )
    ))

  def mockIndividualWithoutEnrolment(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(Some(AffinityGroup.Individual),
        Enrolments(Set(Enrolment("OTHER_ENROLMENT",
          Seq(EnrolmentIdentifier("", "")),
          ""
        )))
      )
    ))

  def mockUserWithoutAffinity(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(None,
        Enrolments(Set(Enrolment("HMRC-MTD-VAT",
          Seq(EnrolmentIdentifier("VRN", "123456789")),
          "Activated"
        )))
      )
    ))

  def mockAgentWithoutAffinity(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.successful(
      new ~(None,
        Enrolments(Set(Enrolment("HMRC-AS-AGENT",
          Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
          "Activated",
          Some("mtd-vat-auth")
        )))
      )
    ))

  def mockMissingBearerToken()(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.failed(MissingBearerToken()))

  def mockUnauthorised()(): OngoingStubbing[Future[~[Option[AffinityGroup], Enrolments]]] =
    setupAuthResponse(Future.failed(InsufficientEnrolments()))
}
