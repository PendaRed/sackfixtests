package org.sackfix.tester.tests

import java.util.Date

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  *
  * f. Receive message with a value in an incorrect data format (syntax) for a particular field identifier (tag number).
  *
  * 1.	Send Reject (session-level) message referencing value is in an incorrect data format for this tag (>= FIX 4.2: SessionRejectReason = "Incorrect data format for value")
  * 2.	Increment inbound MsgSeqNum
  * 3.	Generate an "error" condition in test output.
  */
class T14_f_ValueBadFormat extends SackFixTestSpec {
  behavior of "Receive application or administrative message "

  it should "Send message with missing value" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val msg: Array[(Int, String)] = sess.getHeader(MsgTypes.OrderSingle) ++
      Array((FixTags.ClOrdId, "clOrdId"),
        (FixTags.Symbol, "ici"), // Missing value
        (FixTags.Side, "1"),
        (FixTags.TransactTime, sess.transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "Can I be a number, no"),
        (FixTags.OrdType, "1"))

    sess.sendMessageWithNoChange(msg)

    val msg1 = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")
    assert(Some(6) == msg1.fldIntVal(FixTags.SessionRejectReason))

    // check they upped the seq num
    sess.heartbeat()

    sess.logoutSequence
  }

}
