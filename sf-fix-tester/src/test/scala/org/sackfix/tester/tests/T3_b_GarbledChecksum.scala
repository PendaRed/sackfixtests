package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T3_b_GarbledChecksum extends SackFixTestSpec {
  behavior of "Receive Message Standard Trailer"

  it should "Garbled message ignored" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.heartbeat()

    // Now corrupt the checksum
    sess.messageGarbledCheckSum(false)

    // sends a test heartbeat
    assert(!sess.isSocketClosed(250))

    sess.logoutMessage()
    Thread.sleep(50)
    sess.close
  }
}
