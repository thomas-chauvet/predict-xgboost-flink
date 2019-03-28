package flink

import ml.dmlc.xgboost4j.scala.XGBoost
import model.{Interpretability, Iris}
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.util.Collector

import scala.collection.JavaConversions._

class BroadcastModel extends BroadcastProcessFunction[Iris, String, Interpretability] {

  val defaultModel = XGBoost.loadModel(getClass.getResource("/model.bin").getPath)

  override def processElement(value: Iris, ctx: BroadcastProcessFunction[Iris, String, Interpretability]#ReadOnlyContext, out: Collector[Interpretability]): Unit = {
    val currentBroadcastState = ctx.getBroadcastState(StateModel.xgboostModel).immutableEntries()
    val model = if (currentBroadcastState.isEmpty) {
      defaultModel
    } else {
      currentBroadcastState.last.getValue
    }
    val matrix = value.toDmatrix
    out.collect(Interpretability(model.predict(matrix).flatten.head, model.predictContrib(matrix).flatten.toList))
  }

  override def processBroadcastElement(value: String, ctx: BroadcastProcessFunction[Iris, String, Interpretability]#Context, out: Collector[Interpretability]): Unit = {
    // Keep only last state
    ctx.getBroadcastState(StateModel.xgboostModel).clear()
    // Add state
    ctx.getBroadcastState(StateModel.xgboostModel).put("key", XGBoost.loadModel(value))
  }
}

