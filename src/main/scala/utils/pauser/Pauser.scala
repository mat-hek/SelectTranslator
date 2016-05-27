package utils.pauser

import akka.actor._
import dispatch.Defaults.executor

import scala.concurrent.duration._

import scala.collection.mutable.Queue

/**
  * Created by MatHek on 16.05.2016.
  */

object PauserTest extends App {

    import selectTranslator._

    implicit val pauser = Pauser()
    2.seconds pause()
    println("abc")
    2.seconds ~~= println("def")
    3.seconds ~~= println("ghi")
}

object Pause {
    def For(time:FiniteDuration)(implicit pauser:Pauser) = pauser pause time
}

object Pauser {
    def apply()(implicit actorSystem: ActorSystem) = new Pauser(actorSystem)
}

class Pauser(actorSystem: ActorSystem){
    import PauserActor._
    private val pauserActor = actorSystem.actorOf(Props(new PauserActor))


    def pause(time:FiniteDuration): WaitTo_obj ={
        pauserActor ! Wait(time)
        WaitTo
    }

    def schedule(callback : => Any):Pauser = {
        pauserActor ! Do(() => callback)
        this
    }

    def ~~= (callback: => Any) = schedule(callback)

    type WaitTo_obj = WaitTo.type
    object WaitTo {
        def pauseAnd(callback: => Any):Pauser = schedule(callback)
        def ~~= (callback: => Any) = pauseAnd(callback)
    }

    type PauseDuration_obj = PauseDuration.type
    object PauseDuration {
        def pause() = this
    }

}


private object PauserActor {
    sealed trait Action
    case class Wait(time:FiniteDuration) extends Action
    case class Do(callback: () => Any) extends Action
    case object WaitingFinished
}

private class PauserActor extends Actor {
    import PauserActor._

    val requests = Queue[Action]()

    def receive = ready
    def ready:Receive = {
        case Wait(time) =>
            context.system.scheduler.scheduleOnce(time, self, WaitingFinished)
            context become waiting
        case Do(callback) =>
            callback()
            self ! WaitingFinished
            context become waiting
    }
    def waiting:Receive = {
        case m:Action => requests enqueue m
        case WaitingFinished =>
            if(requests nonEmpty)
                self ! requests.dequeue
            context become ready
    }
}