package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T1S_b_LoginTwiceSpec() extends SackFixTestSpec {
  behavior of "Receive Logon message"

  it should "2nd Logon should just get socket closed" in {
    // Have to login with reset seq num to 1
    var sess1 = new SessMessages(1)
    sess1.logonMessage(30, true)
    Thread.sleep(30)

    // send login again
    var sess2 = new SessMessages(1)
    sess2.logonMessage(30, true)

    sess1.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")
    // Session 2 should have been closed
    assert(sess2.isSocketClosed(1000))

    Thread.sleep(1000)
    sess1.heartbeat()
    sess1.close

  }
}
