package selectTranslator

import akka.actor._
import scala.util.{Success,Failure}

/**
  * Created by MatHek on 12.05.2016.
  */
object TranslationManager {
    Translation.downloader
    def apply(selectedTextGetter: ActorRef):ActorRef = actorSystem.actorOf(Props(new TranslationManager(selectedTextGetter)))
}


class TranslationManager(selectedTextGetter: ActorRef) extends Actor {

    private var skippingTranslations = 0
    private var currentTranslation:Translation = null

    def receive:Receive = {
        case t:Translation =>
            skippingTranslations += 1
            currentTranslation = t
            Ctrl.TranslationCtrl hotkeyTranslationStarted()
            selectedTextGetter ! SelectedTextGetter.MGetSelectedText
        case SelectedTextGetter.MGotSelectedText(result) =>
            skippingTranslations -= 1
            if(skippingTranslations == 0) {
                Ctrl.GetSelectedTextCtrl textRecognized result
                result match {
                    case Success(s) => currentTranslation translate s
                    case _ =>
                }
            }
    }
}