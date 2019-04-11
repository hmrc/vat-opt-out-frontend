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

import java.util.Base64

import config.{ConfigKeys => Keys}
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import play.api.mvc.Call
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig extends ServicesConfig {
  val contactHost: String
  val assetsPrefix: String
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val whitelistEnabled: Boolean
  val whitelistedIps: Seq[String]
  val whitelistExcludedPaths: Seq[Call]
  val shutterPage: String
  val vatOptOutServiceUrl: String
  val vatOptOutServicePath: String
  val agentClientLookupServiceUrl: String
  val agentClientLookupServicePath: String
  val signInUrl: String
  val manageVatUrl: String
  val thresholdPreviousYearsUrl: String
  val vatSubscriptionHost: String
  val contactPreferencesHost: String
}

@Singleton
class FrontendAppConfig @Inject()(val runModeConfiguration: Configuration, environment: Environment) extends AppConfig {
  override protected def mode: Mode = environment.mode

  lazy val contactHost: String = runModeConfiguration.getString(s"contact-frontend.host").getOrElse("")
  lazy val contactFormServiceIdentifier = "VATC"

  lazy val assetsPrefix: String = getString(s"assets.url") + getString(s"assets.version")
  lazy val analyticsToken: String = getString(s"google-analytics.token")
  lazy val analyticsHost: String = getString(s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private lazy val signInContinueUrl: String = ContinueUrl(vatOptOutServicePath).encodedUrl
  private lazy val signInBaseUrl: String = getString(Keys.signInBaseUrl)
  private lazy val signInOrigin = getString("appName")
  override lazy val signInUrl: String = s"$signInBaseUrl?continue=$signInContinueUrl&origin=$signInOrigin"

  private def whitelistConfig(key: String): Seq[String] = Some(new String(Base64.getDecoder
    .decode(getString(key)), "UTF-8"))
    .map(_.split(",")).getOrElse(Array.empty).toSeq

  override lazy val whitelistEnabled: Boolean = getBoolean(Keys.whitelistEnabled)
  override lazy val whitelistedIps: Seq[String] = whitelistConfig(Keys.whitelistedIps)
  override lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(Keys.whitelistExcludedPaths).map(path => Call("GET", path))
  override lazy val shutterPage: String = getString(Keys.whitelistShutterPage)

  override val vatOptOutServiceUrl: String = getString(Keys.vatOptOutServiceUrl)
  override val vatOptOutServicePath: String =
    vatOptOutServiceUrl + getString(Keys.vatOptOutServicePath)


  override val agentClientLookupServiceUrl: String = getString(Keys.agentClientLookupUrl)
  override val agentClientLookupServicePath: String = agentClientLookupHandoff(controllers.routes.OptOutStartController.show().url)



  def agentClientLookupHandoff(redirectUrl: String): String = {
    agentClientLookupServiceUrl + getString(Keys.agentClientLookupPath) +
      s"/client-vat-number?redirectUrl=${ContinueUrl(vatOptOutServiceUrl + redirectUrl).encodedUrl}"
  }

  override val manageVatUrl: String = getString(Keys.manageVatServiceUrl)

  override val thresholdPreviousYearsUrl: String = getString(Keys.thresholdPreviousYearsUrl)
  override val vatSubscriptionHost: String = baseUrl(Keys.vatSubscription)
  override val contactPreferencesHost: String = baseUrl(Keys.contactPreferences)
}
