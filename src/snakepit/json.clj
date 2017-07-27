(ns snakepit.json
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.data.json :as json]))

(def jsonstring (json/write-str [1, 2, 3]))

(defn message-handler
  [ch metadata ^bytes payload]
  (println "Clojure takes your Python and doubles it. "
           (mapv #(* 2 %)
                 (json/read-str (String. payload "UTF-8")))))

(defn json-comm [channel]
  (do
    (lq/declare channel "jsonpy2clj" {:exclusive false :auto-delete false})
    (lq/declare channel "jsonclj2py" {:exclusive false :auto-delete false})
    (println (format "Clojure Connected. Channel id: %d" (.getChannelNumber channel)))
    (lc/subscribe channel "jsonpy2clj" message-handler {:auto-ack true})
    (lb/publish channel "" "jsonclj2py" jsonstring {:content-type "application/json"})))
