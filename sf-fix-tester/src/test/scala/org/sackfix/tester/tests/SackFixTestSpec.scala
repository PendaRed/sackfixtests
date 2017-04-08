package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.sockets.SocketHandler
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

/**
  * Created by Jonathan during 2017.
  */
class SackFixTestSpec extends FlatSpec with BeforeAndAfterEach {
  override protected def beforeEach = {
  }

  override protected def afterEach = {
    SocketHandler.tidyUpTest
  }
}
