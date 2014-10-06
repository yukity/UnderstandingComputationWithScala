package uc.simple

trait Statement {
  def isReducible: Boolean = true
  def reduce(env: Map[String, Expression]): (Statement, Map[String, Expression])
}
case class DoNothing() extends Statement {
  override def toString: String = "DoNothing"
  override def isReducible: Boolean = false
  override def reduce(env: Map[String, Expression]): (Statement, Map[String, Expression]) = (this, env)
}
case class Assign(v: Variable, expression: Expression) extends Statement {
  override def toString: String = s"${v} = ${expression}"
  override def reduce(env: Map[String, Expression]): (Statement, Map[String, Expression]) = expression match {
    case e: Expression if !(e.isReducible) => (DoNothing(), env + (v.name -> e))
    case e: Expression => (Assign(v, e.reduce(env)), env)
  }
}
case class Exp(expression: Expression) extends Statement {
  override def toString: String = s"${expression}"
  override def reduce(env: Map[String, Expression]): (Statement, Map[String, Expression]) = expression match {
    case e: Expression if !(e.isReducible) => (Exp(e), env)
    case e: Expression => (Exp(e.reduce(env)), env)
  }
  override def isReducible: Boolean = expression.isReducible
}