package SelectTranslator

import java.awt.datatransfer.{Clipboard, ClipboardOwner, DataFlavor, StringSelection, Transferable}
import java.awt.{Robot, Toolkit}
import java.awt.event.KeyEvent

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

case object GetSelectedTextException extends Exception

object SelectedTextGetter {
    def apply() = new SelectedTextGetter()
}

class SelectedTextGetter extends ClipboardOwner {
    private var before:Option[Transferable] = None
    private var promise:Option[Promise[String]] = None
    private val robot = new Robot()
    Toolkit.getDefaultToolkit.getSystemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)

    def lostOwnership(clipboard: Clipboard, contents: Transferable) = synchronized {
        promise match {
            case Some(p) =>
                try {
                    val text = getClipboardText
                    setClipboardData(before)
                    p success text.trim
                    promise = None
                }
                catch {
                    case e @ (_:IllegalStateException | _:java.awt.datatransfer.UnsupportedFlavorException) => p failure GetSelectedTextException
                    case e: Exception => e.printStackTrace()
                }
            case None =>
        }
    }


    private def controlC() {
        val sleepTime = 20
        robot keyPress KeyEvent.VK_CONTROL
        Thread sleep sleepTime
        robot keyPress KeyEvent.VK_C
        robot keyRelease KeyEvent.VK_C
        Thread sleep sleepTime
        robot keyRelease KeyEvent.VK_CONTROL
        Thread sleep sleepTime
    }

    private def getClipboardText: String = Toolkit.getDefaultToolkit.getSystemClipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]

    private def getClipboardData: Option[Transferable] = Some(Toolkit.getDefaultToolkit.getSystemClipboard.getContents(this))

    private def setClipboardText(data: String) {
        Toolkit.getDefaultToolkit.getSystemClipboard.setContents(new StringSelection(data), this)
    }

    private val setClipboardData = PartialFunction[Option[Transferable], Unit] {
        case Some(data) => Toolkit.getDefaultToolkit.getSystemClipboard.setContents(data, this)
        case None =>
    }

    private def readSelectedText(): Future[String] = {
        val p = Promise[String]
        promise = Some(p)
        try {
            synchronized {
                before = getClipboardData
                setClipboardData(before)
                Thread sleep 50
                for (i <- 1 to 2)
                    controlC()
            }
        }
        catch {
            case e @ (_:IllegalStateException | _:java.awt.datatransfer.UnsupportedFlavorException) => p failure GetSelectedTextException
            case e: Exception => e.printStackTrace()
        }
        finally {
            Future {
                Thread sleep 100
                promise = None
                p failure GetSelectedTextException
            }
        }
        p.future
    }

    def getSelectedText() = readSelectedText()

}
