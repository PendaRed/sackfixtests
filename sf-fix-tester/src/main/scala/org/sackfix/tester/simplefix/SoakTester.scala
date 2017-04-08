package org.sackfix.tester.simplefix

import org.sackfix.tester.simplefix.config.TestConfig
import org.sackfix.tester.simplefix.message.{FixTags, MsgTypes, SessMessages}
import org.sackfix.tester.simplefix.message.{MsgTypes, SessMessages}

import scala.annotation.tailrec

/**
  * Created by Jonathan during 2017.
  */
object SoakTester extends App {
  val numOrds = 1000
  println(s"This is a very simple client able to send [$numOrds] orders, you can look at latencies on the server, but other than that its very simple.")
  val start = System.nanoTime()
  val o = new LotsOfOrders(1)
  val s = o.start
  o.go(s,numOrds)
  o.stop(s)
  val durationNs = System.nanoTime() - start
  println(s"Send $numOrds in $durationNs nanos  ${(durationNs.toDouble/1000000d).toInt} ms")
}

class LotsOfOrders(val startSeqNum: Int) {
  def start : SessMessages = {
    val sess = new SessMessages(startSeqNum)
    sess.logonMessage(sess.heartBtIntSecs, true) // seq1
    sess.readNextMessageOrFail(2000, MsgTypes.Logon, "Logon")
    sess
  }

  @tailrec
  final def go(sess:SessMessages, numOrds:Int):Unit = {
    sess.newOrderSingle(f"Cl${numOrds}%010d") //seq3
    sess.readNextMessage(2000) match {
      case Some(msg) =>
      case None => throw new RuntimeException(s"Failed as no reply exec with $numOrds still to send")
    }
    if (numOrds%100 ==0) println(numOrds)
    if (numOrds>0) go(sess, numOrds-1)
  }

  def stop(sess:SessMessages) {
    // send a logout and expect a logout
    sess.logoutSequence
  }
}