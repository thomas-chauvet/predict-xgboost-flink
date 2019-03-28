package xgboost

import scala.collection.mutable
import scala.io.Source
import ml.dmlc.xgboost4j.scala.{DMatrix, XGBoost}

object Training {

  def readCSV() : (Array[Float], Array[Float]) = {

    val csvFile = Source.fromFile("data/iris.csv")
      .getLines()
      .toArray
      .map(_.split(","))

    val y = csvFile.map(_.last match {
      case "Iris-versicolor" => "1"
      case "Iris-setosa" => "0"
      case "Iris-virginica" => "0"
      case x => x
    }).map(_.toFloat)

    val X = csvFile.flatMap(_.dropRight(1)).map(_.toFloat)
    (X, y)
  }

  def main(args: Array[String]): Unit = {
    val irisData = readCSV()
    val X = irisData._1
    val y = irisData._2
    val ncol = 4
    val nrow = X.length/ncol
    val train = new DMatrix(X, nrow, ncol)
    train.setLabel(y)

    val params = new mutable.HashMap[String, Any]()
    params += "eta" -> 1.0
    params += "max_depth" -> 10
    params += "silent" -> 1
    params += "objective" -> "binary:logistic"

    // lot of round to add complexity in the tree in order to test computation time with big model.
    val round = 2000
    // train a model
    val booster = XGBoost.train(train, params.toMap, round)

    booster.saveModel("model/model.bin")
  }
}
