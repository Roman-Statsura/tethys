package tethys.circe

import io.circe.{Json, JsonObject}
import org.scalatest.{FlatSpec, Matchers}
import tethys.commons.TokenNode
import tethys.commons.TokenNode.{value => token, _}
import tethys.writers.tokens.SimpleTokenWriter._

class CirceSupportTest extends FlatSpec with Matchers {
  behavior of "Circe ast JsonReader"

  it should "parse Int" in {
    token(100L).tokensAs[Json] shouldBe Json.fromInt(100)
  }

  it should "parse Long" in {
    token(100L).tokensAs[Json] shouldBe Json.fromLong(100L)
  }

  it should "parse Float" in {
    token(100.0f).tokensAs[Json] shouldBe Json.fromFloatOrNull(100.0f)
  }

  it should "parse Double" in {
    token(100.0D).tokensAs[Json] shouldBe Json.fromDoubleOrNull(100.0D)
  }

  it should "parse BigInt" in {
    token(BigInt(100L)).tokensAs[Json] shouldBe Json.fromBigInt(BigInt(100L))
  }

  it should "parse BigDecimal" in {
    token(BigDecimal(100.0D)).tokensAs[Json] shouldBe Json.fromBigDecimal(100.0D)
  }

  it should "parse String" in {
    token("str").tokensAs[Json] shouldBe Json.fromString("str")
  }

  it should "parse Boolean.True" in {
    token(true).tokensAs[Json] shouldBe Json.True
  }

  it should "parse Boolean.False" in {
    token(false).tokensAs[Json] shouldBe Json.False
  }

  it should "parse Null" in {
    List(TokenNode.NullValueNode).tokensAs[Json] shouldBe Json.Null
  }

  it should "parse Array" in {
    arr(1, 2L, 3).tokensAs[Json] shouldBe
      Json.fromValues(List(Json.fromLong(1), Json.fromLong(2), Json.fromLong(3)))
  }

  it should "parse JsonObject" in {
    obj(
      "a" -> arr(1L, 2L),
      "b" -> obj("c" -> null),
      "c" -> token("demo"),
      "d" -> token(true),
      "e" -> token(false)
    ).tokensAs[JsonObject] shouldBe JsonObject(
      "a" -> Json.fromValues(List(Json.fromLong(1L), Json.fromLong(2L))),
      "b" -> Json.fromJsonObject(JsonObject("c" -> Json.Null)),
      "c" -> Json.fromString("demo"),
      "d" -> Json.True,
      "e" -> Json.False
    )
  }

  it should "parse Array of JsonObject" in {
    arr(obj("a" -> "b"), obj("c" -> "d")).tokensAs[Json] shouldBe Json.fromValues(List(
      Json.fromJsonObject(JsonObject("a" -> Json.fromString("b"))),
      Json.fromJsonObject(JsonObject("c" -> Json.fromString("d")))
    ))
  }


  behavior of "Circe ast JsonWriter"

  it should "write Int" in {
    Json.fromInt(100).asTokenList shouldBe token(100L)
  }

  it should "write Long" in {
    Json.fromLong(100L).asTokenList shouldBe token(100L)
  }

  it should "write Float" in {
    Json.fromFloat(100.0f).asTokenList shouldBe token(100.0D)
  }

  it should "write Double" in {
    Json.fromDouble(100.0D).asTokenList shouldBe token(100.0D)
  }

  it should "write BigInt" in {
    Json.fromBigInt(BigInt(100L)).asTokenList shouldBe token(BigDecimal(100L))
  }

  it should "write BigDecimal" in {
    Json.fromBigDecimal(100.0D).asTokenList shouldBe token(BigDecimal(100.0D))
  }

  it should "write String" in {
    Json.fromString("str").asTokenList shouldBe token("str")
  }

  it should "write Boolean.True" in {
    Json.fromBoolean(true).asTokenList shouldBe token(true)
  }

  it should "write Boolean.False" in {
    Json.fromBoolean(false).asTokenList shouldBe token(false)
  }

  it should "write Null" in {
    Json.Null.asTokenList shouldBe List(TokenNode.NullValueNode)
  }

  it should "write Array" in {
    Json.fromValues(List(
      Json.fromLong(1),
      Json.fromLong(2),
      Json.fromLong(3)
    )).asTokenList shouldBe arr(1L, 2L, 3L)
  }

  it should "write JsonObject" in {
    val jobj = JsonObject(
      "a" -> Json.fromValues(List(Json.fromLong(1L), Json.fromLong(2L))),
      "b" -> Json.fromJsonObject(JsonObject("c" -> Json.Null)),
      "c" -> Json.fromString("demo"),
      "d" -> Json.True,
      "e" -> Json.False
    )

    jobj.asTokenList shouldBe obj(
      "a" -> arr(1L, 2L),
      "b" -> obj("c" -> null),
      "c" -> token("demo"),
      "d" -> token(true),
      "e" -> token(false)
    )
  }
}
