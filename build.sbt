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

import play.core.PlayVersion.current
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, integrationTestSettings}
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "vat-opt-out-frontend"

val compile = Seq(
  "uk.gov.hmrc"             %% "govuk-template"           % "5.55.0-play-26",
  "uk.gov.hmrc"             %% "play-ui"                  % "8.10.0-play-26",
  "uk.gov.hmrc"             %% "auth-client"              % "3.0.0-play-26",
  "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.8.0",
  "uk.gov.hmrc"             %% "play-language"            % "4.3.0-play-26",
  "com.typesafe.play"       %% "play-json-joda"           % "2.6.0-RC1",
  "uk.gov.hmrc"             %% "play-frontend-govuk"      % "0.44.0-play-26",
  "uk.gov.hmrc"             %% "play-frontend-hmrc"       % "0.14.0-play-26"
)

def test(scope:String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"             %% "bootstrap-play-26"           % "1.8.0"                 % scope classifier "tests",
  "org.scalatest"           %% "scalatest"                   % "3.0.8"                 % scope,
  "org.jsoup"               %  "jsoup"                       % "1.12.1"                % scope,
  "com.typesafe.play"       %% "play-test"                   % current                 % scope,
  "org.pegdown"             %  "pegdown"                     % "1.6.0"                 % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play"          % "3.1.2"                 % scope,
  "uk.gov.hmrc"             %% "hmrctest"                    % "3.9.0-play-26"         % scope,
  "org.mockito"             %  "mockito-core"                % "2.28.2"                % scope,
  "com.github.tomakehurst"  %  "wiremock-jre8"               % "2.25.1"                % scope
)

lazy val appDependencies:Seq[ModuleID] = compile ++ test()

TwirlKeys.templateImports ++= Seq(
  "uk.gov.hmrc.govukfrontend.views.html.components._",
  "uk.gov.hmrc.govukfrontend.views.html.helpers._",
  "uk.gov.hmrc.hmrcfrontend.views.html.components._"
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
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .settings(
    majorVersion                     := 0,
    libraryDependencies              ++= appDependencies,
    PlayKeys.playDefaultPort         := 9166,
    scalaVersion                     := "2.12.11"
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
  .settings(resolvers += Resolver.jcenterRepo)
