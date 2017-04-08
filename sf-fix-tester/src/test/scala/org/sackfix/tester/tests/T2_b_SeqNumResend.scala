package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_b_SeqNumResend extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  it should "accept first message and then resend request" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessage(5000) match {
      case Some(msg) =>
        assert(MsgTypes.Logon == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Logon response message")
    }

    sess.heartbeat()

    // send in another with seqNum of 100
    sess.heartbeat(100)

    sess.readNextMessage(5000) match {
      case Some(msg) =>
        assert(MsgTypes.ResendRequest == msg.msgType.getOrElse("foo"))
        assert(Some(3) == msg.fldIntVal(FixTags.BeginSeqNo))
        assert(Some(100) == msg.fldIntVal(FixTags.EndSeqNo))
      case None => fail("Expected a resend response message")
    }
    sess.close
  }
}
