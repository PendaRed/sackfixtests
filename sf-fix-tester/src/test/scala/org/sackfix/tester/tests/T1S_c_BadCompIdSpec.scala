package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.SessMessages

/**
  * Created by Jonathan during 2017.
  */
class T1S_c_BadCompIdSpec() extends SackFixTestSpec {
  behavior of "Receive Logon message"

  it should "Close the socket" in {
    val sess = new SessMessages(1, "Fix4.4", "BadSenderCompId", "BadTargetCompId")
    sess.logonMessage(sess.heartBtIntSecs, true)
    assert(sess.isSocketClosed(1000))
  }
}
