package SelectTranslator

import scala.util.{Success, Failure}


/**
  * Created by MatHek on 06.04.2016.
  */
object SelectTranslator extends App {

    val hc = HotkeyManager()
    val stg = SelectedTextGetter()
    TranslationCtrl(hc,stg)
    GUIManager
}
