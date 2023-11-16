package scalasql.query

import scalasql.renderer.{Context, SqlStr}
import scalasql.{TypeMapper, Queryable}
import scalasql.renderer.SqlStr.SqlStringSyntax

/**
 * A SQL `DELETE` query
 */
trait Delete[Q] extends Query[Int] with Returnable[Q]

object Delete {
  class Impl[Q](val expr: Q, filter: Expr[Boolean], val table: TableRef) extends Delete[Q] {
    override def queryIsExecuteUpdate = true
    def queryWalkExprs() = Nil
    def queryIsSingleRow = true

    protected def renderToSql(ctx: Context) = new Renderer(table, filter, ctx).render()
    protected def queryTypeMappers() = Seq(TypeMapper.IntType)

    protected def queryValueReader = implicitly
  }

  class Renderer(table: TableRef, expr: Expr[Boolean], prevContext: Context) {
    lazy val tableNameStr = SqlStr.raw(prevContext.config.tableNameMapper(table.value.tableName))
    implicit val implicitCtx = Context.compute(prevContext, Nil, Some(table))

    def render() = sql"DELETE FROM $tableNameStr WHERE $expr"
  }
}