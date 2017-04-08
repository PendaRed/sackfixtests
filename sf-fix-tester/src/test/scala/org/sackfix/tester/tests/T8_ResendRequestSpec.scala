package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T8_ResendRequestSpec() extends SackFixTestSpec {
  behavior of "Receive Reject Message"

  it should "Send them a valid reject message" in {
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat() // seq2
    sess.newOrderSingle("Cl001") //seq3

    var execReportSendingTime = ""
    sess.readNextMessage(2000) match {
      case Some(msg) =>
        assert(MsgTypes.ExecutionReport == msg.msgType.getOrElse("foo"))
        execReportSendingTime = msg.fldStrVal(FixTags.SendingTime).get
      case None => fail("Expected an Execution report message")
    }
    sess.heartbeat() //seq4

    // Give it time to persist the message so it can be replayed successfully
    Thread.sleep(1000)

    // 1 was login, 2 was executionreport
    sess.resendRequest(1, 2)

    sess.readNextMessage(1000) match {
      case Some(msg) =>
        assert(MsgTypes.SequenceReset == msg.msgType.getOrElse("foo"))
        assert(msg.fldStrVal(FixTags.GapFillFlag).get == "Y")
        assert(msg.fldIntVal(FixTags.NewSeqNo).get == 2)
        assert(msg.fldIntVal(FixTags.MsgSeqNum).get == 1)
      case None => fail("Expected a SequenceRest message")
    }
    sess.readNextMessage(1000) match {
      case Some(msg) =>
        assert(MsgTypes.ExecutionReport == msg.msgType.getOrElse("foo"))
        assert(msg.fldStrVal(FixTags.PossDupFlag).get == "Y")
        assert(msg.fldStrVal(FixTags.OrigSendingTime).get == execReportSendingTime)
        assert(msg.fldIntVal(FixTags.MsgSeqNum).get == 2)
      case None => fail("Expected an execution report message")
    }


    // send a logout and expect a logout
    sess.logoutSequence
  }
}
