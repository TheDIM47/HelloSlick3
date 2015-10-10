package tests

import org.specs2.mutable._
import play.api.libs.json.{Json, JsObject, JsValue}
import play.api.test.Helpers._
import play.api.test._

class HelloSlick3Specs extends Specification {

  val ApiRoot = "/api/names"

  def fakeApp = FakeApplication(additionalConfiguration = inMemoryDatabase(options = Map("MODE" -> "MYSQL")))

  "Application" should {

    "404 on a bad request" in new WithApplication {
      val result = route(FakeRequest(GET, "/qqqq")).get
      status(result) mustEqual NOT_FOUND
    }

    "return empty on root" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get
      status(home) mustEqual OK
      contentType(home) must beSome.which(_ == "application/json")
      contentAsString(home) must contain("[]")
    }

    "return empty on api root" in new WithApplication {
      val home = route(FakeRequest(GET, ApiRoot)).get
      status(home) mustEqual OK
      contentType(home) must beSome.which(_ == "application/json")
      contentAsString(home) must contain("[]")
    }

    "insert new entities and return new indexes" in new WithApplication {
      for {
        v <- Seq("qwe", "www", "zzz").zipWithIndex
      } yield {
        val home2 = route(FakeRequest(POST, s"$ApiRoot/${v._1}")).get
        status(home2) mustEqual OK
        contentType(home2) must beSome.which(_ == "application/json")
        contentAsString(home2) must contain((v._2 + 1).toString)
      }
    }

    "complex test" in new WithApplication {
      // insert "aaa"
      {
        val p = ("aaa", 1)
        val home = route(FakeRequest(POST, s"$ApiRoot/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain((p._2).toString)
      }
      {
        val home = route(FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"}] """.stripMargin))
      }

      // insert "bbb"
      {
        val p = ("bbb", 2)
        val home = route(FakeRequest(POST, s"$ApiRoot/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain((p._2).toString)
      }
      {
        val home = route(FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"},{"id":2, "name":"bbb"}] """.stripMargin))
      }

      // update "bbb" to "xxx"
      {
        val p = ("xxx", 2)
        val home = route(FakeRequest(PUT, s"$ApiRoot/${p._2}/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain("1")
      }
      {
        val home = route(FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"},{"id":2, "name":"xxx"}] """.stripMargin))
      }

      // delete "aaa"
      {
        val home = route(FakeRequest(DELETE, s"$ApiRoot/1")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain("1")
      }
      {
        val home = route(FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":2, "name":"xxx"}] """.stripMargin))
      }
    }

    "should produce error on insert duplicates" in new WithApplication {
      val p = "aaa"
      val h1 = route(FakeRequest(POST, s"$ApiRoot/$p")).get
      status(h1) mustEqual OK
      val h2 = route(FakeRequest(POST, s"$ApiRoot/$p")).get
      status(h2) mustEqual BAD_REQUEST
      contentType(h2) must beSome.which(_ == "text/plain")
    }

    "should produce Not Found on wrong ID on update or delete" in new WithApplication {
      val id = -1

      val h1 = route(FakeRequest(DELETE, s"$ApiRoot/$p")).get
      status(h1) mustEqual NOT_FOUND

      val h2 = route(FakeRequest(PUT, s"$ApiRoot/$p/qwe")).get
      status(h2) mustEqual NOT_FOUND
    }

  }
}
