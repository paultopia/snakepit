(ns snakepit.async
  "wiring rabbitmq up with core.async"
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.data.json :as json]
            [clojure.core.async
             :refer [>! <! >!! <!! go chan thread go-loop]]))

(defn say [async-channel message]
  (go
    (>! async-channel message)))

(defn dispatch-messages! [mq-channel async-channel]
  (go-loop []
    (let [{:keys [queue content]} (<! async-channel)]
      (lb/publish mq-channel "" queue (json/write-str content) {:content-type "application/json"}))
    (recur)))

(defn message-receiver
  [async-channel mq-channel metadata ^bytes payload]
  (let [message (json/read-str (String. payload "UTF-8"))]
    (go
      (>! async-channel message))))

(defn listen! [async-channel mq-channel queue]
  (let [handler (partial message-receiver async-channel)]
    (lc/subscribe mq-channel queue handler {:auto-ack true})))

(defn process-messages! [async-channel processor]
  (thread
    (loop []
      (let [message (<!! async-channel)]
        (processor message))
      (recur))))
;; should this be a plain loop in a thread that blocks, i.e. <!! rather than <! and thread not go?  or should it be go-loop?

(defn setup-async-comm [mq-channel, inqueue, outqueue]
  (let [receiver-async-chan (chan)
        sender-async-chan (chan)
        sender-func (partial say sender-async-chan)
        receiver-func (partial process-messages! receiver-async-chan)]
    (dispatch-messages! mq-channel sender-async-chan)
    (lq/declare mq-channel inqueue {:exclusive false :auto-delete false})
    (lq/declare mq-channel outqueue {:exclusive false :auto-delete false})
    (println "Clojure Connected.")
    (listen! receiver-async-chan mq-channel inqueue)
    {:send! sender-func :receive! receiver-func}))

(defn answer [msg]
  (println "Clojure takes your Python and doubles it. "
           (mapv #(* 2 %) msg)))

(defn async-comm [mq-channel]
  (let [{:keys [send! receive!]} (setup-async-comm mq-channel "jsonpy2clj" "jsonclj2py")]
    (send! {:queue "jsonclj2py" :content [9, 10, 11]})
    (receive! answer)))
