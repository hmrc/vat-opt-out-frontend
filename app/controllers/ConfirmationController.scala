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

import config.AppConfig
import common.Constants.{preferenceDigital, preferenceFail, preferencePaper}
import common.SessionKeys
import controllers.predicates.{AuthPredicate, OptOutPredicate}
import javax.inject.Inject
import models.ContactPreferences
import models.viewModels.ConfirmationPreference
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request}
import services.ContactPreferencesService

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(authenticate: AuthPredicate, val optOutPredicate: OptOutPredicate,
                                       val contactPreferencesService: ContactPreferencesService)
                                      (implicit val appConfig: AppConfig, val messagesApi: MessagesApi,
                                       val ec: ExecutionContext) extends ControllerBase {

  def show(): Action[AnyContent] = (authenticate andThen optOutPredicate).async { implicit request =>

    if (request.isAgent) {
      Future.successful(Ok(views.html.confirmation(getTransactorData(request))))
    } else {
      contactPreferencesService.getContactPreferences(request.vrn).map {
        case Right(contactPreference) =>
          Ok(views.html.confirmation(getClientData(Some(contactPreference))))
        case _ =>
          Ok(views.html.confirmation(getClientData(None)))
      }
    }
  }

  private def getTransactorData(request: Request[AnyContent]): ConfirmationPreference = {

    val transactorEmail: Option[String] = extractFromSession(request, SessionKeys.verifiedAgentEmail)
    val preferenceType: String = transactorEmail.fold(preferencePaper)(_ => preferenceDigital)
    val businessName = extractFromSession(request, SessionKeys.businessName)

    ConfirmationPreference(isTransactor = true, preferenceType, businessName, transactorEmail)

  }

  private def getClientData(contactPreferences: Option[ContactPreferences]): ConfirmationPreference = {
    val preferenceType = contactPreferences.fold(preferenceFail)(pref => pref.preference)
    ConfirmationPreference(isTransactor = false, preferenceType, None, None)
  }

  private[controllers] def extractFromSession(request: Request[AnyContent], sessionKey: String): Option[String] = {
    request.session.get(sessionKey).filter(_.nonEmpty)
  }
}
