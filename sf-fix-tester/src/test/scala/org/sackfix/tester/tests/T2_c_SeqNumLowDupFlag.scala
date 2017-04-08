package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T2_c_SeqNumLowDupFlag extends SackFixTestSpec {
  behavior of "Receive Message Standard Header"

  it should "logon has low seq num and poss dup flag=y, disconnect" in {
    // Have to login with reset seq num to 1
    var sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessage(5000) match {
      case Some(msg) =>
        assert(MsgTypes.Logon == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Logon response message")
    }

    // "1" = BUY
    sess.newOrderSingle("Ord1", "IBM", "1", 1000)

    sess.logoutMessage()
    Thread.sleep(50)
    sess.close

    Thread.sleep(500)

    // Now for the test - send a login with seq num=1 when should be 3 and poss dup=N
    sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, false, "N")

    // Session should have been closed
    assert(sess.isSocketClosed(500))

  }
}
