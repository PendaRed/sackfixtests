package org.sackfix.tester.simplefix.codec

/** Very simple decoder for a fix message, with pretty much no validation.
  */
object SimpleCodec {
  val SOH :Char= 1.toChar
  val SOH_BYTE :Byte= 1.toByte
  val SOH_STR :String = "" + SOH
  val EQUALS :Char = '='

  def decode(fixStr: String): Array[(Int, String)] = {
    val fixTuples: Array[(Int, String)] = fixStr.split(SimpleCodec.SOH).map((kv: String) => {
      val s = kv.split(SimpleCodec.EQUALS)
      if (s.length < 2) {
        throw new RuntimeException(s"Badly encoded message - tag=value was [$kv]")
      }
      val v = List.range(1, s.length).map(s(_)).mkString("=")
      (s(0).toInt, v)
    })
    fixTuples
  }
}
