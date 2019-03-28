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

package mocks

import play.api.mvc.Call
import config.AppConfig
import play.api.{Configuration, Mode}
import play.api.Mode.Mode

class MockAppConfig(val runModeConfiguration: Configuration, val mode: Mode = Mode.Test) extends AppConfig {
  override val contactHost = ""
  override val assetsPrefix = ""
  override val analyticsToken = ""
  override val analyticsHost = ""
  override val reportAProblemPartialUrl = ""
  override val reportAProblemNonJSUrl = ""
  override val whitelistEnabled: Boolean = false
  override val whitelistedIps: Seq[String] = Seq("")
  override val whitelistExcludedPaths: Seq[Call] = Nil
  override val shutterPage: String = "https://www.tax.service.gov.uk/shutter/vat-change-of-circumstances"
  override val vatOptOutServiceUrl = ""
  override val vatOptOutServicePath = ""
  override val signInUrl = "/sign-in-url"
  override val manageVatUrl: String = "/manage-vat"
}

