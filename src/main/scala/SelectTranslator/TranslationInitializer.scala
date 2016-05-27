package selectTranslator


/**
  * Created by MatHek on 12.05.2016.
  */
object TranslationInitializer {

    def apply() {
        val hm = HotkeyManager()
        val stg = SelectedTextGetter()
        val tm = TranslationManager(stg)
        Preferences.translations foreach { t => hm addAction t.toHotkey(tm) }
    }
}
