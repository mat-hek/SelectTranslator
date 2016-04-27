package SelectTranslator

import dispatch._
import dispatch.Defaults.executor
import jawn.ast.{JParser, WrongValueException}

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Promise}

/**
  * Created by MatHek on 06.04.2016.
  */

case object TranslationException extends Exception

case class Translation(lang: String) extends Preference{


    def translate(stg:SelectedTextGetter) {
        TranslationCtrl hotkeyTranslationStarted()
        val f = stg getSelectedText()
        f onSuccess { case input => translate(input) }
        f onComplete GetSelectedTextCtrl.textRecognized
    }

    def translate(input:String) {
        getTranslation(input) onComplete TranslationCtrl.translationFinished
    }



    private def getTranslation(text:String):Future[String] = {
        val translation = Promise[String]

        import com.typesafe.config.ConfigFactory
        val d = Http(url("https://translate.yandex.net/api/v1.5/tr.json/translate").POST << Map(
            "key" -> ConfigFactory.load.getString("SelectTranslator.yandex_key"),
            "text" -> text,
            "lang" -> lang
        ) > as.String)

        d onComplete {
            case Success(result) =>
                translation complete {
                    try
                        Success((JParser parseFromString result).get get "text" get 0 asString)
                    catch {
                        case e: WrongValueException => Failure(TranslationException)
                    }
                }
            case Failure(e) =>
                e.printStackTrace()
        }
        translation.future
    }
}