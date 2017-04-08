package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_o_SendingTimeOut extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    val timeBefore = now.minusMinutes(3)
    Array((FixTags.BeginString, sess.beginStr),
      (FixTags.MsgType, msgType),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgSeqNum, "" + sess.incSeqNum),
      (FixTags.SendingTime, sess.tmFormatter.format(timeBefore)))
  }

  it should "check the clock is within 2 mins" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    val msg = sess.readNextMessageOrFail(2000, MsgTypes.Reject, "Reject")

    assert(Some(10) == msg.fldIntVal(FixTags.SessionRejectReason))

    sess.readNextMessageOrFail(2000, MsgTypes.Logout, "Logout")
    sess.logoutMessage()

    // Session should have been closed
    assert(sess.isSocketClosed(500))
  }

}
