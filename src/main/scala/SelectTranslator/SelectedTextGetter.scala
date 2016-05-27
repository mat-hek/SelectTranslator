package selectTranslator

import java.awt.datatransfer.{Clipboard, ClipboardOwner, DataFlavor, StringSelection, Transferable}
import java.awt.{Robot, Toolkit}
import java.awt.event.KeyEvent

import akka.actor._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Try,Success,Failure}

object SelectedTextGetter {
    def apply() = actorSystem.actorOf(Props(new SelectedTextGetter))

    case object GetSelectedTextException extends Exception
    case object MGetSelectedText
    case class MGotSelectedText(text:Try[String])
}

class SelectedTextGetter extends Actor {
    private class ProcessingMsg
    private case object MTextCopied extends ProcessingMsg
    private case class MTextRead(text:Try[String]) extends ProcessingMsg
    private def success(s:String) = MTextRead(Success(s))
    private def fail(e:Exception) = MTextRead(Failure(e))

    import utils.pauser._
    implicit val pauser = Pauser()

    private object ClipboardManager extends ClipboardOwner {

        var before:Option[Transferable] = None
        val robot = new Robot()
        Toolkit.getDefaultToolkit.getSystemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)

        def lostOwnership(clipboard: Clipboard, contents: Transferable) {
            self ! MTextCopied
        }

        def controlC() {
            val sleepTime = 20
            Thread sleep sleepTime
            robot keyPress KeyEvent.VK_CONTROL
            Thread sleep sleepTime
            robot keyPress KeyEvent.VK_C
            Thread sleep sleepTime
            robot keyRelease KeyEvent.VK_C
            Thread sleep sleepTime
            robot keyRelease KeyEvent.VK_CONTROL
            Thread sleep sleepTime
        }

        def getClipboardText: String = Toolkit.getDefaultToolkit.getSystemClipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]

        def getClipboardData: Option[Transferable] = Some(Toolkit.getDefaultToolkit.getSystemClipboard.getContents(this))

        def setClipboardText(data: String) {
            Toolkit.getDefaultToolkit.getSystemClipboard.setContents(new StringSelection(data), this)
        }

        val setClipboardData = PartialFunction[Option[Transferable], Unit] {
            case Some(data) => Toolkit.getDefaultToolkit.getSystemClipboard.setContents(data, this)
            case None =>
        }
    }

    import ClipboardManager._
    import SelectedTextGetter._

    private def readSelectedText() {
        try {
            before = getClipboardData
            ClipboardManager setClipboardData before
            Thread sleep 100
            for (i <- 1 to 2)
                controlC()

            actorSystem.scheduler scheduleOnce(200 millis, self, fail(GetSelectedTextException))
        }
        catch {
            case e @ (_:IllegalStateException | _:java.awt.datatransfer.UnsupportedFlavorException) => self ! fail(GetSelectedTextException)
            case e: Exception => e.printStackTrace()
        }
    }

    private def textCopied() {
        try {
            val text = getClipboardText
            ClipboardManager setClipboardData before
            self ! success(text.trim)
        }
        catch {
            case e @ (_:IllegalStateException | _:java.awt.datatransfer.UnsupportedFlavorException) => self ! fail(GetSelectedTextException)
            case e: Exception => e.printStackTrace()
        }
    }


    private var failuresToReceive = 0;

    def receive = ready

    def ready:Receive = {
        case MGetSelectedText =>
            failuresToReceive += 1
            context become processing(sender :: Nil)
            readSelectedText()
        case MTextRead(Failure(e)) => failuresToReceive -= 1
        case m:ProcessingMsg =>
    }

    def processing(requestors:List[ActorRef]):Receive = {
        case MGetSelectedText => context become processing(sender :: requestors)
        case MTextCopied => textCopied()
        case MTextRead(Failure(e)) if failuresToReceive > 1 => failuresToReceive -= 1
        case MTextRead(result) =>
            requestors.reverseIterator foreach (_ ! MGotSelectedText(result))
            context become ready
    }

}
