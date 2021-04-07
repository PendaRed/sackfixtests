package org.sackfix.tester.simplefix.message

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date
import org.sackfix.tester.simplefix.codec.SimpleCodec
import org.sackfix.tester.simplefix.config.TestConfig
import org.sackfix.tester.simplefix.codec.SimpleCodec
import org.sackfix.tester.simplefix.config.TestConfig
import org.sackfix.tester.simplefix.sockets.SocketHandler


/** This is a utility class to correctly generate the session level messages
  * which are needed by the tests.
  *
  * It is hacked out and messy and only useful for the test suite, please do not
  * contemplate any other use for it.
  */
class SessMessages(val startSeqNum: Int,
                   val beginStr: String = TestConfig.beginString,
                   val senderCompId: String = TestConfig.senderCompID,
                   val targetCompId: String = TestConfig.targetCompID,
                   val heartBtIntSecs: Int = TestConfig.heartBtIntSecs) {
  var seqNum :Int = startSeqNum

  val socket :SocketHandler = TestConfig.openSocket()

  val tmFormatter :DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss[.SSS]")

  // "20170101-10:26:32"
  val transactTimeFormatter = new SimpleDateFormat("YYYYMMdd-HH:mm:ss")

  def now: LocalDateTime = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime

  def incSeqNum: Int = {
    val ret = seqNum
    seqNum += 1
    ret
  }


  def getHeader(seqNum: Int, msgType: String): Array[(Int, String)] =
    Array((FixTags.BeginString, beginStr),
      (FixTags.MsgType, msgType),
      (FixTags.SenderCompID, senderCompId),
      (FixTags.TargetCompID, targetCompId),
      (FixTags.MsgSeqNum, "" + seqNum),
      (FixTags.SendingTime, tmFormatter.format(now)))

  def getHeader(msgType: String): Array[(Int, String)] =
    getHeader(incSeqNum, msgType)

  def logonMessage(heartBeatSecs: Int, resetSeqNum: Boolean, possDupFlag: Option[String] = None): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Logon) ++
      Array((FixTags.EncryptMethod, "0"),
        (FixTags.HeartBtInt, "" + heartBeatSecs),
        (FixTags.ResetSeqNumFlag, {
          if (resetSeqNum) "Y" else "N"
        }),
        (FixTags.PossDupFlag, possDupFlag.getOrElse("N")))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def logonMessage(heartBeatSecs: Int, resetSeqNum: Boolean, possDupFlag: String): Unit = {
    logonMessage(heartBeatSecs, resetSeqNum, Some(possDupFlag))
  }

  def badLogonMessage(): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Logon) ++
      Array((FixTags.EncryptMethod, "ants"),
        (FixTags.ResetSeqNumFlag, "Y"))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def logoutMessage()  :Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Logout) ++
      Array((FixTags.Text, "Test loggging out"))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def heartbeat(): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Heartbeat)
    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def heartbeat(testReqId: String): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Heartbeat) ++
      Array((FixTags.TestReqID, testReqId))
    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def testReq(testReqId: String): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.TestRequest) ++
      Array((FixTags.TestReqID, testReqId))
    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def sequenceReset(gapFillYN: String, resetSeqNum: Int): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.SequenceReset) ++
      Array((FixTags.GapFillFlag, gapFillYN),
        (FixTags.NewSeqNo, "" + resetSeqNum))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def sequenceReset(possDupFlag: String, gapFillYN: String, resetSeqNum: Int): Unit = {
    val prevTime = now.minusMinutes(5)
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.SequenceReset) ++
      Array((FixTags.PossDupFlag, possDupFlag),
        (FixTags.OrigSendingTime, tmFormatter.format(prevTime)),
        (FixTags.GapFillFlag, gapFillYN),
        (FixTags.NewSeqNo, "" + resetSeqNum))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def rejectMessage(refSeqNum: Int): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Reject) ++
      Array((FixTags.RefSeqNum, "" + refSeqNum))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def resendRequest(beginSeqNum: Int, endSeqNum: Int): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.ResendRequest) ++
      Array((FixTags.BeginSeqNo, "" + beginSeqNum),
        (FixTags.EndSeqNo, "" + endSeqNum))

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def heartbeat(seqNum: Int): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(seqNum, MsgTypes.Heartbeat)
    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def sendMessageWithNoChange(message: Array[(Int, String)]): Unit = {
    socket.send(SimpleMsg(message).fixStr)
  }

  def newOrderSingle(seqNum: Int, clOrdId: String, symbol: String, side: String, quantity: Int): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(seqNum, MsgTypes.OrderSingle) ++
      Array((FixTags.ClOrdId, clOrdId),
        (FixTags.Symbol, symbol),
        (FixTags.Side, side),
        (FixTags.TransactTime, transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "" + quantity),
        (FixTags.OrdType, "1") /*Market*/
      )

    socket.send(SimpleMsg(msgTuples).fixStr)
  }

  def newOrderSingle(clOrdId: String, symbol: String, side: String, quantity: Int): Unit = {
    newOrderSingle(incSeqNum, clOrdId, symbol, side, quantity)
  }

  def newOrderSingle(clOrdId: String): Unit = {
    newOrderSingle(incSeqNum, clOrdId, "IBM", "1" /*Buy*/ , 100)
  }

  def newOrderSingleNoHeader(header: Array[(Int, String)], clOrdId: String, symbol: String, side: String, quantity: Int): Unit = {

    val msgTuples: Array[(Int, String)] = header ++
      Array((FixTags.ClOrdId, clOrdId),
        (FixTags.Symbol, symbol),
        (FixTags.Side, side),
        (FixTags.TransactTime, transactTimeFormatter.format(new Date)),
        (FixTags.OrderQty, "" + quantity),
        (FixTags.OrdType, "1") /*Market*/
      )

    socket.send(SimpleMsg(msgTuples).fixStr)
  }


  def messageGarbledCheckSum(incrementCheckSum: Boolean = true): Unit = {
    if (incrementCheckSum) incSeqNum
    val msgTuples: Array[(Int, String)] = getHeader(seqNum, MsgTypes.Heartbeat)
    val goodStr = SimpleMsg(msgTuples).fixStr

    // garble the checksum
    socket.send(goodStr.substring(0, goodStr.length - 4) + "001" + SimpleCodec.SOH)
  }

  def messageGarbledBodyLen(): Unit = {
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Heartbeat)
    val goodStr = SimpleMsg(msgTuples).fixStr

    // garble the bodylen
    val pos = goodStr.indexOf(s"${SimpleCodec.SOH}${FixTags.BodyLength}=")
    val pos2 = goodStr.indexOf("" + SimpleCodec.SOH, pos + 3)
    val badBodyMsg = goodStr.substring(0, pos + 1) + s"${FixTags.BodyLength}=456567" + goodStr.substring(pos2)
    socket.send(badBodyMsg)
  }


  /**
    *
    * @param waitMs If >0 will Thread.sleep first
    * @return
    */
  def isSocketClosed(waitMs: Long = 0L): Boolean = {
    if (waitMs > 0) {
      Thread.sleep(waitMs)
    }
    val msgTuples: Array[(Int, String)] = getHeader(MsgTypes.Heartbeat)
    socket.isSocketClosed(SimpleMsg(msgTuples).fixStr)
  }

  def readNextMessage(timeoutMs: Long): Option[SimpleMsg] = socket.readInMsg(timeoutMs)

  def readNextMessageOrFail(timeoutMs: Long, msgType: String, msgName: String): SimpleMsg = {
    readNextMessage(timeoutMs) match {
      case Some(msg) =>
        if (msgType != msg.msgType.getOrElse("foo"))
          throw new RuntimeException(s"Expected a $msgName with msttype=$msgType, but got a ${msg.msgType}")
        else msg
      case None =>
        throw new RuntimeException(s"Expected a $msgName with msttype=$msgType, but got no reply in $timeoutMs ms")
    }
  }

  def logoutSequence() :Unit  = {
    logoutMessage()
    readNextMessageOrFail(2000, MsgTypes.Logout, "Logout")
    close()
  }

  def close(): Unit = socket.close()
}
