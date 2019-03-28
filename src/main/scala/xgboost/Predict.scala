    package xgboost

    import ml.dmlc.xgboost4j.scala.{Booster, XGBoost}
    import model.{Interpretability, Iris}
    import org.apache.flink.streaming.api.scala._

    object Predict {

      def main(args: Array[String]): Unit = {

        // parameters
        val modelPath = "model/model.bin"

        //Load model
        val model: Booster = XGBoost.loadModel(modelPath)

        // Set Flink's env
        implicit val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setParallelism(1)

        //Read data from custom iris source
        val irisDataStream =  env.fromCollection(Seq.fill(100)(Iris()))

        // predict iris data stream
        val predictionStream: DataStream[Interpretability] = irisDataStream.map({
          x =>
            val mat = x.toDmatrix
            Interpretability(model.predict(mat).flatten.head, model.predictContrib(mat).flatten.toList)
        })

        predictionStream.writeAsText("predict.txt", org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE)

        env.execute("Predict model from xgboost on Iris dataset")
      }
    }

