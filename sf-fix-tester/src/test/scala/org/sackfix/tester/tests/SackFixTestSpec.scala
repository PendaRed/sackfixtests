package org.sackfix.tester.tests

import org.sackfix.tester.simplefix.sockets.SocketHandler
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Created by Jonathan during 2017.
  */
class SackFixTestSpec extends AnyFlatSpec with BeforeAndAfterEach {
  override protected def beforeEach() = {
  }

  override protected def afterEach() = {
    SocketHandler.tidyUpTest
  }
}
