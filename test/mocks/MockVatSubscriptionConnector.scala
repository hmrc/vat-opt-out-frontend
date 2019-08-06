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

import assets.BaseTestConstants.{errorModel, updateVatSubscriptionModel}
import assets.CustomerInformationConstants.customerInfoModel
import connectors.VatSubscriptionConnector
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import models.MTDfBMandated
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockVatSubscriptionConnector extends MockitoSugar {

  type VatGetStub = OngoingStubbing[Future[GetVatSubscriptionResponse]]
  type VatUpdateStub = OngoingStubbing[Future[UpdateVatSubscriptionResponse]]

  val connector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def mockGetVatSubscriptionResponse(result: Future[GetVatSubscriptionResponse]): VatGetStub =
    when(connector.getCustomerInfo(any())(any(), any())).thenReturn(result)

  def mockGetVatSubscriptionSuccess(): VatGetStub =
    mockGetVatSubscriptionResponse(Future.successful(Right(customerInfoModel(MTDfBMandated))))

  def mockGetVatSubscriptionFailure(): VatGetStub =
    mockGetVatSubscriptionResponse(Future.successful(Left(errorModel)))

  def mockUpdateVatSubscriptionResponse(result: Future[UpdateVatSubscriptionResponse]): VatUpdateStub =
    when(connector.updateMandationStatus(any(),any())(any(), any(), any())).thenReturn(result)

  def mockUpdateVatSubscriptionSuccess(): VatUpdateStub =
    mockUpdateVatSubscriptionResponse(Future.successful(Right(updateVatSubscriptionModel)))

  def mockUpdateVatSubscriptionFailure(): VatUpdateStub =
    mockUpdateVatSubscriptionResponse(Future.successful(Left(errorModel)))

}
