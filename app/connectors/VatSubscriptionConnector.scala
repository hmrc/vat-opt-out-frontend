/*
 * Copyright 2021 HM Revenue & Customs
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

package connectors

import common.SessionKeys
import config.AppConfig
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionReads
import connectors.httpParsers.GetVatSubscriptionHttpParser.GetVatSubscriptionResponse
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionReads
import connectors.httpParsers.UpdateVatSubscriptionHttpParser.UpdateVatSubscriptionResponse
import javax.inject.{Inject, Singleton}
import models.{MandationStatus, MandationStatusPost, User}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatSubscriptionConnector @Inject()(http: HttpClient,
                                         appConfig: AppConfig) {

  private[connectors] def getCustomerInfoUrl(vrn: String): String =
    s"${appConfig.vatSubscriptionHost}/vat-subscription/$vrn/full-information"

  private[connectors] def updateMandationStatusUrl(vrn: String): String =
    s"${appConfig.vatSubscriptionHost}/vat-subscription/$vrn/mandation-status"

  def getCustomerInfo(vrn: String)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetVatSubscriptionResponse] = {

    http.GET[GetVatSubscriptionResponse](getCustomerInfoUrl(vrn)).map {
      case customerInfo@Right(_) =>
        Logger.debug(s"[VatSubscriptionConnector][getCustomerInfo] successfully received customer info response")
        customerInfo
      case httpError@Left(error) =>
        Logger.warn("[VatSubscriptionConnector][getCustomerInfo] received error - " + error.body)
        httpError
    }
  }

  def updateMandationStatus(vrn: String,
                            mandationStatus: MandationStatus)
                           (implicit hc: HeaderCarrier,
                            ec: ExecutionContext,
                            user: User[_]): Future[UpdateVatSubscriptionResponse] = {

    val updateModel = MandationStatusPost(mandationStatus, user.session.get(SessionKeys.verifiedAgentEmail))

    http.PUT[MandationStatusPost, UpdateVatSubscriptionResponse](updateMandationStatusUrl(vrn), updateModel).map {
      case success@Right(response) =>
        Logger.debug(s"[VatSubscriptionConnector][updateMandationStatus] successfully received response: " + response)
        success
      case httpError@Left(error) =>
        Logger.warn(s"[VatSubscriptionConnector][updateMandationStatus] received error: " + error.body)
        httpError
    }
  }
}
