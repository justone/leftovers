(ns leftover.core
  (:require-macros  [cljs.core.async.macros :refer  [go]])
  (:require 
    [leftover.net :as net]
    [leftover.ui :as ui]
    [leftover.util :as util]
    [om.core :as om :include-macros true]
    [cljs.core.async :refer [chan <!]]
    [clojure.walk :as walk]))

(enable-console-print!)

(def actions (chan))
(def app-state (atom {:state :loading
                      :data {}}))

(go (loop []
      (let [action (<! actions)]
        (case (:type action)
          :enter-payment (swap! app-state assoc :state :enter-payment)
          :view-history (swap! app-state assoc :state :view-history)
          :add-payment (net/POST "http://localhost:8000/conn/foo" (clj->js action)))
        (recur))))

(go
  (loop []
    (util/log "getting more data")
    (let [json (<! (net/GET "http://localhost:8000/conn/foo/baz"))
          cleaned (util/json->clj json)]
      (util/log (clj->js cleaned))
      (if (seq cleaned)
        (swap! app-state assoc :data cleaned)))
    (recur)))

(om/root
  ui/main-view
  app-state
  {:target (. js/document (getElementById "app"))
   :shared {:actions actions}})
