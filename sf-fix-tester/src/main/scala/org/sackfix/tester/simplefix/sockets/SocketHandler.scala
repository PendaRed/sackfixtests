package org.sackfix.tester.simplefix.sockets

import java.io.{DataInputStream, DataOutputStream, EOFException, IOException}
import java.net.{ConnectException, Socket}

import org.sackfix.tester.simplefix.codec.SimpleCodec
import org.sackfix.tester.simplefix.message.{FixTags, SimpleMsg}
import org.sackfix.tester.simplefix.codec.SimpleCodec
import org.sackfix.tester.simplefix.message.{FixTags, SimpleMsg}
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by Jonathan during 2017.
  *
  * Deliberately keeping things simple, this is for testing, so no NIO, no akka, netty or grizzly etc
  *
  * As for this revolting singleton holding state and synchronization - its testing,  don't care.
  */
object SocketHandler {
  private val socketsOpened = mutable.ArrayBuffer.empty[SocketHandler]

  def addSocket(socket:SocketHandler) : Unit = synchronized {
    socketsOpened += socket
  }
  def tidyUpTest() : Unit = synchronized {
    if (socketsOpened.nonEmpty) {
      // println(s"Closing ${socketsOpened} sockets")
      socketsOpened.foreach( s => try {
        s.close()
      } catch {
        case ex:Exception=>
      })
      socketsOpened.clear()
      Thread.sleep(100)
    }
  }
}

case class SocketHandler(serverAddress: String, port: Int) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val socket = openSocket
  private val dos = new DataOutputStream(socket.getOutputStream)
  private val din = new DataInputStream(socket.getInputStream)

  private def openSocket: Socket = {
    try {
      logger.info(s"Opening socket to $serverAddress:$port")
      val ret = new Socket(serverAddress, port)
      SocketHandler.addSocket(this)
      ret
    } catch {
      case ex: ConnectException =>
        val msg = s"Could not connect to server [$serverAddress:$port], please check your config in resources/applicatrion.conf, tests terminating"
        logger.error(msg)
        println(msg)
        System.exit(1)
        throw ex // stupid intellij doesnt know about system.exit
    }
  }

  def send(fixStr: String) :Unit = {
    dos.writeBytes(fixStr)
    if (logger.isInfoEnabled()) logger.info("OUT " + fixStr)
  }

  /**
    * Has to try and send a heartbeat to see if the socket is closed.
    */
  def isSocketClosed(fixTestHeartbeat: String): Boolean = {
    // Sadly the isBound, isClosed and isConnected calls only say true if you called them to close etc
    // you have to send something and get a -1.
    try {
      send(fixTestHeartbeat)
      false
    } catch {
      case ex: IOException =>
        logger.info("Detected socket closed:" + ex.getMessage)
        true
      case ex: Throwable => logger.error("Unexpected exception", ex)
        true
    }
  }

  def close(): Unit = {
    socket.close()
  }

  /**
    * @return None if it timesout or IOException
    */
  def readInMsg(timeoutMs: Long): Option[SimpleMsg] = {
    try {
      readTilChecksum(timeoutMs, "", 0) match {
        case Some(fixStr) =>
          if (logger.isInfoEnabled()) logger.info("IN  " + fixStr)
          Some(SimpleMsg(fixStr))
        case None => None
      }
    } catch {
      case ex: IOException =>
        logger.error("Got IOException when reading the next fix message, so dropping all the bytes cos this is a simple impl")
        None
    }
  }

  /**
    * @return None if it timesout
    */
  @tailrec
  private def readTilChecksum(timeoutLeftMs: Long, msgSoFar: String, lastTuplePos: Int): Option[String] = {
    //     println(msgSoFar)
    val start = System.currentTimeMillis()
    readByte(timeoutLeftMs) match {
      case None => None
      case Some(b) if b == SimpleCodec.SOH_BYTE =>
        val kv = msgSoFar.substring(lastTuplePos).split(SimpleCodec.EQUALS)
        if (kv.length >= 2) {
          if (kv(0) == FixTags.CheckSum.toString) {
            val ret: String = msgSoFar + SimpleCodec.SOH_STR
            Some(ret)
          } else {
            readTilChecksum(timeoutLeftMs - (System.currentTimeMillis() - start),
              msgSoFar + SimpleCodec.SOH_STR, msgSoFar.length + 1)
          }
        } else {
          throw new RuntimeException(s"Failed to decode, as bad keyvalue pair [${kv.mkString(",")}]")
        }
      case Some(b) =>
        readTilChecksum(timeoutLeftMs - (System.currentTimeMillis() - start), msgSoFar + b.toChar, lastTuplePos)
    }
  }

  // V slow, but its a test so do not care
  @tailrec
  private def readByte(timeoutLeftMs: Long): Option[Byte] = {
    val start = System.currentTimeMillis()
    // println(timeoutLeftMs)
    if (din.available() > 0) {
      try {
        Some(din.readByte())
      } catch {
        case ex: EOFException =>
          None
      }
    } else {
      if (timeoutLeftMs < 50) None
      else {
        Thread.sleep(50)
        readByte(timeoutLeftMs - (System.currentTimeMillis() - start))
      }
    }
  }
}