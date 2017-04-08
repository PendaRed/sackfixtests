package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T4_b_SendThemTestReqSpec() extends SackFixTestSpec {
  behavior of "Send heartbeat message"

  it should "If send a test request expect heartbeat reply" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    sess.testReq("TestTestReq")

    sess.readNextMessage(2000) match {
      case Some(msg) =>
        assert(MsgTypes.Heartbeat == msg.msgType.getOrElse("foo"))
        msg.fldStrVal(FixTags.TestReqID) match {
          case Some(reqId) => assert(reqId == "TestTestReq")
          case None => fail("Expected a TestReqId")
        }
      case None => fail("Expected a Heartbeat message")
    }
    sess.logoutSequence
  }
}
