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

import common.{Constants, SessionKeys}
import config.AppConfig
import controllers.predicates.AuthPredicate
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import forms.ConfirmOptOutForm._

import scala.concurrent.Future

@Singleton
class ConfirmOptOutController @Inject()(val messagesApi: MessagesApi,
                                        val authenticate: AuthPredicate)
                                       (implicit val appConfig: AppConfig) extends ControllerBase {

  def show(): Action[AnyContent] = authenticate.async { implicit request =>

    val confirmOptOut = request.session.get(SessionKeys.confirmOptOut)

    confirmOptOut match {
      case Some(answer) =>
        Future.successful(Ok(views.html.confirmOptOut(confirmOptOutForm.fill(answer))))
      case None =>
        Future.successful(Ok(views.html.confirmOptOut(confirmOptOutForm)))
    }

  }

  def submit: Action[AnyContent] = authenticate { implicit user =>
    confirmOptOutForm.bindFromRequest().fold(
      error => BadRequest(views.html.confirmOptOut(error.discardingErrors.withError(
        Constants.confirmOptOut, "confirmOptOut.error.empty"))),
      {
        case formData@Constants.optionYes =>
          Redirect(controllers.routes.ConfirmationController.show())
            .addingToSession(SessionKeys.confirmOptOut -> formData)

        case formData@Constants.optionNo =>
          Redirect(controllers.routes.DecidedNotToOptOutController.show())
            .addingToSession(SessionKeys.confirmOptOut -> formData)
      }
    )
  }
}
