# Flink + Xgboost (+ docker) example

Simple example showing how to use xgboost to predict data on Flink's stream. In bonus, we add the
native interpretability computed in xgboost (based on Shap values).

## Train an xgboost model

We use classic iris data to train a simple xgboost model in `xgboost.Training`.

In order to train the model, launch `sbt` then:

```bash
runMain xgboost.Training
```

You will obtain in `model` folder xgboost binaries (trained model).

## Predict on Flink's stream

We use the model trained on a Flink's stream in `xgboost.Predict`.

The output is a text file containing the `Interpretability` case class (shap values + prediction).

In order to predict on the model, launch `sbt` then:

```bash
runMain xgboost.Predict
```

*Note: we limit our stream to few element for demo purpose. Obviously, you can use an
infinite stream and sink your data everywhere you want.

You can also run your application from within IntelliJ:  select the classpath of the 'mainRunner' module in the run/debug configurations.
Simply open 'Run -> Edit configurations...' and then select 'mainRunner' from the "Use classpath of module" dropbox.

## Predict in docker(-compose)

In order to use our classes in docker, first we have to create the `.jar` of our application:

```bash
sbt assembly
```

Then we can use our `docker-compose` file:

```bash
docker-compose up -d
```

In few seconds, we can run:
```bash
docker-compose exec taskmanager bash -c "cat predict.txt"
```
and see the output `predict.txt`.

*Note: we customize Flink's image by adding `libgomp1` to make xgboost work.*
