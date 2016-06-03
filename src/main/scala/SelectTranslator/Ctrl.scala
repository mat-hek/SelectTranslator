package selectTranslator

import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._


/**
  * Created by MatHek on 21.04.2016.
  */
import GUI._
import GUI.IGUIManager._
trait Ctrl

object Ctrl {
    val TranslationCtrl = new TranslationCtrl(GUIManager)
    val GetSelectedTextCtrl = new GetSelectedTextCtrl(GUIManager)
}

class TranslationCtrl(gui:IGUIManager) extends Ctrl{

    def hotkeyTranslationStarted() {
        gui ! MShow
    }

    def translationFinished(result:Try[String]) {
        result match {
            case Success(r) => gui ! MUpdateResult(r)
            case Failure(TranslationException) => println("cannot get translation")
            case Failure(e) => e.printStackTrace()
        }
        gui ! MHide(5 seconds)
    }

}

class GetSelectedTextCtrl(gui:IGUIManager) extends Ctrl {
    val textRecognized = PartialFunction[Try[String], Unit] {
        case Success(r) => gui ! MUpdateInput(r)
        case Failure(SelectedTextGetter.GetSelectedTextException) =>
            gui ! MHide(5 seconds)
            println("cannot get data from clipboard")
        case Failure(e) =>
            gui ! MHide(5 seconds)
            e.printStackTrace()
    }
}