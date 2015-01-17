(ns leftover.util
  (:require 
    [cljs.core.async :refer [chan close!]]
    [clojure.walk :as walk]))

(defn log
  "Logs data to the console."
  [s]
  (.log js/console s))

(defn d
  "Logs to the console and then returns the last value"
  ([v] (.log js/console (str v)) v)
  ([n v] (.log js/console (str n v)) v))

(defn stringify-in
  "Converts the value at the specified path to a string"
  [blob path]
  (assoc-in blob path (str (get-in blob path))))

(defn dissoc-in
  "Removes a nested element according to a sequence of keys"
  [m [k & ks]]
  (if-not ks
    (dissoc m k)
    (assoc m k (dissoc-in (m k) ks))))

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

(defn random-string
  [len]
  (let [ascii-codes (lazy-cat (range 48 57) (range 65 90) (range 97 122))
        chars (map char ascii-codes)
        rand-chars (take len (repeatedly #(rand-nth chars)))]
    (reduce str rand-chars)))
