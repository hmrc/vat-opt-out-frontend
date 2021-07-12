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

import play.core.PlayVersion.current
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, integrationTestSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "vat-opt-out-frontend"
RoutesKeys.routesImport := Seq.empty

val compile = Seq(
  "uk.gov.hmrc"             %% "govuk-template"             % "5.69.0-play-26",
  "uk.gov.hmrc"             %% "play-ui"                    % "9.6.0-play-26",
  "uk.gov.hmrc"             %% "bootstrap-frontend-play-26" % "5.7.0",
  "uk.gov.hmrc"             %% "play-language"              % "5.1.0-play-26",
  "com.typesafe.play"       %% "play-json-joda"             % "2.6.14",
  "uk.gov.hmrc"             %% "play-frontend-govuk"        % "0.80.0-play-26",
  "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "0.82.0-play-26"
)

def test(scope:String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatest"           %% "scalatest"                   % "3.0.9"                 % scope,
  "org.jsoup"               %  "jsoup"                       % "1.13.1"                % scope,
  "com.typesafe.play"       %% "play-test"                   % current                 % scope,
  "org.pegdown"             %  "pegdown"                     % "1.6.0"                 % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play"          % "3.1.3"                 % scope,
  "uk.gov.hmrc"             %% "hmrctest"                    % "3.10.0-play-26"        % scope,
  "org.mockito"             %  "mockito-core"                % "2.28.2"                % scope,
  "com.github.tomakehurst"  %  "wiremock-jre8"               % "2.27.2"                % scope
)

lazy val appDependencies:Seq[ModuleID] = compile ++ test()

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.govukfrontend.views.html.helpers._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._",
  "uk.gov.hmrc.hmrcfrontend.views.html.helpers._"
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = tests map {
  test => Group(test.name, Seq(test), SubProcess(
    ForkOptions().withRunJVMOptions(Vector("-Dtest.name=" + test.name, "-Dlogger.resource=logback-test.xml"))
  ))
}

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    ".*standardError*.*",
    ".*govuk_wrapper*.*",
    ".*main_template*.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "config.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    "views.html.*",
    ".*feedback*.*"
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimum := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 0,
    libraryDependencies              ++= appDependencies,
    PlayKeys.playDefaultPort         := 9166,
    scalaVersion                     := "2.12.12"
  )
  .settings(publishingSettings: _*)
  .settings(coverageSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
