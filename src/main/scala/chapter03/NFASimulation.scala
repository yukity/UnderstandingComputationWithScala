package uc.nfasimulation

import uc.nfa._
import uc.dfa._
import uc.rule._

case class NFASimulation[A](nfa: NFA[A]) {
  def nextState(states: Set[A], character: Option[Char]): Set[A] =
    NFA(states, nfa.acceptStates, nfa.rulebook)
      .readCharacter(character)
      .currentStates
  def rulesFor(state: Set[A]): Set[Rule[Set[A]]] =
    nfa.rulebook.alphabet.map( (c) => FARule(state, Some(c), nextState(state, Some(c))) )
  def discoverStatesAndRules(states: Set[Set[A]]): (Set[Set[A]], Set[Rule[Set[A]]]) = {
    val rules: Set[Rule[Set[A]]] = states.flatMap( rulesFor(_) )
    val moreStates: Set[Set[A]] = rules.map( _.follow ).filter( _ != None).map( _.get )
    if (moreStates.subsetOf(states)) (states, rules)
    else discoverStatesAndRules(states | moreStates)
  }
  def toDFA: DFA[Set[A]] = {
    val startState: Set[A] = nfa.currentStates
    val discoveredStatesAndRules: (Set[Set[A]], Set[Rule[Set[A]]]) = discoverStatesAndRules(Set(startState))
    val states: Set[Set[A]] = discoveredStatesAndRules._1
    val rules: Set[Rule[Set[A]]] = discoveredStatesAndRules._2
    val acceptStates: Set[Set[A]] = states.filter( (state) => NFA(state, nfa.acceptStates, nfa.rulebook).isAccepting )
    DFA(startState, acceptStates, DFARulebook(rules))
  }
}