package scalasql.dialects

import scalasql.query.Expr
import scalasql.utils.H2Suite
import utest._

trait H2DialectTests extends H2Suite {
  def description = "Operations specific to working with H2 Databases"
  def tests = Tests {

    test("ltrim2") - checker(
      query = Expr("xxHellox").ltrim("x"),
      sql = "SELECT LTRIM(?, ?) AS res",
      value = "Hellox"
    )

    test("rtrim2") - checker(
      query = Expr("xxHellox").rtrim("x"),
      sql = "SELECT RTRIM(?, ?) AS res",
      value = "xxHello"
    )

    test("lpad") - checker(
      query = Expr("Hello").lpad(10, "xy"),
      sql = "SELECT LPAD(?, ?, ?) AS res",
      value = "xxxxxHello" // H2 only uses first character of fill string
    )

    test("rpad") - checker(
      query = Expr("Hello").rpad(10, "xy"),
      sql = "SELECT RPAD(?, ?, ?) AS res",
      value = "Helloxxxxx" // H2 only uses first character of fill string
    )
  }
}