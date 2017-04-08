package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_e_SeqNumLowDupFlagGoodTimes extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    val timeBefore = now.minusMinutes(2)
    Array((FixTags.BeginString, sess.beginStr),
      (FixTags.MsgType, msgType),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgSeqNum, "" + (sess.seqNum - 1)),
      (FixTags.PossDupFlag, "Y"),
      (FixTags.SendingTime, sess.tmFormatter.format(now)),
      (FixTags.OrigSendingTime, sess.tmFormatter.format(timeBefore)))
  }

  it should "Message has low seq num and poss dup flag=y with correct times, ignore it as dup." in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // f. PossDupFlag set to Y; OrigSendingTime specified is less than SendingTime and MsgSeqNum lower than expected
    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    sess.heartbeat()
    Thread.sleep(100)

    sess.logoutSequence
  }

}
