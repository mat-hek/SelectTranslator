package SelectTranslator

import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._


/**
  * Created by MatHek on 21.04.2016.
  */
trait Ctrl

object TranslationCtrl extends Ctrl{
    def apply(hk:HotkeyManager, stg:SelectedTextGetter) {
        Preferences.translations foreach { t => hk addAction t.toHotkey(stg) }
    }

    def hotkeyTranslationStarted() {
        GUIManager show()
    }

    def translationFinished(result:Try[String]) {
        result match {
            case Success(r) => GUIManager updateResult r
            case Failure(TranslationException) => println("cannot get translation")
            case Failure(e) => e.printStackTrace()
        }
        GUIManager hide (5 seconds)
    }

}

object GetSelectedTextCtrl extends Ctrl {
    val textRecognized = PartialFunction[Try[String], Unit] {
        case Success(r) => GUIManager updateInput r
        case Failure(GetSelectedTextException) =>
            GUIManager hide (5 seconds)
            println("cannot get data from clipboard")
        case Failure(e) =>
            GUIManager hide (5 seconds)
            e.printStackTrace()
    }
}