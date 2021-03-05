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

package controllers.predicates

import javax.inject.{Inject, Singleton}
import common.{EnrolmentKeys, SessionKeys}
import config.{AppConfig, ErrorHandler}
import models.User
import play.api.Logger
import play.api.mvc._
import services.{EnrolmentsAuthService, VatSubscriptionService}
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolments, NoActiveSession}
import uk.gov.hmrc.auth.core.retrieve._
import views.html.errors.{SessionTimeoutView, UnauthorisedAgentView, UnauthorisedView}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthPredicate @Inject()(enrolmentsAuthService: EnrolmentsAuthService,
                              vatSubscriptionService: VatSubscriptionService,
                              val errorHandler: ErrorHandler,
                              sessionTimeout: SessionTimeoutView,
                              unauthorisedAgent: UnauthorisedAgentView,
                              unauthorised: UnauthorisedView,
                              val authenticateAsAgentWithClient: AuthoriseAsAgentWithClient,
                              implicit val appConfig: AppConfig,
                              override implicit val mcc: MessagesControllerComponents)
  extends AuthBasePredicate with ActionBuilder[User, AnyContent] with ActionFunction[Request, User] {

  override implicit val executionContext: ExecutionContext = mcc.executionContext
  override val parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override def invokeBlock[A](request: Request[A], block: User[A] => Future[Result]): Future[Result] = {

    implicit val req: Request[A] = request
    enrolmentsAuthService.authorised().retrieve(v2.Retrievals.affinityGroup and v2.Retrievals.allEnrolments) {
      case Some(affinityGroup) ~ allEnrolments =>
        (isAgent(affinityGroup), allEnrolments) match {
          case (true, enrolments) => checkAgentEnrolment(enrolments, block)
          case (false, enrolments) => checkVatEnrolment(enrolments, block)
        }
      case _ =>
        Logger.warn("[AuthPredicate][invokeBlock] - Missing affinity group")
        Future.successful(errorHandler.showInternalServerError)
    } recover {
      case _: NoActiveSession =>
        Logger.debug("[AuthPredicate][invokeBlock] - No active session, rendering Session Timeout view")
        Unauthorized(sessionTimeout())

      case _: AuthorisationException =>
        Logger.warn("[AuthPredicate][invokeBlock] - Unauthorised exception, rendering standard error view")
        errorHandler.showInternalServerError
    }
  }

  private def checkAgentEnrolment[A](enrolments: Enrolments, block: User[A] => Future[Result])(implicit request: Request[A]) =
    if (enrolments.enrolments.exists(_.key == EnrolmentKeys.agentEnrolmentId)) {
      Logger.debug("[AuthPredicate][checkAgentEnrolment] - Authenticating as agent")
      authenticateAsAgentWithClient.invokeBlock(request, block)
    }
    else {
      Logger.debug(s"[AuthPredicate][checkAgentEnrolment] - Agent without HMRC-AS-AGENT enrolment. Enrolments: $enrolments")
      Logger.warn(s"[AuthPredicate][checkAgentEnrolment] - Agent without HMRC-AS-AGENT enrolment.")
      Future.successful(Forbidden(unauthorisedAgent()))
    }

  private def checkVatEnrolment[A](enrolments: Enrolments, block: User[A] => Future[Result])(implicit request: Request[A]) =
    if (enrolments.enrolments.exists(_.key == EnrolmentKeys.vatEnrolmentId)) {
      val user = User(enrolments)
      request.session.get(SessionKeys.insolventWithoutAccessKey) match {
        case Some("true") => Future.successful(Forbidden(unauthorised()))
        case Some("false") => block(user)
        case _ => vatSubscriptionService.getCustomerInfo(user.vrn).flatMap {
          case Right(details) if details.isInsolventWithoutAccess =>
            Logger.debug("[AuthPredicate][checkVatEnrolment] - User is insolvent and not continuing to trade")
            Future.successful(Forbidden(unauthorised()).addingToSession(SessionKeys.insolventWithoutAccessKey -> "true"))
          case Right(_) =>
            Logger.debug("[AuthPredicate][checkVatEnrolment] - Authenticated as principle")
            block(user).map(result => result.addingToSession(SessionKeys.insolventWithoutAccessKey -> "false"))
          case _ =>
            Logger.warn("[AuthPredicate][checkVatEnrolment] - Failure obtaining insolvency status from Customer Info API")
            Future.successful(errorHandler.showInternalServerError)
        }
      }
    } else {
      Logger.debug(s"[AuthPredicate][checkVatEnrolment] - Non-agent without HMRC-MTD-VAT enrolment. $enrolments")
      Future.successful(Forbidden(unauthorised()))
    }
}
