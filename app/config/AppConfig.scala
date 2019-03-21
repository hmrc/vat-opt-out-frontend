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

package config

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import uk.gov.hmrc.play.config.ServicesConfig

@ImplementedBy(classOf[FrontendAppConfig])
trait AppConfig extends ServicesConfig {
  val contactHost: String
  val assetsPrefix: String
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, environment: Environment) extends AppConfig {
  override protected def mode: Mode = environment.mode

  lazy val contactHost = runModeConfiguration.getString(s"contact-frontend.host").getOrElse("")
  lazy val contactFormServiceIdentifier = "VATC"

  lazy val assetsPrefix = getString(s"assets.url") + getString(s"assets.version")
  lazy val analyticsToken = getString(s"google-analytics.token")
  lazy val analyticsHost = getString(s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
}