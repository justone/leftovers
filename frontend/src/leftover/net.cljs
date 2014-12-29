(ns leftover.net
  (:require-macros  [cljs.core.async.macros :refer  [go]])
  (:require [cljs.core.async :refer [put! chan <! >! close!]]
            [goog.net.XhrIo :as xhr]))

(defn GET
  [url]
  (let [resp (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go (>! resp res)
                      (close! resp)))))
    resp))

(defn POST
  [url content]
  (xhr/send
    url
    (fn [event] (let [res (-> event .-target .getResponseText)] ))
    "POST"
    (.stringify js/JSON (clj->js content))))

