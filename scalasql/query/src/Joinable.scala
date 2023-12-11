package scalasql.query

import scalasql.core.{Context, Expr, JoinNullable, WithSqlExpr}

/**
 * Something that can be joined; typically a [[Select]] or a [[Table]]
 */
trait Joinable[Q, R] {
  protected def joinableToSelect: Select[Q, R]
  protected def joinableIsTrivial: Boolean

  protected def joinableToFromExpr: (Context.From, Q)

  def joinFrom = {
    val otherSelect = joinableToSelect
    val otherFrom =
      if (joinableIsTrivial) otherSelect.asInstanceOf[SimpleSelect[_, _]].from.head
      else new SubqueryRef(otherSelect)

    (otherFrom, WithSqlExpr.get(otherSelect))
  }
  /**
   * Version of `crossJoin` meant for usage in `for`-comprehensions
   */
  def crossJoin[Q2, R2](): FlatJoin.Mapper[Q, Q2, R, R2] = {
    val (from, expr) = joinableToFromExpr
    new FlatJoin.Mapper[Q, Q2, R, R2]("CROSS JOIN", from, expr, None, Nil)
  }

  /**
   * Version of `join` meant for usage in `for`-comprehensions
   */
  def join[Q2, R2](on: Q => Expr[Boolean]): FlatJoin.Mapper[Q, Q2, R, R2] = {
    val (from, expr) = joinableToFromExpr
    new FlatJoin.Mapper[Q, Q2, R, R2]("JOIN", from, expr, Some(on(expr)), Nil)
  }

  /**
   * Version of `leftJoin` meant for usage in `for`-comprehensions
   */
  def leftJoin[Q2, R2](on: Q => Expr[Boolean]): FlatJoin.NullableMapper[Q, Q2, R, R2] = {
    val (from, expr) = joinableToFromExpr
    new FlatJoin.NullableMapper[Q, Q2, R, R2](
      "LEFT JOIN",
      from,
      JoinNullable(expr),
      Some(on(expr)),
      Nil
    )
  }

}
object Joinable {
  def toFromExpr[Q, R](x: Joinable[Q, R]) = x.joinableToFromExpr
  def toSelect[Q, R](x: Joinable[Q, R]) = x.joinableToSelect
  def isTrivial[Q, R](x: Joinable[Q, R]) = x.joinableIsTrivial
}
