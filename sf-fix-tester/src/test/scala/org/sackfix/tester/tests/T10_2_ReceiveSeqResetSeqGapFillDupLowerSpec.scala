package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T10_2_ReceiveSeqResetSeqGapFillDupLowerSpec() extends SackFixTestSpec {
  behavior of "Receive SequenceReset Message"

  it should "If msgSeq<expected and poss dup ignore" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.seqNum = 1
    sess.sequenceReset("Y", "Y", 1) //possdup=Y, reset to seq 40, but this is seq1

    sess.readNextMessage(2000) match {
      case Some(msg) =>
        fail("Expected Server to ignore a poss dup which tried to reset to lower number")
      case None => // pass
    }

    sess.seqNum = 3
    // send a logout and expect a logout, but wierdly, this is because it wants me to replay
    // message 4, and instead I'm sending it msg40, so it tells me to logout.
    sess.logoutSequence
  }
}
