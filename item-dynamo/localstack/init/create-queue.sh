#!/bin/bash

awslocal sqs create-queue \
  --queue-name item-created-queue

awslocal sqs create-queue \
  --queue-name item-created-dlq

awslocal sqs create-queue \
  --queue-name item-queue.fifo \
  --attributes FifoQueue=true,ContentBasedDeduplication=true