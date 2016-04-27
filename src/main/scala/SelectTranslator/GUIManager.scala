package SelectTranslator

import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing._

import Utils.Timeout

import scala.concurrent.duration._

/**
  * Created by MatHek on 11.04.2016.
  */

object GUIManager {

    GUI

    def updateInput(text:String): Unit = {
        GUI.textContainer write text
    }
    def updateResult(text:String): Unit = {
        GUI.resultContainer write text
    }

    val hideTimeout = Timeout()
    def show() ={
        hideTimeout cancel()
        GUI.f setFocusableWindowState false
        GUI.f setVisible true
        GUI.f setFocusableWindowState true
        this
    }
    def hide() {
        GUI.f setVisible false
    }
    def hide(time:FiniteDuration) {
        hideTimeout wait time andThen hide()
    }

    private object GUI {

        object TranslationField {
            implicit def toJComponent(tf:TranslationField):JComponent = tf.p
        }

        class TranslationField(fontSize:Int = 14) {
            private val p = new JPanel()
            p setBorder BorderFactory.createEmptyBorder(3,5,3,5)
            p setOpaque false
            p setAlignmentX 0
            p setLayout new BoxLayout(p, 0)
            private var tf = makeTextField()
            p add tf
            private val font = new Font("arial", 0, fontSize)

            def makeTextField(contents:String = "") = {
                val ntf = new JTextField(contents)
                ntf setOpaque false
                ntf setBorder BorderFactory.createEmptyBorder()
                ntf setFont font
                ntf setForeground Color.orange
                ntf setAutoscrolls true
                ntf
            }

            def write(contents:String): Unit = {
                p remove tf
                tf = makeTextField(contents)
                p add tf
                f pack()
                f repaint()
                updateSize()
            }

        }

        def updateSize(): Unit = {
            val screenSize = Toolkit getDefaultToolkit() getScreenSize()
            f setLocation (screenSize.width - f.getWidth, screenSize.height - f.getHeight - 50)
        }

        val transparentBg = new Color(0, 0, 0, 0)

        val textContainer = new TranslationField()
        val resultContainer = new TranslationField(fontSize = 18)

        val closeButton = new JLabel("x")

        val f = new JFrame()
        f dispose()
        f setType Window.Type.UTILITY
        f setAlwaysOnTop true
        f setUndecorated true

        val contentPanel = new JPanel()
        contentPanel setLayout new BoxLayout(contentPanel,1)


        closeButton.addMouseListener(new MouseAdapter {
            override def mouseClicked(e: MouseEvent) {f setVisible false}
        })

        contentPanel setBorder BorderFactory.createEmptyBorder(10,20,10,20)
        contentPanel add textContainer
        contentPanel add Box.createRigidArea(new Dimension(0,5))
        contentPanel add resultContainer

        contentPanel setBackground  new Color(10, 10, 10, 230)

        f add contentPanel
        f setBackground transparentBg
        f pack()
        updateSize()

    }
}


