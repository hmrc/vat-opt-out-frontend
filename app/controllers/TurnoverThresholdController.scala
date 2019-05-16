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
import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import forms.TurnoverThresholdForm._
import common.SessionKeys.turnoverThreshold
import controllers.predicates.{AuthPredicate, OptOutPredicate}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TurnoverThresholdController @Inject()(authenticate: AuthPredicate, val optOutPredicate: OptOutPredicate)
                                           (implicit val appConfig: AppConfig,
                                            val messagesApi: MessagesApi, val ec: ExecutionContext) extends ControllerBase {

  val show: Action[AnyContent] = (authenticate andThen optOutPredicate).async { implicit user =>
    user.session.get(turnoverThreshold) match {
      case Some(turnoverOption) =>
        Future.successful(Ok(views.html.turnoverThreshold(turnoverThresholdForm.fill(turnoverOption), user.isAgent)))
      case _ =>
        Future.successful(Ok(views.html.turnoverThreshold(turnoverThresholdForm, user.isAgent)))
    }
  }

  def submit: Action[AnyContent] = (authenticate andThen optOutPredicate) { implicit user =>
    turnoverThresholdForm.bindFromRequest().fold(
      error => {
        BadRequest(views.html.turnoverThreshold(
          error.discardingErrors.withError("threshold", "turnoverThreshold.error.empty"), user.isAgent
        ))
      },
      {
        case formData@Constants.optionYes => Redirect(controllers.routes.CannotOptOutController.show())
          .addingToSession(SessionKeys.turnoverThreshold -> formData)
        case formData@Constants.optionNo => Redirect(controllers.routes.ConfirmOptOutController.show())
          .addingToSession(SessionKeys.turnoverThreshold -> formData)
      }
    )
  }
}
