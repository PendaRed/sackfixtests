package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages, SimpleMsg}

/**
  * Created by Jonathan during 2017.
  */
class T3_e_GarbledChecksumBadLen extends SackFixTestSpec {
  behavior of "Receive Message Standard Trailer"

  it should "Garbled checsum of 2 chars ignored" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")


    // Now corrupt the checksum
    val msgTuples: Array[(Int, String)] = sess.getHeader(sess.seqNum, MsgTypes.Heartbeat)
    val goodStr = SimpleMsg(msgTuples).fixStr
    val checksumTooShort = goodStr.substring(0, goodStr.length - 4) + goodStr.substring(goodStr.length - 3)

    // garble the checksum
    sess.socket.send(checksumTooShort)

    Thread.sleep(250)
    // sends a test heartbeat
    assert(!sess.isSocketClosed(250))

    sess.logoutMessage()
    Thread.sleep(50)
    sess.close
  }
}
