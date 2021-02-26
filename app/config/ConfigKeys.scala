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

package config

object ConfigKeys {

  val vatOptOutServiceUrl: String = "vat-opt-out-frontend.url"
  val vatOptOutServicePath: String = "vat-opt-out-frontend.path"
  val agentClientLookupUrl: String = "vat-agent-client-lookup-frontend.url"
  val agentClientLookupPath: String = "vat-agent-client-lookup-frontend.path"
  val vatAgentClientLookupServiceUnauthPath: String = "vat-agent-client-lookup-frontend.unauthorised"
  val agentClientLookupChoices: String = "vat-agent-client-lookup-frontend.choicesPath"
  val signInBaseUrl: String = "signIn.url"
  val manageVatSubscriptionServiceUrl: String = "manage-vat-subscription-frontend.url"
  val manageVatSubscriptionServicePath: String = "manage-vat-subscription-frontend.path"
  val thresholdPreviousYearsUrl: String = "govuk.thresholdPreviousYearsUrl"
  val vatSubscription: String = "vat-subscription"
  val host: String = "host"
  val exitSurveyHost: String = "feedback-frontend.host"
  val exitSurveyPath: String = "feedback-frontend.path"
  val governmentGatewayHost: String = "government-gateway.host"
  val govUkSetupAgentServices: String = "govuk.setupAgentServicesUrl"
  val timeoutPeriod: String = "timeout.period"
  val timeoutCountdown: String = "timeout.countdown"
  val govUkManageClientsDetails: String = "govuk.manageClientsDetails"
  val govUkContactUs: String = "govuk.contactUs"
  val thresholdAmount: String = "threshold.amount"
  val vatSummaryServiceUrl: String = "vat-summary-frontend.url"
  val vatSummaryServicePath: String = "vat-summary-frontend.path"
  val vatSummaryAccessibilityUrl: String = "vat-summary-frontend.accessibilityUrl"
  val gtmContainer: String = "tracking-consent-frontend.gtm.container"
}
