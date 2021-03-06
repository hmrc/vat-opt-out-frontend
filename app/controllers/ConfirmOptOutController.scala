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

package controllers

import audit.AuditService
import audit.models.UpdateVatSubscriptionAuditModel
import config.{AppConfig, ErrorHandler}
import controllers.predicates.{AuthPredicate, OptOutPredicate}
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import models.{ErrorModel, NonMTDfB}
import services.VatSubscriptionService
import common.SessionKeys._
import play.api.Logger
import views.html.ConfirmOptOutView
import scala.concurrent.{ExecutionContext, Future}

class ConfirmOptOutController @Inject()(authenticate: AuthPredicate,
                                        optOutPredicate: OptOutPredicate,
                                        errorHandler: ErrorHandler,
                                        vatSubscriptionService: VatSubscriptionService,
                                        auditService: AuditService,
                                        confirmOptOutView: ConfirmOptOutView)
                                        (implicit val appConfig: AppConfig,
                                         override val mcc: MessagesControllerComponents
                                        ) extends ControllerBase {
  implicit val ec: ExecutionContext = mcc.executionContext

  def show(): Action[AnyContent] = (authenticate andThen optOutPredicate).async { implicit user =>
    Future.successful(Ok(confirmOptOutView()))
  }

  def updateMandationStatus(): Action[AnyContent] = (authenticate andThen optOutPredicate).async { implicit user =>

    vatSubscriptionService.updateMandationStatus(user.vrn, NonMTDfB) map {
      case Right(_) =>
        val auditModel = UpdateVatSubscriptionAuditModel(
          user.vrn,
          user.arn,
          user.isAgent
        )
        auditService.audit(auditModel, Some(controllers.routes.ConfirmOptOutController.updateMandationStatus().url))

        Redirect(routes.ConfirmationController.show())
          .removingFromSession(turnoverThreshold)
          .addingToSession(inflightMandationStatus -> "true", optOutSuccessful -> "true", mandationStatus -> NonMTDfB.value)

      case Left(ErrorModel(CONFLICT, _)) =>
        Logger.warn("[ConfirmOptOutController][updateMandationStatus] - " +
          "There is already a mandation status update in progress. Redirecting user to vat-overview page.")
        if (user.isAgent) {
          Redirect(appConfig.manageVatSubscriptionServicePath).addingToSession(inflightMandationStatus -> "true")
        } else {
          Redirect(appConfig.vatSummaryServicePath).addingToSession(inflightMandationStatus -> "true")
        }
      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }
}
