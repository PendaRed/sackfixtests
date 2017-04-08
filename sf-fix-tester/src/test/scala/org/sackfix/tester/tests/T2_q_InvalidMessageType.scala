package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_q_InvalidMessageType extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    Array((FixTags.BeginString, sess.beginStr),
      (FixTags.MsgType, "-1"),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgSeqNum, "" + sess.incSeqNum),
      (FixTags.SendingTime, sess.tmFormatter.format(now)))
  }


  it should "Reject invalid message type" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    /**
      * 1.	Send Reject (session-level) message referencing invalid MsgType (>= FIX 4.2: SessionRejectReason = "Invalid MsgType")
      * 2.	Increment inbound MsgSeqNum
      * 3.	Generate a "warning" condition in test output.
      */
    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    val msg = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")

    assert(Some(11) == msg.fldIntVal(FixTags.SessionRejectReason))

    // check they upped the seq num
    sess.heartbeat

    sess.logoutSequence
  }

}
