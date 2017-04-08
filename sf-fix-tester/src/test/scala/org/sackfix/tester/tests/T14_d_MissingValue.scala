package org.sackfix.tester.tests

import java.util.Date

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  *
  * d. Receive message with field identifier (tag number) specified but no value (e.g. "55=<SOH>" vs. "55=IBM<SOH>").
  *
  * 1.	Send Reject (session-level) message referencing tag specified without a value (>= FIX 4.2: SessionRejectReason = "Tag specified without a value")
  * 2.	Increment inbound MsgSeqNum
  * 3.	Generate an "error" condition in test output.
  */
class T14_d_MissingValue extends SackFixTestSpec {
  behavior of "Receive application or administrative message "

  it should "Send message with missing value" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val msg: Array[(Int, String)] = sess.getHeader(MsgTypes.OrderSingle) ++
      Array((FixTags.ClOrdId, "clOrdId"),
        (FixTags.Symbol, ""), // Missing value
        (FixTags.Side, "1"),
        (FixTags.TransactTime, sess.transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "100"),
        (FixTags.OrdType, "1"))

    sess.sendMessageWithNoChange(msg)

    val msg1 = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")
    assert(Some(4) == msg1.fldIntVal(FixTags.SessionRejectReason))

    // check they upped the seq num
    sess.heartbeat()

    sess.logoutSequence
  }

}
