import sbt._
import Keys._

object HmrcBuild extends Build {

  import uk.gov.hmrc.DefaultBuildSettings
  import DefaultBuildSettings._
  import BuildDependencies._
  import uk.gov.hmrc.{SbtBuildInfo, ShellPrompt}

  val appName = "car-tax-calculator"
  val appVersion = "0.3.0"

  lazy val carTaxCalculator = Project(appName, file("."))
    .settings(version := appVersion)
    .settings(scalaSettings : _*)
    .settings(defaultSettings() : _*)
    .settings(
      targetJvm := "jvm-1.7",
      shellPrompt := ShellPrompt(appVersion),
      libraryDependencies ++= AppDependencies(),
      resolvers := Seq(
        Opts.resolver.sonatypeReleases,
        Opts.resolver.sonatypeSnapshots,
      "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/",
      "typesafe-snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
      ),
      crossScalaVersions := Seq("2.11.5")
    )
    .settings(ScoverageSettings(): _*)
    .settings(SbtBuildInfo(): _*)
    .settings(SonatypeBuild(): _*)

}

private object AppDependencies {

  import play.core.PlayVersion
  import play.PlayImport._

  val compile = Seq(
      // FIXME: this is only used as the play.api.Logger is used.
      // Just reference slf4j or alternative you don't need all of play pulled in just for logging
      "com.typesafe.play" %% "play" % PlayVersion.current % "provided",
      json % "provided",
      "uk.gov.hmrc" %% "time" % "1.1.0"
    )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test: Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % "2.2.1" % scope,
        "org.pegdown" % "pegdown" % "1.4.2" % scope
      )
    }.test
  }

  def apply() = compile ++ Test()
}

object SonatypeBuild {

  import xerial.sbt.Sonatype._

  def apply() = {
    sonatypeSettings ++ Seq(
      pomExtra := (<url>https://www.gov.uk/government/organisations/hm-revenue-customs</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          </license>
        </licenses>
        <scm>
          <connection>scm:git@github.com:hmrc/car-tax-calculator.git</connection>
          <developerConnection>scm:git@github.com:hmrc/car-tax-calculator.git</developerConnection>
          <url>git@github.com:hmrc/car-tax-calculator.git</url>
        </scm>
        <developers>
          <developer>
            <id>ganeshkumarv10</id>
            <name>Ganesh Kumar</name>
            <url>http://www.accenture.com</url>
          </developer>
        </developers>)
    )
  }
}

object ScoverageSettings {
  import scoverage.ScoverageSbtPlugin.ScoverageKeys._

  def apply() = Seq(
    coverageExcludedPackages := "<empty>;Reverse.*;app.Routes.*;uk.gov.hmrc;testOnlyDoNotUseInAppConf;.src_managed.*;prod;com.kenshoo.play.*",
    coverageMinimum := 80,
    coverageFailOnMinimum := false,
    coverageHighlighting := true
  )
}

