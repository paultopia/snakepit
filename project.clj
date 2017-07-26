(defproject snakepit "0.1.0-SNAPSHOT"
  :description "quick demo of wiring together clj and py via rabbitmq"
  :url "https://github.com/paultopia/snakepit"
  :license {:name "committed to public domain"
            :url "https://creativecommons.org/share-your-work/public-domain/cc0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.novemberain/langohr "3.6.1"]]
  :main ^:skip-aot snakepit.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
