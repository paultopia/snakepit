(ns snakepit.core
  (:gen-class)
  (:require [snakepit.json :refer [json-comm]]
            [snakepit.basic :refer [basic-comm]]
            [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))



(defn sync-shutdown [channel connection]
  (Thread/sleep 5000) ;; wait for messages to finish.
  (println "Clojure Disconnecting...")
  (rmq/close channel)
  (rmq/close connection))

(defn -main
  [& args]
  (let [conn (rmq/connect)
        ch (lch/open conn)]
    (cond
      (some #{"json"} args)
      (json-comm ch)
      :else
      (basic-comm ch))
    (sync-shutdown ch conn)))
