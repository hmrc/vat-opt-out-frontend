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

package controllers.predicates

import config.{AppConfig, ErrorHandler}
import javax.inject.Inject
import models.{NonMTDfB, User}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{ActionRefiner, Request, Result}
import services.VatSubscriptionService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import play.api.mvc.Results.{Ok, Redirect}
import scala.concurrent.{ExecutionContext, Future}
import common.SessionKeys.{businessName, inflightMandationStatus, mandationStatus}
@Singleton
class OptOutPredicate @Inject()(vatSubscriptionService: VatSubscriptionService,
                                val errorHandler: ErrorHandler,
                                val messagesApi: MessagesApi,
                                implicit val appConfig: AppConfig,
                                implicit val ec: ExecutionContext)
  extends ActionRefiner[User, User] with I18nSupport {

  override def refine[A](request: User[A]): Future[Either[Result, User[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    implicit val req: User[A] = request

    req.session.get() match {

    }
//    req.session.get(inFlightContactDetailsChangeKey) match {
//      case Some("true") => Future.successful(Left(Ok(views.html.errors.ppobChangePending())))
//      case Some("false") => Future.successful(Right(req))
//      case Some(_) => Future.successful(Left(errorHandler.showInternalServerError))
//      case None => getCustomerInfoCall(req.vrn)
//    }
  }

  private def getCustomerInfoCall[A](vrn: String)(implicit hc: HeaderCarrier,
                                                  request: User[A]): Future[Either[Result, User[A]]] =
    vatSubscriptionService.getCustomerInfo(vrn).map {
      case Right(customerInfo) =>
        (customerInfo.businessName, customerInfo.inflightMandationStatus, customerInfo.mandationStatus) match {
          case (_, _, NonMTDfB) =>
            //TODO error page provided by BTAT-5727
            request
            Left(errorHandler.showInternalServerError.addingToSession(mandationStatus -> NonMTDfB.value))
          case (_, true, _) =>
            Left(errorHandler.showInternalServerError.addingToSession(inflightMandationStatus -> "true"))
          case (maybeBussName, false, mandStatus) =>
            Left(Redirect(controllers.routes.OptOutStartController.show().url)
              .addingToSession(
                businessName -> maybeBussName.getOrElse(""),
                mandationStatus -> mandStatus.value,
                inflightMandationStatus -> "false"
              )
            )
        }
      case Left(error) =>
        Logger.warn(s"[OptOutPredicate][getCustomerInfoCall] - The call to the GetCustomerInfo API failed. Error: ${error.body}")
        Left(errorHandler.showInternalServerError)
    }
}