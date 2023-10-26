package scalasql.dialects

import scalasql._
import scalasql.query.Expr
import utest._

trait MySqlDialectTests extends MySqlSuite {
  def tests = Tests {
    test("reverse") - checker(
      query = Expr("Hello").reverse,
      sql = "SELECT REVERSE(?) as res",
      value = "olleH"
    )

    test("lpad") - checker(
      query = Expr("Hello").lpad(10, "xy"),
      sql = "SELECT LPAD(?, ?, ?) as res",
      value = "xyxyxHello"
    )

    test("rpad") - checker(
      query = Expr("Hello").rpad(10, "xy"),
      sql = "SELECT RPAD(?, ?, ?) as res",
      value = "Helloxyxyx"
    )
  }
}