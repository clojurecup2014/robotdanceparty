(defproject robotdanceparty "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "robotdanceparty"
              :source-paths ["src"]
              :compiler {
                :output-to "robotdanceparty.js"
                :output-dir "out"
                :externs ["jaws.js" "howler.js"]
                :optimizations :none
                :source-map true}}]})
