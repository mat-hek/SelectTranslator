package SelectTranslator

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.PrivateMethodTester._

import scala.concurrent.duration._
import scala.concurrent.Future
import TranslationCtrl._

/**
  * Created by MatHek on 09.04.2016.
  */
class TranslationTest extends FunSuite with BeforeAndAfter {

    var tc:Translation = null

    before {
        tc = Translation("en-pl")
    }

    test("getting translation") {

        implicit val config = ScalaFutures.PatienceConfig(2000 millis, 2000 millis)
        val f = tc invokePrivate PrivateMethod[Future[String]] ('getTranslation)("tree")
        ScalaFutures.whenReady(f) {
            e => assert(e == "drzewo")
        }
    }


}
