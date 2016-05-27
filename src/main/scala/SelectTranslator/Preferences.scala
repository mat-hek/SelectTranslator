package selectTranslator

import javax.swing.KeyStroke

import akka.actor._

/**
  * Created by MatHek on 07.04.2016.
  */
trait Preference
case class TranslationPreference(shortcut: Hotkey.Keys, t: Translation) extends Preference {
    def toHotkey(translationManager:ActorRef) = Hotkey(shortcut, () => t.sendTo(translationManager))
}

object Preferences {
    private val l:List[TranslationPreference] = TranslationPreference(KeyStroke.getKeyStroke("control 1"), Translation("en-pl")) :: Nil
    def translations = l.iterator

}
