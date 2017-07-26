(ns snakepit.core
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lch]
            [langohr.queue     :as lq]
            [langohr.consumers :as lc]
            [langohr.basic     :as lb]
            [clojure.data.json :as json]))

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
    (lb/publish ch "" "clj2py" "Hello from Clojure!" {:content-type "text/plain"})
    (Thread/sleep 5000)
    (println "Clojure Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))

;; JSON stuff below.

(def jsonstring (json/write-str [1, 2, 3]))

(defn json-message-handler
  [ch metadata ^bytes payload]
  (println "Clojure takes your Python and doubles it. "
           (mapv #(* 2 %)
                 (json/read-str (String. payload "UTF-8")))))

(defn json-comm []
  (do
    (lq/declare ch "jsonpy2clj" {:exclusive false :auto-delete false})
    (lq/declare ch "jsonclj2py" {:exclusive false :auto-delete false})
    (println (format "Clojure Connected. Channel id: %d" (.getChannelNumber ch)))
    (lc/subscribe ch "jsonpy2clj" json-message-handler {:auto-ack true})
    (lb/publish ch "" "jsonclj2py" jsonstring {:content-type "application/json"})
    (Thread/sleep 5000)
    (println "Clojure Disconnecting...")
    (rmq/close ch)
    (rmq/close conn)))


(defn -main
  [& args]
  (cond
    (some #{"json"} args)
    (json-comm)
    :else
    (basic-comm)))
