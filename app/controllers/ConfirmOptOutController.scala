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

package controllers

import audit.AuditService
import audit.models.UpdateVatSubscriptionAuditModel
import config.{AppConfig, ErrorHandler}
import controllers.predicates.{AuthPredicate, OptOutPredicate}
import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import models.NonMTDfB
import services.VatSubscriptionService
import common.SessionKeys.{inflightMandationStatus, mandationStatus, optOutSuccessful}

import scala.concurrent.{ExecutionContext, Future}

class ConfirmOptOutController @Inject()(authenticate: AuthPredicate,
                                        optOutPredicate: OptOutPredicate,
                                        errorHandler: ErrorHandler,
                                        vatSubscriptionService: VatSubscriptionService,
                                        auditService: AuditService)
                                        (implicit val appConfig: AppConfig,
                                        val messagesApi: MessagesApi,
                                        val ec: ExecutionContext) extends ControllerBase {

  def show(): Action[AnyContent] = (authenticate andThen optOutPredicate).async { implicit user =>
    Future.successful(Ok(views.html.confirmOptOut(user.isAgent)))
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
          .removingFromSession(mandationStatus)
          .addingToSession(inflightMandationStatus -> "true", optOutSuccessful -> "true")

      case Left(_) =>
        errorHandler.showInternalServerError
    }
  }
}
