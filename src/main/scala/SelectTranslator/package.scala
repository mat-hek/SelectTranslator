import akka.actor.ActorRef

/**
  * Created by MatHek on 10.04.2016.
  */
package object selectTranslator {
    implicit val actorSystem = akka.actor.ActorSystem("system")
}
