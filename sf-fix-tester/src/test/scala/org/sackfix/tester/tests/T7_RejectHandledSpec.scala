package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T7_RejectHandledSpec() extends SackFixTestSpec {
  behavior of "Receive Reject Message"

  it should "Send them a valid reject message" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.testReq("Boo")

    //println("Expect a heartbeat")
    sess.readNextMessage(1000) match {
      case Some(msg) =>
        assert(MsgTypes.Heartbeat == msg.msgType.getOrElse("foo"))
        sess.rejectMessage(msg.fldIntVal(FixTags.MsgSeqNum).get)
      case None => fail("Expected a Heartbeat message")
    }

    // send a logout and expect a logout
    sess.logoutSequence
  }
}
