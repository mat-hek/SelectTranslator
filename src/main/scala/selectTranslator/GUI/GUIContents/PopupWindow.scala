package selectTranslator.GUI.GUIContents

import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing._

/**
  * Created by MatHek on 18.05.2016.
  */

import PopupWindow._

private[GUIContents] object TranslationField {
    implicit def toJComponent(tf: TranslationField): JComponent = tf.p
}

private[GUIContents] class TranslationField(fontSize: Int = 14) {
    private val p = new JPanel()
    p setBorder BorderFactory.createEmptyBorder(3, 5, 3, 5)
    p setOpaque false
    p setAlignmentX 0
    p setLayout new BoxLayout(p, 0)
    private var tf = makeTextField()
    p add tf
    private val font = new Font("arial", 0, fontSize)

    def makeTextField(contents: String = "") = {
        val ntf = new JTextField(contents)
        ntf setOpaque false
        ntf setBorder BorderFactory.createEmptyBorder()
        ntf setFont font
        ntf setForeground Color.orange
        ntf setAutoscrolls true
        ntf
    }

    def write(contents: String): Unit = {
        p remove tf
        tf = makeTextField(contents)
        p add tf
        frame pack()
        frame repaint()
        updateSize()
    }

}

object PopupWindow {

    def updateSize(): Unit = {
        val screenSize = Toolkit getDefaultToolkit() getScreenSize()
        frame setLocation(screenSize.width - frame.getWidth, screenSize.height - frame.getHeight - 50)
    }

    val transparentBg = new Color(0, 0, 0, 0)

    val textContainer = new TranslationField()
    val resultContainer = new TranslationField(fontSize = 18)

    val closeButton = new JLabel("x")

    val frame = new JFrame()
    frame dispose()
    frame setType Window.Type.UTILITY
    frame setAlwaysOnTop true
    frame setUndecorated true

    val contentPanel = new JPanel()
    contentPanel setLayout new BoxLayout(contentPanel, 1)


    closeButton.addMouseListener(new MouseAdapter {
        override def mouseClicked(e: MouseEvent) {
            frame setVisible false
        }
    })

    contentPanel setBorder BorderFactory.createEmptyBorder(10, 20, 10, 20)
    contentPanel add textContainer
    contentPanel add Box.createRigidArea(new Dimension(0, 5))
    contentPanel add resultContainer

    contentPanel setBackground new Color(10, 10, 10, 230)

    frame add contentPanel
    frame setBackground transparentBg
    frame pack()
    updateSize()


}