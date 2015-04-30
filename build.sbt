import Libraries.android._

// Using Android Plugin
android.Plugin.androidBuild

// Specifying the Android target Sdk version
platformTarget in Android := "android-22"

// Application Name
name := """ScalaAndroid"""

// Application Version
version := "1.0.0"

// Scala version
scalaVersion := "2.11.4"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-feature", "-deprecation", "-target:jvm-1.7")

// Repositories for dependencies
resolvers ++= Seq(Resolver.mavenLocal,
  DefaultMavenRepository,
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeIvyRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.defaultLocal)

libraryDependencies ++=Seq(
  aar(androidAppCompat),
  aar(androidCardView) )

// Override the run task with the android:run
run <<= run in Android

// Activate proguard for Scala
proguardScala in Android := true

// Activate proguard for Android
useProguard in Android := true

// Set proguard options
proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic")