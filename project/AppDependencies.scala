import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30" % "8.3.0",
    "uk.gov.hmrc"       %% "internal-auth-client-play-30"   % "1.9.0",
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test,
    "org.scalatestplus"       %% "scalacheck-1-15"        % "3.2.10.0"              % Test
  )

  val it = Seq.empty
}
