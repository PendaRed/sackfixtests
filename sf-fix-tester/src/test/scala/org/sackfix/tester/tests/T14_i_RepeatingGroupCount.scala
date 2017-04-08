package org.sackfix.tester.tests

import java.util.Date

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  *
  * i. Receive a message with repeating groups in which the "count" field value for a repeating group is incorrect.
  *
  * 1.	Send Reject (session-level) message referencing the incorrect "count" field identifier (tag number)
  * 2.	Increment inbound MsgSeqNum
  * 3.	Generate an "error" condition in test output.
  */
class T14_i_RepeatingGroupCount extends SackFixTestSpec {
  behavior of "Receive application or administrative message "

  it should "Send message with bad repeating group count" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val msg: Array[(Int, String)] = sess.getHeader(MsgTypes.OrderSingle) ++
      Array((FixTags.ClOrdId, "clOrdId"),
        (FixTags.Symbol, "ici"), // Missing value
        (FixTags.Side, "1"),
        (FixTags.TransactTime, sess.transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "100"),
        (FixTags.OrdType, "1"),
        (78, "3"), //NoAllocsField
        (79, "Acc1"), //AllocAccount
        (661, "111"), //AllocAcctIDSource
        (736, "GBP"), //AllocSettlCurrency
        (467, "Alloc1"), //IndividualAllocID
        (80, "10"), //AllocQty
        (79, "Acc2"),
        (661, "222"),
        (736, "GBP"),
        (467, "Alloc2"),
        (80, "11"))

    sess.sendMessageWithNoChange(msg)

    val msg1 = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")
    // IncorrectNumingroupCountForRepeatingGroup=16
    assert(Some(16) == msg1.fldIntVal(FixTags.SessionRejectReason))

    // check they upped the seq num
    sess.heartbeat()

    sess.logoutSequence
  }

}
