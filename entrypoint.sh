#!/bin/bash

if [[ ! -v FLINK_JOB_MANAGER_HOST ]]; then
  echo "FLINK_JOB_MANAGER_HOST variable not set. Giving up!"
  exit 1
fi
if [[ ! -v FLINK_JOB_MANAGER_PORT ]]; then
  echo "FLINK_JOB_MANAGER_PORT variable not set. Giving up!"
  exit 1
fi

FLINK_CMD="flink run -m ${FLINK_JOB_MANAGER_HOST}:${FLINK_JOB_MANAGER_PORT} \
    -c xgboost.Predict \
    predict-xgboost-flink-assembly-0.1-SNAPSHOT.jar"

echo $FLINK_CMD
exec $FLINK_CMD
