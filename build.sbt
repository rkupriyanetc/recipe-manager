import sbt._
import Keys._
import play.Project._
import com.typesafe.config._

name := "recipe-manager"

version := "1.0-SNAPSHOT"

resolvers ++= Seq(
  Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  javaCore,
  "com.feth" %% "play-authenticate"    % "0.5.2-SNAPSHOT",
	"mysql" 	 %  "mysql-connector-java" % "5.1.29",
  javaEbean,
  cache
)     

playJavaSettings