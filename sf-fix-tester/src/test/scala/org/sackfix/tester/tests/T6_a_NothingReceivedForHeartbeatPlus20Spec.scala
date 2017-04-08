package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}

/**
  * Created by Jonathan during 2017.
  */
class T6_a_NothingReceivedForHeartbeatPlus20Spec() extends SackFixTestSpec {
  behavior of "Send test request"

  it should "No data received during heartbeat interval" in {
    val sess = new SessMessages(1)
    sess.logonMessage(sess.heartBtIntSecs, true)
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")

    val heartbeatPlus = sess.heartBtIntSecs + 2
    println(s"Very slow test, waiting $heartbeatPlus seconds for heartbeat to arrive")
    sess.readNextMessage(heartbeatPlus * 1000) match {
      case Some(msg) =>
        assert(MsgTypes.Heartbeat == msg.msgType.getOrElse("foo"))
      case None => fail("Expected a Heartbeat message")
    }
    val twentyPerscent = (sess.heartBtIntSecs.toDouble * 0.2).toInt + 2
    println(s"Very slow test, waiting another $twentyPerscent seconds for testreq to arrive")
    sess.readNextMessage(twentyPerscent * 1000) match {
      case Some(msg) =>
        assert(MsgTypes.TestRequest == msg.msgType.getOrElse("foo"))
        assert(msg.fldStrVal(FixTags.TestReqID).isDefined)
        sess.heartbeat(msg.fldStrVal(FixTags.TestReqID).get)
      case None => fail("Expected a TestRequest message")
    }
    sess.logoutSequence
  }
}
