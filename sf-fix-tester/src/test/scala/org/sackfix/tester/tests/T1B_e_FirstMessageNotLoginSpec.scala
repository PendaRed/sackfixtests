package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.SessMessages

/**
  * Created by Jonathan during 2017.
  */
class T1B_e_FirstMessageNotLoginSpec() extends SackFixTestSpec {
  behavior of "Connect and send logon message"

  it should "Receive any message other than a Logon message" in {
    val sess = new SessMessages(1)
    sess.heartbeat()
    assert(sess.isSocketClosed(1000))
  }
}
