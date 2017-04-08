package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T10_3_ReceiveSeqResetSeqGapFillLowerSpec() extends SackFixTestSpec {
  behavior of "Receive SequenceReset Message"

  it should "If msgSeq<expected and NOT poss dup disconnect" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.seqNum = 1
    sess.sequenceReset("Y", 1) // reset to seq 1, but this is seq3

    sess.readNextMessage(2000) match {
      case Some(msg) =>
        fail("Expected Server to ignore a poss dup which tried to reset to lower number")
      case None => // pass
    }
    // Session should have been closed
    assert(sess.isSocketClosed(500))
  }
}
