package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T13_b_LogoutFromClient() extends SackFixTestSpec {
  behavior of "Receive Logout message"

  it should "ClientLogoutSequence" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2

    // send a logout and expect a logout.
    sess.logoutMessage()
    sess.readNextMessage(1000) match {
      case Some(msg) =>
        assert(MsgTypes.Logout == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Logout message")
    }

    // Wait for 10 seconds and the server should close the socket...
    // Session should have been closed
    assert(sess.isSocketClosed(10200))
  }
}
