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

package controllers.predicates

import audit.AuditService
import audit.models.GetCustomerInfoAuditModel
import common.SessionKeys.{inflightMandationStatus, mandationStatus}
import config.{AppConfig, ErrorHandler}
import javax.inject.{Inject, Singleton}
import models.{MTDfBExempt, NonDigital, NonMTDfB, User}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, MessagesControllerComponents, Result}
import services.VatSubscriptionService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OptOutPredicate @Inject()(vatSubscriptionService: VatSubscriptionService,
                                val errorHandler: ErrorHandler,
                                val messagesApi: MessagesApi,
                                auditService: AuditService,
                                implicit val appConfig: AppConfig,
                                implicit val mcc: MessagesControllerComponents)
  extends ActionRefiner[User, User] with I18nSupport {

  override implicit val executionContext: ExecutionContext = mcc.executionContext
  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    implicit val req: User[A] = request

    val getSessionAttribute: String => Option[String] = req.session.get

    (getSessionAttribute(inflightMandationStatus), getSessionAttribute(mandationStatus)) match {
      case (_, Some(NonMTDfB.value)) | (_, Some(NonDigital.value)) | (_, Some(MTDfBExempt.value)) | (Some("true"), _) =>
        Future.successful(Left(redirectOutOfJourney))
      case (Some("false"), _) =>
        Future.successful(Right(req))
      case _ =>
        getCustomerInfoCall(req.vrn)
    }
  }

  private def getCustomerInfoCall[A](vrn: String)(implicit hc: HeaderCarrier,
                                                  request: User[A]): Future[Either[Result, User[A]]] =
    vatSubscriptionService.getCustomerInfo(vrn).map {
      case Right(customerInfo) =>

        val auditModel = GetCustomerInfoAuditModel(
          request.vrn,
          request.arn,
          request.isAgent,
          customerInfo.mandationStatus,
          customerInfo.inflightMandationStatus
        )
        auditService.audit(auditModel, Some(controllers.routes.TurnoverThresholdController.show().url))

        (customerInfo.inflightMandationStatus, customerInfo.mandationStatus) match {
          case (_, NonMTDfB) =>
            Logger.warn("[OptOutPredicate][getCustomerInfoCall] - " +
              "Mandation status is NonMTDfB. Redirecting user out of journey.")
            Left(redirectOutOfJourney.addingToSession(mandationStatus -> NonMTDfB.value))
          case (_, NonDigital) =>
            Logger.warn("[OptOutPredicate][getCustomerInfoCall] - " +
              "Mandation status is NonDigital. Redirecting user out of journey.")
            Left(redirectOutOfJourney.addingToSession(mandationStatus -> NonDigital.value))
          case (_, MTDfBExempt) =>
            Logger.warn("[OptOutPredicate][getCustomerInfoCall] - " +
              "Mandation status is MTDfBExempt. Redirecting user out of journey.")
            Left(redirectOutOfJourney.addingToSession(mandationStatus -> MTDfBExempt.value))
          case (true, _) =>
            Logger.warn("[OptOutPredicate][getCustomerInfoCall] - " +
              "Mandation status is inflight. Redirecting user out of journey.")
            Left(redirectOutOfJourney.addingToSession(inflightMandationStatus -> "true"))
          case (false, mandStatus) =>
            Logger.debug("[OptOutPredicate][getCustomerInfoCall] -"
              + "Mandation status is not in flight and not NonMTDfB. Redirecting user to the start of the journey.")
            Left(Redirect(controllers.routes.TurnoverThresholdController.show().url)
              .addingToSession(
                mandationStatus -> mandStatus.value,
                inflightMandationStatus -> "false"
              )
            )
        }
      case Left(error) =>
        Logger.warn(s"[OptOutPredicate][getCustomerInfoCall] - The call to the GetCustomerInfo API failed. Error: ${error.body}")
        Left(errorHandler.showInternalServerError)
    }

  private def redirectOutOfJourney[A](implicit user: User[A]): Result =
    if (user.isAgent) {
      Redirect(appConfig.agentClientHubPath)
    } else {
      Redirect(appConfig.vatSummaryServicePath)
    }
}
