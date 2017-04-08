package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  *
  * c. Valid Logon message as response is received
  */
class T1B_c_ExpectLoginResponseSpec() extends SackFixTestSpec {
  behavior of "Connect and send logon message"

  it should "Valid Logon message as response is received" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")
    sess.close
  }
}
