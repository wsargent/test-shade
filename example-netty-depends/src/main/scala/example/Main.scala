package example

import example.netty.handler.codec.http.cookie.ClientCookieEncoder

/**
 * Handles a server-side channel.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val foo = ClientCookieEncoder.STRICT.encode("JSESSIONID", "1234")
    println(s"encoded string is $foo")
    println(s"ClientCookieEncoder class is ${classOf[ClientCookieEncoder].getName}")
  }

}