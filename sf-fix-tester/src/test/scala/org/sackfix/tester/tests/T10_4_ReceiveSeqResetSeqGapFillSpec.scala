package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T10_4_ReceiveSeqResetSeqGapFillSpec() extends SackFixTestSpec {
  behavior of "Receive SequenceReset Message"

  it should "Reset the sequence number!" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.sequenceReset("Y", 30) // reset to seq 30

    sess.readNextMessage(2000) match {
      case Some(msg) =>
        fail("Expected Server to do the reset, and nothing else")
      case None => // pass
    }

    // send a logout and expect a logout.
    sess.seqNum = 30
    sess.logoutSequence
  }
}
