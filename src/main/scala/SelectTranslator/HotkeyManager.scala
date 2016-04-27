package SelectTranslator

import javax.swing.KeyStroke

import com.tulskiy.keymaster.common.{HotKeyListener, Provider}

import scala.collection.{immutable => imm, mutable => mt}

object HotkeyManager {
    val provider = Provider.getCurrentProvider(true)
    def apply() = new HotkeyManager
    def reset() = provider.reset()
}

object Hotkey {
    type Keys = KeyStroke
    type Callback = (() => Any)

}

case class Hotkey(keys: Hotkey.Keys, callback : Hotkey.Callback)

class HotkeyManager extends HotKeyListener{

    private val actions = mt.HashMap.empty[Hotkey.Keys, mt.Set[Hotkey.Callback]]

    def addAction(h:Hotkey): HotkeyManager = {
        actions getOrElseUpdate (h.keys, mt.Set.empty[Hotkey.Callback]) += h.callback
        HotkeyManager.provider.register(h.keys, this)
        this
    }

    override def onHotKey(hotKey:com.tulskiy.keymaster.common.HotKey) {

        actions get hotKey.keyStroke match {
            case Some(hcSet) => hcSet foreach { _() }
            case None =>
        }
    }
}