package scalasql.query

import scalasql.dialects.Dialect
import scalasql.renderer.SqlStr.{Renderable, SqlStringSyntax}
import scalasql.renderer.{Context, SqlStr}
import scalasql.utils.OptionPickler
import scalasql.{Column, Queryable, Table, TypeMapper}

/**
 * A SQL `INSERT VALUES` query
 */
trait InsertColumns[Q, R] extends InsertReturnable[Q] with Query[Int] {
  def columns: Seq[Column.ColumnExpr[_]]
  def valuesLists: Seq[Seq[Expr[_]]]
}
object InsertColumns {
  class Impl[Q, R](
      insert: Insert[Q, R],
      val columns: Seq[Column.ColumnExpr[_]],
      val valuesLists: Seq[Seq[Expr[_]]]
  )(implicit val qr: Queryable[Q, R], dialect: Dialect)
      extends InsertColumns[Q, R] {
    import dialect.{dialectSelf => _, _}
    def table = insert.table
    protected def expr: Q = WithExpr.get(insert)

    protected override def renderToSql(ctx: Context) =
      new Renderer(columns, ctx, valuesLists, Table.tableName(table.value)).render()

    protected override def queryTypeMappers(): Seq[TypeMapper[_]] = Seq(dialect.IntType)

    def queryWalkExprs() = Nil
    protected override def queryIsSingleRow = true
    protected override def queryIsExecuteUpdate = true

    protected override def queryValueReader: OptionPickler.Reader[Int] = implicitly
  }

  class Renderer(
      columns0: Seq[Column.ColumnExpr[_]],
      prevContext: Context,
      valuesLists: Seq[Seq[Expr[_]]],
      tableName: String
  ) {

    implicit lazy val ctx: Context = prevContext.withExprNaming(Map()).withFromNaming(Map())
    lazy val columns = SqlStr
      .join(columns0.map(c => SqlStr.raw(ctx.config.columnNameMapper(c.name))), sql", ")
    lazy val values = SqlStr.join(
      valuesLists
        .map(values =>
          sql"(" + SqlStr.join(values.map(Renderable.renderToSql(_)), sql", ") + sql")"
        ),
      sql", "
    )
    def render() = {
      sql"INSERT INTO ${SqlStr.raw(ctx.config.tableNameMapper(tableName))} ($columns) VALUES $values"
    }
  }
}