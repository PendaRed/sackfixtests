package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T10_1_ReceiveSeqResetSeqGapFillHigherSeqNumSpec() extends SackFixTestSpec {
  behavior of "Receive SequenceReset Message"

  it should "If msgSeq>expected then gap fill" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.seqNum = 30
    sess.sequenceReset("Y", 40) //seq30

    val msg = sess.readNextMessageOrFail(2000, MsgTypes.ResendRequest, "ResendRequest")
    assert(msg.fldIntVal(FixTags.BeginSeqNo).get == 3)
    assert(msg.fldIntVal(FixTags.EndSeqNo).get == 30)

    // send a logout and expect a logout, but wierdly, this is because it wants me to replay
    // message 4, and instead I'm sending it msg31, so it tells me to logout.
    sess.logoutSequence
  }
}
