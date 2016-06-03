package selectTranslator

import akka.actor._
import dispatch._
import dispatch.Defaults.executor
import jawn.ast.{JParser, WrongValueException}

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Promise}

/**
  * Created by MatHek on 06.04.2016.
  */

case object TranslationException extends Exception

object Translation {
    val downloader = TranslationDownloader
    def apply(lang:String) = new Translation(lang)(downloader)
}

case class Translation(lang: String)(td: ITranslationDownloader) extends Preference {

    def sendTo(translationManager:ActorRef) {
        translationManager ! this
    }

    def translate(input:String) {
        td.downloadTranslation(this, input) onComplete Ctrl.TranslationCtrl.translationFinished
    }
}

trait ITranslationDownloader{
    def downloadTranslation(translation: Translation, text:String):Future[String]
}
object TranslationDownloader extends ITranslationDownloader {

    Http()

    def downloadTranslation(translation:Translation, text:String) = {
        val translated = Promise[String]

        import com.typesafe.config.ConfigFactory
        val d = Http(url("https://translate.yandex.net/api/v1.5/tr.json/translate").POST << Map(
            "key" -> ConfigFactory.load.getString("SelectTranslator.yandex_key"),
            "text" -> text,
            "lang" -> translation.lang
        ) > as.String)

        Future {
            d onComplete {
                case Success(result) =>
                    translated complete {
                        try
                            Success((JParser parseFromString result).get get "text" get 0 asString)
                        catch {
                            case e: WrongValueException => Failure(TranslationException)
                        }
                    }
                case Failure(e) =>
                    e.printStackTrace()
            }
        }
        translated.future
    }

}