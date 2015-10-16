name := "frontend-js"

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node
resourceGenerators in Compile <+=
  (resourceManaged, baseDirectory, tsk) map { (rs, bd, _) =>
    val tgt = rs / "whut.js"
    IO.copy(Seq(bd / "build" / "whut.js" -> tgt), overwrite = true)
    Seq(tgt)
  }

lazy val tsk = TaskKey[Unit]("tsk")

tsk := {
  if ( util.Properties.isWin ) {
    Process(command = "cmd /c npm install gulp", cwd = baseDirectory.value).run()
    Process(command = "cmd /c npm install", cwd = baseDirectory.value).run()
    Process(command = "cmd /c gulp build", cwd = baseDirectory.value).run()
  } else {
    Process(command = "npm install gulp", cwd = baseDirectory.value).run()
    Process(command = "npm install", cwd = baseDirectory.value).run()
    Process(command = "gulp build", cwd = baseDirectory.value).run()
  }
}