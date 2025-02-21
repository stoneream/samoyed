package utils.security

import java.security.{MessageDigest, SecureRandom}
import java.util.Base64
import scala.util.Random

object TokenGenerator {
  private val secureRandom = new Random(new SecureRandom())

  /**
   * code verifier, code challengeを生成する
   * https://datatracker.ietf.org/doc/html/rfc7636#section-4.1
   *
   * @return
   */
  def makeCodeChallenge(): CodeChallengeSet = {
    val codeVerifier = secureRandom.alphanumeric.take(62).mkString("")
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val sha256Digest = messageDigest.digest(codeVerifier.getBytes("US-ASCII"))
    val codeChallenge = Base64.getUrlEncoder.withoutPadding.encodeToString(sha256Digest)
    CodeChallengeSet(codeChallenge, codeVerifier)
  }

  def generate(seed: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update((seed + secureRandom.nextLong()).getBytes())
    md.digest().map("%02x".format(_)).mkString
  }

  def digest(value: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(value.getBytes())
    md.digest().map("%02x".format(_)).mkString
  }

  case class CodeChallengeSet(codeChallenge: String, codeVerifier: String)
}
