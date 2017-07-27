(ns snakepit.basic
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "Clojure Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn basic-comm [channel]
  (do
    (lq/declare channel "py2clj" {:exclusive false :auto-delete false})
    (lq/declare channel "clj2py" {:exclusive false :auto-delete false})

    (println (format "Clojure Connected. Channel id: %d" (.getChannelNumber channel)))
    (lc/subscribe channel "py2clj" message-handler {:auto-ack true})
    (lb/publish channel "" "clj2py" "Hello from Clojure!" {:content-type "text/plain"})))
