package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_d_GarbledMessage extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  it should "Garbled message ignored" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessage(5000) match {
      case Some(msg) =>
        assert(MsgTypes.Logon == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Logon response message")
    }

    sess.heartbeat()

    // Now corrupt the checksum
    sess.messageGarbledCheckSum(false)

    // sends a test heartbeat
    assert(!sess.isSocketClosed(250))

    sess.logoutMessage()
    Thread.sleep(50)
    sess.close
  }
}
