package Utils

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by MatHek on 21.04.2016.
  */



object Timeout {
    def apply(): Timeout = new Timeout
}

class Timeout {

    var future = Future{}
    var finished: ( () => Any) = () => {}
    var waitTo: Deadline = 0 seconds fromNow
    def wait(time:FiniteDuration) = {
        waitTo = time.fromNow
        WaitTo
    }

    def cancel() = synchronized {
        finished = () => {}
        waitTo = 0 seconds fromNow
    }

    object WaitTo {
        def andThen(newFinished: => Any): Unit = synchronized {
            finished = () => newFinished
            if(future.isCompleted) {
                future = Future {
                    while (waitTo hasTimeLeft)
                        Thread sleep waitTo.timeLeft.toMillis
                }
                future onSuccess { case _ =>
                    synchronized{
                        if(future isCompleted)
                            finished()
                    }
                }
            }
        }
    }
}

