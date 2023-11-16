package scalasql.operations

import scalasql.TypeMapper
import scalasql.query.Expr
import scalasql.renderer.SqlStr
import scalasql.renderer.SqlStr.SqlStringSyntax

class ExprOps(v: Expr[_]) {

  /** Equals to */
  def `=`[T](x: Expr[T]): Expr[Boolean] = Expr { implicit ctx => sql"($v = $x)" }

  /** Not equal to */
  def <>[T](x: Expr[T]): Expr[Boolean] = Expr { implicit ctx => sql"($v <> $x)" }

  /** Greater than */
  def >[V](x: Expr[V]): Expr[Boolean] = Expr { implicit ctx => sql"($v > $x)" }

  /** Less than */
  def <[V](x: Expr[V]): Expr[Boolean] = Expr { implicit ctx => sql"($v < $x)" }

  /** Greater than or equal to */
  def >=[V](x: Expr[V]): Expr[Boolean] = Expr { implicit ctx => sql"($v >= $x)" }

  /** Less than or equal to */
  def <=[V](x: Expr[V]): Expr[Boolean] = Expr { implicit ctx => sql"($v <= $x)" }

  def cast[V: TypeMapper]: Expr[V] = Expr { implicit ctx =>
    sql"CAST($v AS ${SqlStr.raw(implicitly[TypeMapper[V]].typeString)})"
  }
}