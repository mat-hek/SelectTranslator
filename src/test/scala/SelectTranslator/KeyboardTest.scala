package SelectTranslator

import javax.swing.KeyStroke

import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.PrivateMethodTester._

/**
  * Created by MatHek on 09.04.2016.
  */
class KeyboardTest extends FunSuite with BeforeAndAfter {

    var hc:HotkeyManager = null
    var finished = false

    before {
        hc = HotkeyManager()
        hc addAction Hotkey(KeyStroke.getKeyStroke("control C"), () => {finished = true})
    }

    test("keys simulating and catching test") {
        SelectedTextGetter invokePrivate PrivateMethod[String]('controlC)()
        Thread sleep 1000
        assert(finished)
    }

    after {
        HotkeyManager reset()
    }
}