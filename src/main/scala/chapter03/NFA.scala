package uc.nfa

sealed trait Rule[A] {
  def canApplyTo(s: A, ch: Option[Char]): Boolean
  def follow: Option[A]
}
case class FARule[A](state: A, character: Option[Char], nextState: A) extends Rule[A] {
  override def canApplyTo(s: A, ch: Option[Char]): Boolean = state == s && character == ch
  override def follow: Option[A] = Some(nextState)
  override def toString: String = s"FARule<${state} -- ${character.getOrElse(' ')} --> ${nextState}>"
}
case class NoRule[A]() extends Rule[A] {
  override def toString: String = "No Rule"
  override def follow: Option[A] = None
  override def canApplyTo(s: A, ch: Option[Char]): Boolean = false
}

case class NFARulebook[A](rules: Set[Rule[A]]) {
  def nextStates(states: Set[A], character: Option[Char]): Set[A] =
    states.flatMap( followRulesFor(_, character) )
      .filter( _ != None )
      .map ( _.get )
  def followRulesFor(state: A, character: Option[Char]): Set[Option[A]] = rulesFor(state, character).map( _.follow )
  def rulesFor(state: A, character: Option[Char]): Set[Rule[A]] = rules.filter( _.canApplyTo(state, character) )
}

case class NFA[A](currentStates: Set[A], acceptStates: Set[A], rulebook: NFARulebook[A]) {
  def isAccepting: Boolean = (currentStates & acceptStates).size != 0
  def readCharacter(character: Option[Char]): NFA[A] =
    NFA(rulebook.nextStates(currentStates, character), acceptStates, rulebook)
  def readString(string: String): NFA[A] =
    if (string == "") this
    else readCharacter(Some(string.head)).readString(string.tail)
  def acceptString(string: String): Boolean = readString(string).isAccepting
}