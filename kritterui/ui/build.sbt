name := "kritter"

version := "1-0-SNAPSHOT"

play.Project.playJavaSettings

organization := "com.kritter"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.typesafe.play"       %% "play-jdbc"                   % "2.2.0" withSources,
  "com.typesafe.play"       %% "play-json"                   % "2.2.0" withSources,
  "ws.securesocial" 		%% "securesocial" 				 % "2.1.3" withSources,
  "be.objectify" 			%% "deadbolt-java" 				 % "2.2-RC4" withSources, 
  "org.codehaus.jackson" % "jackson-core-asl" 		 		 % "1.9.13" withSources, 
  "com.google.guava" % "guava-io" 		 		 % "r03", 
  "mysql" % "mysql-connector-java" % "5.1.18",
  "net.coobird" % "thumbnailator" % "0.4.7",
  "com.kritter.geo"  % "utils" % "1.0.0",
  "com.kritter.kritterui.api.def"  % "kritter_kritterui_api_def" % "1.0.0",
  "com.kritter.kritterui.api.upload_to_cdn"  % "kritter_kritterui_api_upload_to_cdn" % "1.0.0",
  "com.kritter.thrift"  % "structs" % "1.0.0",
  "com.kritter.utils.amazon_s3_upload"  % "kritter_utils_amazon_s3_upload" % "1.0.0"
)


resolvers ++= Seq(
  Resolver.file("Local Maven Repository", file("~/.m2/repository/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("SecureSocial Repository", url("http://securesocial.ws/repository/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns)
)
