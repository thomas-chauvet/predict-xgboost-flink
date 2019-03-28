package flink

import ml.dmlc.xgboost4j.scala.Booster
import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.scala._

object StateModel {
  val xgboostModel: MapStateDescriptor[String, Booster] =
    new MapStateDescriptor(
      "Models",
      createTypeInformation[String],
      createTypeInformation[Booster]
    )
}
