#!/bin/bash

/usr/local/sbin/rabbitmq-server &
sleep 5
python talktoclojure.py &
PYPID=$!
sleep 2

if [[ "$@" == 'json' ]]; then
	lein run json
elif [[ "$@" == 'async' ]]; then
	lein run async
else
	lein run
fi
kill $PYPID
/usr/local/sbin/rabbitmqctl stop
