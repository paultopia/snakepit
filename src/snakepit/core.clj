(ns snakepit.core
  (:gen-class)
  (:require [snakepit.json :refer [json-comm]]
            [snakepit.basic :refer [basic-comm]]
            [snakepit.async :refer [async-comm]]
            [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]))



(defn shutdown [channel connection]
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
      (some #{"async"} args)
      (async-comm ch)
      :else
      (basic-comm ch))
    (shutdown ch conn)))
