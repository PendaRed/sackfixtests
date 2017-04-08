package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_f_SeqNumLowDupFlagBadTimes extends SackFixTestSpec {
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
      (FixTags.SendingTime, sess.tmFormatter.format(timeBefore)),
      (FixTags.OrigSendingTime, sess.tmFormatter.format(now)))
  }

  it should "a message has low seq num and poss dup flag=y, and origsendtime>send time, disconnect" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2012, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // f. PossDupFlag set to Y; OrigSendingTime specified is greater than SendingTime and MsgSeqNum lower than expected
    //
    // Note: OrigSendingTime should be earlier than SendingTime unless the message is being resent within the same
    // second during which it was sent
    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    sess.readNextMessageOrFail(2013, MsgTypes.Logout, "Logout")
    sess.logoutMessage()

    // Session should have been closed
    assert(sess.isSocketClosed(500))
  }

  it should "logon has low seq num and poss dup flag=y, disconnect and close socket after 2 secs" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // f. PossDupFlag set to Y; OrigSendingTime specified is greater than SendingTime and MsgSeqNum lower than expected
    //
    // Note: OrigSendingTime should be earlier than SendingTime unless the message is being resent within the same
    // second during which it was sent
    val header: Array[(Int, String)] = genHeader(sess, MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)
    sess.readNextMessageOrFail(2000, MsgTypes.Logout, "Logout")

    // DO NOT REPLY LOGOUT - server should still close socket - after a 2 second wait

    // Session should have been closed
    assert(sess.isSocketClosed(2200))
  }

}
