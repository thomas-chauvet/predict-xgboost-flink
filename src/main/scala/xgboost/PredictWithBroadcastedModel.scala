package xgboost


import java.util.Properties

import flink.{BroadcastModel, StateModel}
import model.{Interpretability, Iris}
import org.apache.flink.api.common.serialization.{DeserializationSchema, SimpleStringEncoder, SimpleStringSchema}
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala._
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer.{FetcherType, OffsetStore}

// todo: do not limit stream
// todo: Add UTs

object PredictWithBroadcastedModel {

  def main(args: Array[String]): Unit = {

    // Broadcast stream
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "flink-xgboost")

    run(properties)
  }

  def run(properties: Properties): Unit = {

    // Set Flink's env
    implicit val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val broadcastStream = env
      .addSource(new FlinkKafkaConsumer[String]("model-path-topic",
        DeserializationSchema[String],
        properties,
        OffsetStore.KAFKA,
        FetcherType.NEW_HIGH_LEVEL)
      )
      .broadcast(StateModel.xgboostModel)

    //Read data from custom iris source
    val irisDataStream =  env.fromCollection(Seq.fill(100)(Iris()))

    // predict iris data stream
    val predictionStream: DataStream[Interpretability] = irisDataStream
      .connect(broadcastStream)
      .process(new BroadcastModel)

    // output predict in text file
    val textSink: StreamingFileSink[String] = StreamingFileSink
      .forRowFormat(new Path("output"), new SimpleStringEncoder[String]("UTF-8"))
      .build()

    predictionStream.map(_.toString).addSink(textSink)

    env.execute("Predict model from xgboost on Iris dataset")
  }
}

