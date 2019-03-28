package model

import ml.dmlc.xgboost4j.scala.DMatrix

case class Iris(modelId: String,
                sepalLength: Double,
                sepalWidth: Double,
                petalLength: Double,
                petalWidth: Double) {

  def toDmatrix: DMatrix = {
    new DMatrix(Array(sepalLength, sepalWidth, petalLength, petalWidth).map(_.toFloat), 1,  4)
  }

}

object Iris {

  private final lazy val RandomMin = 0.2
  private final lazy val RandomMax = 6.0
  private final lazy val RandomGenerator = scala.util.Random

  private final def truncateDouble(n: Double) = (math floor n * 10) / 10

  /** Random iris data
    *
    * Totally random data. The goal is to generate fake data.
    *
    * @return Iris
    */
  def apply(): Iris = {
    def randomVal = RandomMin + (RandomMax - RandomMin) * RandomGenerator.nextDouble()

    val randomData = Seq.fill(4)(truncateDouble(randomVal))
    Iris(
      "",
      randomData(0),
      randomData(1),
      randomData(2),
      randomData(3)
    )
  }
}