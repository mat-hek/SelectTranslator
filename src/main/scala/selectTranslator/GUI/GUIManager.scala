package selectTranslator.GUI

import akka.actor._
import selectTranslator._

import scala.concurrent.duration._

/**
  * Created by MatHek on 11.04.2016.
  */

object IGUIManager {
    case class MUpdateInput(text:String)
    case class MUpdateResult(text:String)
    case object MShow
    case object MHide
    case class MHide(time:FiniteDuration)
    implicit def toActor(gm:IGUIManager):ActorRef = gm.guiActor
    implicit def toScalaActor(gm:IGUIManager):ScalaActorRef = gm.guiActor
}

trait IGUIManager{
    val guiActor:ActorRef
    def apply = guiActor
}

object GUIManager extends IGUIManager{
    val guiActor = actorSystem.actorOf(Props(new GUIManager()))
}

class GUIManager extends Actor{
    import IGUIManager._
    import GUIContents._
    import GUIContents.PopupWindow._
    PopupWindow

    import utils.pauser._

    private implicit val pauser = Pauser()

    def receive = {
        case MUpdateInput(text) =>
            textContainer write text
            resultContainer write ""
        case MUpdateResult(text) => resultContainer write text
        case MShow =>
            pauser cancelAll()
            frame setFocusableWindowState false
            frame setVisible true
            frame setFocusableWindowState true
        case MHide => frame setVisible false
        case MHide(time) => time ~~= self ! MHide

    }
}