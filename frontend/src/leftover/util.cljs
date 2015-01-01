(ns leftover.util
  (:require 
    [cljs.core.async :refer [chan close!]]
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

(defn timeout
  "Returns a channel that is closed after the passed number of milliseconds"
  [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))
