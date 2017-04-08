package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T1S_a_LoginResendRequestSpec() extends SackFixTestSpec {
  behavior of "Receive Logon message"

  it should "Receive a logon and a resend request" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")
    sess.logoutSequence

    // send login with seqnum 1000
    sess = new SessMessages(1000)
    sess.logonMessage(sess.heartBtIntSecs, false)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val msg = sess.readNextMessageOrFail(2000, MsgTypes.ResendRequest, "ResendRequest")
    assert(Some(3) == msg.fldIntVal(FixTags.BeginSeqNo))
    assert(Some(1000) == msg.fldIntVal(FixTags.EndSeqNo))

    sess.logoutSequence
  }
}
