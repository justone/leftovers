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
(defonce app-state (atom {:state :enter-payment
                      :data {}}))

; if using figwheel, connect to separate port
; otherwise, use the same port as source
(def source-port (.. js/document -location -port))
(if (= "3449" source-port)
  (def port "8000")
  (def port source-port))

(def base-url (str "http://" (.. js/document -location -hostname) ":" port))

; handle events from the UI
(go (loop []
      (let [action (<! actions)]
        (case (:type action)
          :enter-payment (swap! app-state assoc :state :enter-payment)
          :view-history (swap! app-state assoc :state :view-history)
          :add-payment (net/POST
                         (str base-url "/conn/foo")
                         ; need to stringify the boolean value in the action
                         (clj->js (util/stringify-in action [:args :date-change]))))
        (recur))))

; handle events from the server
(go
  (loop []
    ; (util/log "getting more data")
    (let [json (<! (net/GET (str base-url "/conn/foo/bar")))
          cleaned (util/json->clj json)]
      ; (util/log (clj->js cleaned))
      (if (seq cleaned)
        (swap! app-state assoc :data cleaned)))
    (recur)))

; call for initial data, up to 5 seconds
(go
  (loop [times 10]
    (<! (util/timeout 500))
    (util/log "calling for data")
    (net/POST (str base-url "/conn/foo") {:type "load-info"})
    (if (and (> times 0) (empty? (:data @app-state)))
      (recur (dec times)))))

(defn main
  []
  (om/root
    ui/main-view
    app-state
    {:target (. js/document (getElementById "app"))
     :shared {:actions actions}}))


(main)
