/*
 * Copyright 2020 HM Revenue & Customs
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

package services

import connectors.VatSubscriptionConnector
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import models.{MandationStatus, User}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionService @Inject()(connector: VatSubscriptionConnector) {

  def getCustomerInfo(vrn: String)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetVatSubscriptionResponse] =
    connector.getCustomerInfo(vrn)

  def updateMandationStatus(vrn: String,
                            mandationStatus: MandationStatus)
                           (implicit hc: HeaderCarrier,
                            ec: ExecutionContext,
                            user: User[_]): Future[UpdateVatSubscriptionResponse] =
    connector.updateMandationStatus(vrn, mandationStatus)
 }
