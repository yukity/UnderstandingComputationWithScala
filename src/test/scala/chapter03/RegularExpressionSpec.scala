package uc.regularexpression

import org.scalatest._

class RegularExpressionSpec extends FlatSpec with Matchers {

  "Pattern" should "make AST of regular-expression" in {
    val pattern: Pattern = Repeat(
      Choose(
        Concatenate(Literal('a'), Literal('b')),
        Literal('a')
      )
    )
    pattern.inspect should be ("/(ab|a)*/")
  }

  "Empty" should "matches ''" in {
    val pattern: Pattern = Empty()
    pattern.matches("a") should be (false)
    pattern.matches("") should be (true)
  }

  "Literal" should "matches a single ascii" in {
    val pattern: Pattern = Literal('a')
    pattern.matches("a") should be (true)
    pattern.matches("aa") should be (false)
    pattern.matches("") should be (false)
    pattern.matches("b") should be (false)
  }

  "Concatenate" should "matches sequence of two Patterns" in {
    val pattern: Pattern = Concatenate(
      Literal('a'),
      Concatenate(Literal('b'), Literal('c'))
    )
    pattern.matches("a") should be (false)
    pattern.matches("ab") should be (false)
    pattern.matches("abc") should be (true)
    pattern.matches("abca") should be (false)
    val pattern2: Pattern = Concatenate(Literal('a'), Empty())
    pattern2.matches("a") should be (true)
    pattern2.matches("") should be (false)
  }

}