package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T1S_d_BadLoginMsgSpec() extends SackFixTestSpec {
  behavior of "Receive Logon message"

  it should "send a logout and close socket" in {
    val sess = new SessMessages(1)
    sess.badLogonMessage()

    // Can optionally get a reject, and then get a logout and a socket close
    sess.readNextMessage(5000) match {
      case Some(msg) =>
        if (MsgTypes.Reject == msg.msgType.getOrElse("foo")) {
          sess.readNextMessage(5000) match {
            case Some(msg) => assert(MsgTypes.Logout == msg.msgType.getOrElse(MsgTypes.Heartbeat))
            case None => fail("Expected a Logout response message")
          }
        } else {
          assert(MsgTypes.Logout == msg.msgType.getOrElse("foo"))
        }
      case None => fail("Expected a Logout response message")
    }

    assert(sess.isSocketClosed(1000))
  }
}
