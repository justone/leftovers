(ns leftover.util
  (:require 
    [clojure.walk :as walk]))

(defn log
  "Logs data to the console."
  [s]
  (.log js/console s))

(defn p
  "Prints given arguments, and then returns the last one"
  [& values]
  (.log js/console (apply js->clj values))
  (last values))

(defn json->clj
  "Takes in json data and returns a keywordized version"
  [json]
  (walk/keywordize-keys (js->clj (.parse js/JSON json))))
