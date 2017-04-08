package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T11_c_ReceiveSeqResetSeqTooLowSpec() extends SackFixTestSpec {
  behavior of "Receive SequenceReset Message"

  it should "Reset the sequence number!" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.sequenceReset("N", 1) // seq3 reset to seq 1

    sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")

    sess.seqNum = 3
    sess.heartbeat() // seq3
    sess.readNextMessage(1000) match {
      case Some(msg) =>
        fail("Expected Server to ignore the heartbeat as its seq was correct")
      case None => // pass
    }

    // send a logout and expect a logout.
    sess.logoutSequence
  }
}
