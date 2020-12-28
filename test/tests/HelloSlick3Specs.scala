package tests

import org.specs2.mutable._
import play.api.libs.json.{Json, JsObject, JsValue}
import play.api.test.Helpers._
import play.api.test._

class HelloSlick3Specs extends PlaySpecification {

  val ApiRoot = "/api/names"

  "Application" should {

    "404 on a bad request" in new WithApplication with Injecting {
      val result = route(app, FakeRequest(GET, "/qqqq")).get
      status(result) mustEqual NOT_FOUND
    }

    "return empty on root" in new WithApplication with Injecting {
      val home = route(app, FakeRequest(GET, "/")).get
      status(home) mustEqual OK
      contentType(home) must beSome.which(_ == "application/json")
      contentAsString(home) must contain("[]")
    }

    "return empty on api root" in new WithApplication with Injecting {
      val home = route(app, FakeRequest(GET, ApiRoot)).get
      status(home) mustEqual OK
      contentType(home) must beSome.which(_ == "application/json")
      contentAsString(home) must contain("[]")
    }

    "insert new entities and return new indexes" in new WithApplication with Injecting {
      for {
        v <- Seq("qwe", "www", "zzz").zipWithIndex
      } yield {
        val home2 = route(app, FakeRequest(POST, s"$ApiRoot/${v._1}")).get
        status(home2) mustEqual OK
        contentType(home2) must beSome.which(_ == "application/json")
        contentAsString(home2) must contain((v._2 + 1).toString)
      }
    }

    "complex test" in new WithApplication with Injecting {
      // insert "aaa"
      {
        val p = ("aaa", 1)
        val home = route(app, FakeRequest(POST, s"$ApiRoot/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain((p._2).toString)
      }
      {
        val home = route(app, FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"}] """.stripMargin))
      }

      // insert "bbb"
      {
        val p = ("bbb", 2)
        val home = route(app, FakeRequest(POST, s"$ApiRoot/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain((p._2).toString)
      }
      {
        val home = route(app, FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"},{"id":2, "name":"bbb"}] """.stripMargin))
      }

      // update "bbb" to "xxx"
      {
        val p = ("xxx", 2)
        val home = route(app, FakeRequest(PUT, s"$ApiRoot/${p._2}/${p._1}")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain("1")
      }
      {
        val home = route(app, FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":1, "name":"aaa"},{"id":2, "name":"xxx"}] """.stripMargin))
      }

      // delete "aaa"
      {
        val home = route(app, FakeRequest(DELETE, s"$ApiRoot/1")).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsString(home) must contain("1")
      }
      {
        val home = route(app, FakeRequest(GET, ApiRoot)).get
        status(home) mustEqual OK
        contentType(home) must beSome.which(_ == "application/json")
        contentAsJson(home) mustEqual (Json.parse(""" [{"id":2, "name":"xxx"}] """.stripMargin))
      }
    }

    "should produce error on insert duplicates" in new WithApplication {
      val p = "aaa"
      val h1 = route(app, FakeRequest(POST, s"$ApiRoot/$p")).get
      status(h1) mustEqual OK
      val h2 = route(app, FakeRequest(POST, s"$ApiRoot/$p")).get
      status(h2) mustEqual BAD_REQUEST
      contentType(h2) must beSome.which(_ == "text/plain")
    }

    "should produce Ok on wrong ID on update or delete" in new WithApplication {
      val id = -123

      val h1 = route(app, FakeRequest(DELETE, s"$ApiRoot/$id")).get
      status(h1) mustEqual OK

      val h2 = route(app, FakeRequest(PUT, s"$ApiRoot/$id/qwe")).get
      status(h2) mustEqual OK
    }

  }
}
