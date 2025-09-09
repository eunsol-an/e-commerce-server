#!/bin/bash
set -e

DATA_DIR="/var/lib/kafka/data"

if [ -z "$(ls -A $DATA_DIR)" ]; then
  echo "Formatting storage directory for Kafka Node ID ${KAFKA_NODE_ID}..."

  kafka-storage format \
    --ignore-formatted \
    --cluster-id "${KAFKA_CLUSTER_ID}" \
    --config /etc/kafka/kraft/server.properties
else
  echo "Storage directory already formatted, skipping..."
fi

exec /etc/confluent/docker/run
