import scala.language.implicitConversions

ThisBuild / scalaVersion := "3.6.4"
ThisBuild / scalacOptions += "-release:17"

lazy val generateScalaRuntimeJarsMapping =
  taskKey[Seq[(File, String)]]("Generate scala runtime jars mapping.")
lazy val generateModJarsMapping =
  taskKey[Seq[(File, String)]]("Generate mod jars mapping.")
lazy val generateModInfo = taskKey[File]("Genrate mod_info.json.")
lazy val packageModZip = taskKey[File]("Package as a starsector mod(zip).")

lazy val scalaPower = project
  .in(file("."))
  .enablePlugins(UniversalPlugin)
  .settings(
    name := "starsector-scalapower",
    version := "0.1.0",
    maintainer := "ZeroDegress@zerodegress.com",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "log4j" % "log4j" % "1.2.9" % Provided,
    libraryDependencies += "com.thoughtworks.xstream" % "xstream" % "1.4.10" % Provided,
    Compile / unmanagedJars += file(
      sys.env("STARSECTOR_GAME_PATH")
    ) / "starfarer.api.jar",
    generateScalaRuntimeJarsMapping := scalaInstance.value.libraryJars
      .filter(f => f.name.contains("scala3"))
      .map(f => f -> s"lib/${f.getName}")
      .toSeq,
    generateModJarsMapping := {
      Seq((Compile / packageBin).value -> "lib/starsector-scalapower.jar")
    },
    generateModInfo := {
      val modInfoTargetFile =
        target.value / "mod-info" / "mod_info.json"
      val modInfo = ujson.read(IO.read(baseDirectory.value / "mod_info.json"))
      modInfo.obj("jars") =
        ujson.Arr.from(generateScalaRuntimeJarsMapping.value.map({
          case (_, name) =>
            name
        }) ++ generateModJarsMapping.value.map({ case (_, name) => name }))
      IO.write(modInfoTargetFile, ujson.write(modInfo))
      modInfoTargetFile
    },
    Universal / mappings ++= generateScalaRuntimeJarsMapping.value,
    Universal / mappings ++= generateModJarsMapping.value,
    Universal / mappings += generateModInfo.value -> "mod_info.json",
    packageModZip := (Universal / packageBin).value
  )
