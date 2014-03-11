import sbt._
import Keys._
import play.Project._
import com.typesafe.config._

name := "recipe-manager"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
	"mysql" 	 %  "mysql-connector-java" % "5.1.29",
  javaEbean,
  cache
)     

playJavaSettings