(defproject leftover "0.1.0-SNAPSHOT"
  :description "This is a little app to keep track of how much money is left over."
  :url "https://github.com/justone/leftovers"

  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [figwheel "0.2.3-SNAPSHOT"]
                 [org.omcljs/om "0.8.8"]
                 [racehub/om-bootstrap "0.4.0"]
                 [prismatic/om-tools "0.3.10"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-figwheel "0.2.3-SNAPSHOT"]]

  :source-paths ["src"]

  ; figwheel config
  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]}

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src" "env/dev"]
              :compiler {
                :output-to "resources/public/leftover.js"
                :output-dir "resources/public/out"
                :optimizations :none
                :cache-analysis true
                :source-map true}}
             {:id "prod"
              :source-paths ["src" "env/prod"]
              :compiler {
                :output-to "resources/prod/leftover.js"
                :output-dir "resources/prod/out"
                :optimizations :advanced
                :cache-analysis true
                :source-map "resources/prod/leftover.js.map"}}]})
