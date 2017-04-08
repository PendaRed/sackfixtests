package org.sackfix.tester.tests

import java.util.Date

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  *
  * 1.	Send Reject (session-level) message referencing required tag missing (>= FIX 4.2: SessionRejectReason = "Required tag missing")
  * 2.	Increment inbound MsgSeqNum
  * 3.	Generate an "error" condition in test output.
  */
class T14_b_MissingMandTag extends SackFixTestSpec {
  behavior of "Receive application or administrative message "

  it should "Send message with missing mand tag" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val msg: Array[(Int, String)] = sess.getHeader(MsgTypes.OrderSingle) ++
      Array((FixTags.ClOrdId, "clOrdId"),
        // Exclude this mand field       (FixTags.Symbol,"symbol"),
        (FixTags.Side, "1"),
        (FixTags.TransactTime, sess.transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "100"),
        (FixTags.OrdType, "1"))

    sess.sendMessageWithNoChange(msg)

    val msg1 = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")
    assert(Some(1) == msg1.fldIntVal(FixTags.SessionRejectReason))

    // check they upped the seq num
    sess.heartbeat()

    sess.logoutSequence
  }

}
