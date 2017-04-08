package org.sackfix.tester.tests

import java.time.LocalDateTime

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_i_BadBeginString extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  def genHeader(sess: SessMessages, beginStr: String, msgType: String): Array[(Int, String)] = {
    val now = LocalDateTime.now()
    Array((FixTags.BeginString, beginStr),
      (FixTags.MsgType, msgType),
      (FixTags.SenderCompID, sess.senderCompId),
      (FixTags.TargetCompID, sess.targetCompId),
      (FixTags.MsgSeqNum, "" + (sess.incSeqNum)),
      (FixTags.SendingTime, sess.tmFormatter.format(now)))
  }

  it should "Bad begin Str, so logout" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // Server should send a reject in response
    val header: Array[(Int, String)] = genHeader(sess, "SmallGods", MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    sess.readNextMessageOrFail(2000, MsgTypes.Logout, "Logout")

    sess.logoutMessage()

    // Session should have been closed
    assert(sess.isSocketClosed(500))
  }

  it should "Bad begin Str and close socket after 2 secs" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // Server should send a reject in response
    val header: Array[(Int, String)] = genHeader(sess, "SmallGods", MsgTypes.Heartbeat)
    sess.sendMessageWithNoChange(header)

    sess.readNextMessageOrFail(2000, MsgTypes.Logout, "Logout")

    // DO NOT REPLY LOGOUT - server should still close socket - after a 2 second wait

    // Session should have been closed
    assert(sess.isSocketClosed(2200))
  }


}
