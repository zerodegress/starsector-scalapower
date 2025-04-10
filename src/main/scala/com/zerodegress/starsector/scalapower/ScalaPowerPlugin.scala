package com.zerodegress.starsector.scalapower

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import org.apache.log4j.Level

class ScalaPowerPlugin extends BaseModPlugin {
  override def onApplicationLoad: Unit = {
    Global
      .getLogger(classOf[ScalaPowerPlugin])
      .log(Level.INFO, "Scala Power up.");
  }
}
