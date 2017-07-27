(ns snakepit.async
  "wiring rabbitmq up with core.async"
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.data.json :as json]
            [clojure.core.async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout go-loop]]))

(def receiver-async-chan (chan))
(def sender-async-chan (chan))

(defn say [async-channel message]
  (go
    (>! async-channel message)))

(defn dispatch-messages! [mq-channel async-channel]
  (go-loop []
    (let [{:keys [queue content]} (<! async-channel)]
      (lb/publish mq-channel "" queue content {:content-type "text/plain"}))
    (recur)))

(defn message-handler
  [ch metadata ^bytes payload]
  (println "Clojure takes your Python and doubles it. "
           (mapv #(* 2 %)
                 (json/read-str (String. payload "UTF-8")))))

(defn async-comm [mq-channel]
  (let [send! (partial say sender-async-chan)]
    (dispatch-messages! mq-channel sender-async-chan)
    (lq/declare mq-channel "jsonpy2clj" {:exclusive false :auto-delete false})
    (lq/declare mq-channel "jsonclj2py" {:exclusive false :auto-delete false})
    (println (format "Clojure Connected. Channel id: %d" (.getChannelNumber mq-channel)))
    (lc/subscribe mq-channel "jsonpy2clj" message-handler {:auto-ack true})
    (send! {:queue "jsonclj2py" :content (json/write-str [9, 10, 11])})))
