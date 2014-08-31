(defproject leftover "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [racehub/om-bootstrap "0.2.6"]
                 [om "0.7.1"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-figwheel  "0.1.4-SNAPSHOT"]]

  :source-paths ["src"]

  ; figwheel config
  :figwheel {
             :http-server-root "public"
             :port 3449
             :css-dirs ["resources/public/css"]}

  :cljsbuild { 
    :builds [{:id "leftover"
              :source-paths ["src/leftover" "src/figwheel"]
              :compiler {
                :output-to "resources/public/leftover.js"
                :output-dir "resources/public/out"
                :optimizations :none
                :source-map true}}]})
