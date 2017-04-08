package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T4_a_NoHeartbeatInIntervalSpec() extends SackFixTestSpec {
  behavior of "Send heartbeat message"

  it should "If nothing sent for heartbeat then expect them to send one" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val heartbeatPlus = sess.heartBtIntSecs + 2
    println(s"Very slow test, waiting $heartbeatPlus seconds for heartbeat to arrive")
    sess.readNextMessage(heartbeatPlus * 1000) match {
      case Some(msg) =>
        assert(MsgTypes.Heartbeat == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Heartbeat message")
    }
    sess.logoutMessage()
    sess.close
  }
}
