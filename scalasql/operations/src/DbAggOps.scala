package scalasql.operations

import scalasql.core.Aggregatable
import scalasql.core.SqlStr.SqlStringSyntax
import scalasql.core.{Queryable, TypeMapper, Db}

abstract class DbAggOps[T](v: Aggregatable[Db[T]]) {

  /** Concatenates the given values into one string using the given separator */
  def mkString(sep: Db[String] = null)(implicit tm: TypeMapper[T]): Db[String]

  /** TRUE if the operand is equal to one of a list of expressions or one or more rows returned by a subquery */
  //    def contains(e: Db[_]): Db[Boolean] = v.queryExpr(implicit ctx => sql"ALL($e in $v})")
}