#!/bin/bash

/usr/local/sbin/rabbitmq-server &
sleep 5
python talktoclojure.py &
PYPID=$!
sleep 2
lein run
kill $PYPID
/usr/local/sbin/rabbitmqctl stop
