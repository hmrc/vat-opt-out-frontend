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

package models.viewModels

import config.AppConfig
import models.User
import play.api.data.Form

case class TurnoverThresholdViewModel(errorMessage: String,
                                      titleMessage: String,
                                      p1Message: String,
                                      p2Message: String,
                                      yesMessage: String,
                                      yesHint: String,
                                      noMessage: String,
                                      backLinkHref: String,
                                      turnoverThresholdForm: Form[String])

object TurnoverThresholdViewModel {

  def constructModel(turnoverThresholdForm: Form[String])(implicit appConfig: AppConfig,
                                                                   user: User[_]): TurnoverThresholdViewModel = {
    if(user.isAgent) {
      TurnoverThresholdViewModel(
        "turnoverThreshold.agent.error",
        "turnoverThreshold.agent.title",
        "turnoverThreshold.agent.p1",
        "turnoverThreshold.agent.p2",
        "turnoverThreshold.agent.yes",
        "turnoverThreshold.agent.yesHint",
        "turnoverThreshold.agent.no",
        appConfig.agentClientHubPath,
        turnoverThresholdForm
      )
    } else {
      TurnoverThresholdViewModel(
        "turnoverThreshold.client.error",
        "turnoverThreshold.client.title",
        "turnoverThreshold.client.p1",
        "turnoverThreshold.client.p2",
        "turnoverThreshold.client.yes",
        "turnoverThreshold.client.yesHint",
        "turnoverThreshold.client.no",
        appConfig.vatSummaryServicePath,
        turnoverThresholdForm
      )
    }
  }

}
