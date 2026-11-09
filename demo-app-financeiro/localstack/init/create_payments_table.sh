#!/bin/bash
set -e

aws --endpoint-url=http://localhost:4566 dynamodb create-table \
  --table-name payments \
  --attribute-definitions \
    AttributeName=PK,AttributeType=S \
  --key-schema \
    AttributeName=PK,KeyType=HASH \
  --provisioned-throughput \
    ReadCapacityUnits=5,WriteCapacityUnits=5
