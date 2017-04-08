package org.sackfix.tester.simplefix.message

import org.sackfix.tester.simplefix.codec.SimpleCodec
import org.sackfix.tester.simplefix.codec.SimpleCodec

/** Constants - but only those needed by the tests
  */
object FixTags {
  val BeginSeqNo = 7
  val BeginString = 8
  val BodyLength = 9
  val CheckSum = 10
  val ClOrdId = 11
  val EndSeqNo = 16
  val MsgSeqNum = 34
  val MsgType = 35
  val NewSeqNo = 36
  val OrderQty = 38
  val OrdType = 40
  val PossDupFlag = 43
  val RefSeqNum = 45
  val SenderCompID = 49
  val SendingTime = 52
  val Side = 54
  val Symbol = 55
  val TargetCompID = 56
  val Text = 58
  val TransactTime = 60
  val EncryptMethod = 98
  val HeartBtInt = 108
  val TestReqID = 112
  val OrigSendingTime = 122
  val GapFillFlag = 123
  val ResetSeqNumFlag = 141
  val SessionRejectReason = 373
}

object MsgTypes {
  val Heartbeat = "0"
  val TestRequest = "1"
  val ResendRequest = "2"
  val Reject = "3"
  val SequenceReset = "4"
  val Logout = "5"
  val ExecutionReport = "8"
  val Logon = "A"
  val OrderSingle = "D"
}

/** The simple message which can calculate body length and check sum.
  * It is the minimum that was needed.
  */
object SimpleMsg {
  // This version is for your hand built outgoing messages where I calc len and checksum
  def apply(fields: Array[(Int, String)]) = new SimpleMsg(true, fields)

  // This is for an incoming message from a string where the msg is fully formed
  def apply(fixMsg: String) = new SimpleMsg(false, SimpleCodec.decode(fixMsg))
}

class SimpleMsg(val recalcLenAndChecksum: Boolean, val fields: Array[(Int, String)]) {

  var seqNum: Option[Int] = fldIntVal(FixTags.MsgSeqNum)
  var len :Int = 0
  var msgType: Option[String] = fldStrVal(FixTags.MsgType)

  def fixStr: String = {
    if (recalcLenAndChecksum) fixStrWithRecalc
    else fields.map(f=>fldStr(f)).mkString("" + SimpleCodec.SOH)
  }

  private def fixStrWithRecalc: String = {
    val msg = fields.filter(_._1 != FixTags.BeginString).filter(_._1 != FixTags.BodyLength).filter(_._1 != FixTags.CheckSum).map(f=>fldStr(f))

    // Len excludes beginStr, len and checksum
    val msgRemainder = msg.toList.mkString
    len = msgRemainder.length

    val almostFullMsg = fldStr(FixTags.BeginString).getOrElse(throw new RuntimeException("No BeginString in your message")) +
      fldStr(FixTags.BodyLength, len.toString) + msgRemainder

    // Checksum includes everything other than checksum
    almostFullMsg + fldStr(FixTags.CheckSum, calcCheckSum(almostFullMsg))
  }

  def fldStr(fld: (Int, String)): String = {
    fldStr(fld._1, fld._2)
  }

  def fldStr(tagId: Int, v: String): String = {
    s"$tagId=$v${SimpleCodec.SOH}"
  }

  def fldStr(tagId: Int): Option[String] = {
    fldStrVal(tagId) match {
      case None => None
      case Some(strVal) => Some(s"$tagId=$strVal${SimpleCodec.SOH}")
    }
  }

  def fldIntVal(tagId: Int): Option[Int] = fldStrVal(tagId).map(_.toInt)

  def fldStrVal(tagId: Int): Option[String] = {
    val retArray = fields.filter(_._1 == tagId).map(_._2)
    if (retArray.length > 0) {
      Some(retArray(0))
    } else None
  }

  def calcCheckSum(allOfMessage: String): String = {
    val totValue = allOfMessage.toCharArray.foldLeft(0)((sum, ch) => sum + ch.toInt)
    val c: Int = {
      totValue % 256
    }
    f"$c%03d"
  }
}
