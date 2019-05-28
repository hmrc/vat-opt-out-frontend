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

import uk.gov.hmrc.DefaultBuildSettings.{integrationTestSettings,addTestReportOption}
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import play.core.PlayVersion.current
import sbt.Tests.{Group, SubProcess}

val appName = "vat-opt-out-frontend"

val compile = Seq(
  "uk.gov.hmrc"             %% "govuk-template"           % "5.30.0-play-25",
  "uk.gov.hmrc"             %% "play-ui"                  % "7.40.0-play-25",
  "uk.gov.hmrc"             %% "play-whitelist-filter"    % "2.0.0",
  "uk.gov.hmrc"             %% "auth-client"              % "2.22.0-play-25",
  "uk.gov.hmrc"             %% "bootstrap-play-25"        % "4.12.0",
  "uk.gov.hmrc"             %% "play-language"            % "3.4.0"
)

def test(scope:String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatest"           %% "scalatest"                   % "3.0.7"                 % scope,
  "org.jsoup"               %  "jsoup"                       % "1.11.3"                % scope,
  "com.typesafe.play"       %% "play-test"                   % current                 % scope,
  "org.pegdown"             %  "pegdown"                     % "1.6.0"                 % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play"          % "2.0.1"                 % scope,
  "uk.gov.hmrc"             %% "hmrctest"                    % "3.8.0-play-25"         % scope,
  "org.mockito"             %  "mockito-core"                % "2.7.17"                % scope,
  "com.github.tomakehurst"  %  "wiremock"                    % "2.22.0"                % scope
)

lazy val appDependencies:Seq[ModuleID] = compile ++ test()

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = tests map {
  test => Group(test.name, Seq(test), SubProcess(
    ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name, "-Dlogger.resource=logback-test.xml"))
  ))
}

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "Reverse.*",
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
    ".*feedback*.*")

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
    PlayKeys.playDefaultPort         := 9166
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
