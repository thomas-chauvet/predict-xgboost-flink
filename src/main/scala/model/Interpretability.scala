package model

import scala.math.exp

case class Interpretability(score: Float, shapValues: Map[String, Float])

object Interpretability {

  def apply(pred: Float, shapValues: List[Float]): Interpretability = {
    Interpretability(pred, (List("sepalLength", "sepalWidth", "petalLength", "petalWidth") zip shapValues).toMap)
  }

  /** Convert margin score from xgboost predict to probability
    *
    * @param x: xgboost margin score
    * @return probability
    */
  def scoreToProba(x: Float): Float = {
    (1 / (1 + exp(-x))).toFloat
  }

  /** Constructor to build Interpretability only based on shap values.
    * Shap value returned by xgboost exactly sum to margin score. Then,
    * we convert it in probability.
    *
    * @param shapValues: shap values returned by xgboost contributions.
    * @return Interpretability
    */
  def apply(shapValues: List[Float]): Interpretability = {
    Interpretability(scoreToProba(shapValues.sum), (List("sepalLength", "sepalWidth", "petalLength", "petalWidth") zip shapValues).toMap)
  }

  val empty = Interpretability(-1, Map.empty[String, Float])
}
