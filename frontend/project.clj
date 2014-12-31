(defproject leftover "0.1.0-SNAPSHOT"
  :description "This is a little app to keep track of how much money is left over."
  :url "https://github.com/justone/leftovers"

  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2511"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [figwheel "0.2.0-SNAPSHOT"]
                 [racehub/om-bootstrap "0.3.2"]
                 [om "0.8.0-beta5"]
                 [prismatic/om-tools "0.3.9"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-figwheel  "0.2.0-SNAPSHOT"]
            [com.cemerick/austin "0.1.4"]]

  :source-paths ["src"]

  ; figwheel config
  :figwheel {
             :http-server-root "public"
             :port 3449
             :css-dirs ["resources/public/css"]}

  :cljsbuild { 
    :builds [{:id "leftover"
              :source-paths ["src/leftover" "src/figwheel" "src/brepl"]
              :compiler {
                :output-to "resources/public/leftover.js"
                :output-dir "resources/public/out"
                :optimizations :none
                :source-map true}}]})
