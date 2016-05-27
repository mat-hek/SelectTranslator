package utils

import scala.concurrent.duration.FiniteDuration

/**
  * Created by MatHek on 18.05.2016.
  */
package object pauser {
    implicit def durationToPauserWait(fd:FiniteDuration)(implicit pauser:Pauser): Pauser#WaitTo_obj = pauser pause fd
    implicit def durationPause(fd:FiniteDuration)(implicit pauser:Pauser): Pauser#PauseDuration_obj = {
        pauser pause fd
        pauser PauseDuration
    }
}
