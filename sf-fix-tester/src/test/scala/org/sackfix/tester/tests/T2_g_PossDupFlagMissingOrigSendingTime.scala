package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_g_PossDupFlagMissingOrigSendingTime extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    Array((FixTags.BeginString, sess.beginStr),
      (FixTags.MsgType, msgType),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgSeqNum, "" + (sess.incSeqNum)),
      (FixTags.PossDupFlag, "Y"),
      (FixTags.SendingTime, sess.tmFormatter.format(now)))
  }

  it should "PossibleDupFlag=Y but origSendingTime is missing" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // Server should send a reject in response
    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)
    val msg = sess.readNextMessageOrFail(1000, MsgTypes.Reject, "Reject")

    assert(Some(1) == msg.fldIntVal(FixTags.SessionRejectReason))

    sess.heartbeat()

    sess.logoutSequence
  }


}
