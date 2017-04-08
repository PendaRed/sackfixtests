package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_m_GarbledBodyLen extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  it should "Bad compIds, so reject and logout" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // Server should ignore the garbled message
    sess.messageGarbledBodyLen()

    sess.readNextMessage(1000) match {
      case Some(msg) =>
        fail("Expected server to ignore garbled message, but it sent:" + msg)
      case None => // pass
    }

    // Should not have incremented the seq num
    sess.heartbeat(sess.seqNum - 1)

    sess.readNextMessage(1000) match {
      case Some(msg) =>
        fail("Expected server to accept the seq num, but it sent:" + msg)
      case None => // pass
    }

    sess.logoutSequence
  }

}
