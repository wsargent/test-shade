package example

/**
 * Handles a server-side channel.
 */
class Main extends App {

  val foo = play.api.libs.ws.ahc.netty.handler.codec.http.cookie.ClientCookieEncoder.encode("JSESSIONID", "1234")
}