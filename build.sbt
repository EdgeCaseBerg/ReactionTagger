import play.PlayImport.PlayKeys.routesImport
import scalariform.formatter.preferences._

organization := "com.github.edgecaseberg"

name := "reaction-tagger"

version := "0.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

autoAPIMappings := true // Allow scaladoc to grab documentation as neccesary

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

libraryDependencies ++= Seq(
	"org.mockito" % "mockito-all" % "1.10.+",
	"org.scalatest" %% "scalatest" % "3.0.0",
	"com.google.inject" % "guice" % "4.1.0"
)

scalariformPreferences := scalariformPreferences.value
	.setPreference(DoubleIndentClassDeclaration, true)
	.setPreference(PreserveDanglingCloseParenthesis, true)
	.setPreference(AlignParameters, false)
	.setPreference(IndentWithTabs, true)
	.setPreference(MultilineScaladocCommentsStartOnFirstLine, true)

scalacOptions ++= Seq("-feature")

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"