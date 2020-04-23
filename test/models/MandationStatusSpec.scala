/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import play.api.libs.json.{JsString, Json}
import utils.TestUtils

class MandationStatusSpec extends TestUtils {

  "Parsing JSON to MandationStatus" when {

    "the status is MTDfB Mandated" should {

      "have the correct value" in {
        JsString("MTDfB Mandated").as[MandationStatus] shouldBe MTDfBMandated
      }
    }

    "the status is MTDfB Voluntary" should {

      "have the correct value" in {
        JsString("MTDfB Voluntary").as[MandationStatus] shouldBe MTDfBVoluntary
      }
    }

    "the status is Non MTDfB" should {

      "have the correct value" in {
        JsString("Non MTDfB").as[MandationStatus] shouldBe NonMTDfB
      }
    }

    "the status is Non Digital" should {

      "have the correct value" in {
        JsString("Non Digital").as[MandationStatus] shouldBe NonDigital
      }
    }

    "the status is MTDfB Exempt" should {

      "have the correct value" in {
        JsString("MTDfB Exempt").as[MandationStatus] shouldBe MTDfBExempt
      }
    }

    "the status is MTDfB" should {

      "have the correct value" in {
        JsString("MTDfB").as[MandationStatus] shouldBe MTDfB
      }
    }

    "the status is not recognised" should {

      "throw an exception" in {
        intercept[Exception](Json.obj("mandationStatus" -> "Yes").as[MandationStatus])
      }
    }
  }

  "Serializing MandationStatus to JSON" should {

    "write the object's value" in {
      Json.toJson(NonMTDfB) shouldBe JsString(NonMTDfB.desValue)
    }
  }

  "Unapplying MandationStatus" should {

    "translate the value to an Option[String]" in {
      MandationStatus.unapply(MTDfBMandated) shouldBe Some(MTDfBMandated.value)
    }
  }
}
