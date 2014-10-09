package uc.dfa

import org.scalatest._

class DFASpec extends FlatSpec with Matchers {

  "A FARule" should "follow specific state and character" in {
    val rule: Rule[Int] = FARule(1, 'A', 2)
    rule.canApplyTo(1, 'A') should be (true)
    rule.canApplyTo(2, 'B') should be (false)
    rule.follow.get should be (2)
  }

  "A DFARulebook" should "search rule by character" in {
    val rulebook: Rulebook[Int] = DFARulebook(List(
      FARule(1, 'A', 2),
      FARule(2, 'B', 3),
      FARule(3, 'C', 1)
    ))
    rulebook.nextState(1, 'A').get should be (2)
    rulebook.nextState(2, 'B').get should be (3)
    rulebook.nextState(3, 'C').get should be (1)
    rulebook.nextState(3, 'A').getOrElse(0) should be (0)
  }

}