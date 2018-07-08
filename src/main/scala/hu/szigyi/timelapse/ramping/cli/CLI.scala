package hu.szigyi.timelapse.ramping.cli

import java.io.File

import sys.process._
import com.typesafe.scalalogging.LazyLogging

class CLI extends LazyLogging {

  def exec(workDir: File, cmd: Seq[String]): String = {
    logger.info(s"$$ cd $workDir && ${cmd.mkString(" ")}")
    Process(cmd, workDir) !!
  }
}

object CLI {
  def apply(): CLI = new CLI()
}
