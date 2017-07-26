(ns snakepit.core
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.java.shell :refer [sh]]))

(def conn (rmq/connect))
(def ch (lch/open conn))

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "Clojure Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn basic-comm []
  (do
    (lq/declare ch "py2clj" {:exclusive false :auto-delete false})
    (lq/declare ch "clj2py" {:exclusive false :auto-delete false})

    (println (format "Clojure Connected. Channel id: %d" (.getChannelNumber ch)))
    (lc/subscribe ch "py2clj" message-handler {:auto-ack true})
    (lb/publish ch "" "clj2py" "Hello from Clojure!" {:content-type "text/plain" :type "greetings.hi"})
    (Thread/sleep 5000)
    (println "Clojure Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))

(defn -main
  [& args]
  (cond
    (some #{"json"} args)
      (println "not implemented")
    :else
    (basic-comm)))
