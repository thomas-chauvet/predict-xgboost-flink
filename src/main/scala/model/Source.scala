package model

import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.scala._

object Source {

  // Random Iris data stream
  @throws(classOf[Exception])
  def randomIrisData(env: StreamExecutionEnvironment, sleep: Int = 1000): DataStream[Iris] = {
    env.addSource((sc: SourceContext[Iris]) => {
      while (true) {
        sc.collect(Iris())
        Thread.sleep(1000)
      }
    })

  }


}
