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

package assets

import common.Constants
import models.ContactPreferences
import models.viewModels.ConfirmationPreference
import play.api.libs.json.{JsObject, Json}

object ContactPreferencesConstants {

  val contactPreferencesJsonMax: JsObject = Json.obj(
    "preference" -> "Digital"
  )

  val contactPreferenceJsonInvalid: JsObject = Json.obj(
    "preferences" -> true
  )

  val contactPreferenceModelDigital = ContactPreferences("Digital")

  val agentPreferencesPaper = ConfirmationPreference(isTransactor = true, Constants.preferencePaper, Some("Acme ltd"), Some("test@test.com"))
  val agentPreferencesDigital = ConfirmationPreference(isTransactor = true, Constants.preferenceDigital, Some("Acme ltd"), Some("test@test.com"))
  val clientPreferencesDigital = ConfirmationPreference(isTransactor = false, Constants.preferenceDigital, None, None)
  val clientPreferencesPaper = ConfirmationPreference(isTransactor = false, Constants.preferencePaper, None, None)
  val clientPreferencesFail = ConfirmationPreference(isTransactor = false, Constants.preferenceFail, None, None)
}
