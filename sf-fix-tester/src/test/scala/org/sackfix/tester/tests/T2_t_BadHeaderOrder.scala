package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_t_BadHeaderOrder extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, seqNum: Int, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    Array((FixTags.BeginString, sess.beginStr),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgType, msgType),
      (FixTags.MsgSeqNum, "" + seqNum),
      (FixTags.SendingTime, sess.tmFormatter.format(now)))
  }


  it should "Reject a message where first three tags are not as expected" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    /**
      * 1.	Consider garbled and ignore message  (do not increment inbound MsgSeqNum) and continue accepting messages
      * 2.	Generate a "warning" condition in test output.
      */
    val header: Array[(Int, String)] = genHeader(sess, sess.seqNum, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    // check they did NOT up the seq num
    sess.heartbeat

    sess.logoutSequence
  }

}
