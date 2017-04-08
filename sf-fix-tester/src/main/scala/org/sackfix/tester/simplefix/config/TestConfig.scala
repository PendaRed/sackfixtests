package org.sackfix.tester.simplefix.config

import com.typesafe.config.ConfigFactory
import org.sackfix.tester.simplefix.sockets.SocketHandler

/**
  * Created by Jonathan during 2017.
  *
  * The simplext fix I can do.  Purely to test the session implementations.
  * Test cases taken from the fix spec test spec doc
  */
case object TestConfig {
  val conf = ConfigFactory.load

  val config = conf.getConfig("session")

  val beginString = config.getString("BeginString")
  val senderCompID: String = config.getString("SenderCompID")
  val targetCompID: String = config.getString("TargetCompID")
  val socketAcceptAddress = config.getString("SocketAcceptAddress")
  val socketAcceptPort = config.getInt("SocketAcceptPort")
  val heartBtIntSecs = config.getInt("HeartBtIntSecs")

  def openSocket(): SocketHandler = {
    SocketHandler(socketAcceptAddress, socketAcceptPort)
  }
}
