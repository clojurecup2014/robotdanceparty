(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!")
  (println (aget js/jaws "Sprite")))


