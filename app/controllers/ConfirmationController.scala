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

import common.SessionKeys.optOutSuccessful
import config.{AppConfig, ErrorHandler}
import controllers.predicates.AuthPredicate
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import views.html.ConfirmationView
import scala.concurrent.ExecutionContext

class ConfirmationController @Inject()(authenticate: AuthPredicate,
                                       errorHandler: ErrorHandler,
                                       confirmationView: ConfirmationView)
                                      (implicit val appConfig: AppConfig,
                                       override val mcc: MessagesControllerComponents
                                      ) extends ControllerBase {
  implicit val ec: ExecutionContext = mcc.executionContext

  def show(): Action[AnyContent] = authenticate { implicit user =>
    user.session.get(optOutSuccessful) match {
      case Some("true") => Ok(confirmationView())
      case _ => errorHandler.showInternalServerError
    }
  }
}
