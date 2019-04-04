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

package mocks

import assets.BaseTestConstants.errorModel
import assets.CustomerInformationConstants.customerInfoModelTradeName
import connectors.VatSubscriptionConnector
import connectors.httpParsers.VatSubscriptionHttpParser.VatSubscriptionResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockVatSubscriptionConnector extends MockitoSugar {

  type VatSubStub = OngoingStubbing[Future[VatSubscriptionResponse]]

  val connector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def mockVatSubscriptionResponse(result: Future[VatSubscriptionResponse]): VatSubStub =
    when(connector.getCustomerInfo(any())(any(), any())).thenReturn(result)

  def mockVatSubscriptionSuccess(): VatSubStub =
    mockVatSubscriptionResponse(Future.successful(Right(customerInfoModelTradeName)))

  def mockVatSubscriptionFailure(): VatSubStub =
    mockVatSubscriptionResponse(Future.successful(Left(errorModel)))
}
