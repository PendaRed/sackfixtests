package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.SessMessages

/**
  * Created by Jonathan during 2017.
  *
  * d. Invalid Logon message is received
  */
class T1B_d4_BadCompIdSpec() extends SackFixTestSpec {
  behavior of "Connect and send logon message"

  it should "Invalid Logon message is received - BadCompIds Close the socket" in {
    val sess = new SessMessages(1, "Fix4.4", "BadSenderCompId", "BadTargetCompId")
    sess.logonMessage(sess.heartBtIntSecs, true)
    assert(sess.isSocketClosed(1000))
  }
}
