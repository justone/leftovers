(ns leftover.core
  (:require-macros  [cljs.core.async.macros :refer  [go]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <! >! close!]]
            [goog.net.XhrIo :as xhr]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]
            [om-bootstrap.table :as obt]))

(enable-console-print!)

(def app-state (atom {:state :loading
                      :data {}}))

(defn hist->tr
  [hist]
  (dom/tr
    ; (dom/td  (:when hist))
    (dom/td  (:location hist))
    (dom/td  (:amount hist))))

(defn view-history [data owner]
  (reify om/IRender
    (render [this]
      (dom/div {:class "col-sm-6 component"}
               (obt/table {:striped? true :bordered? true}
                          (dom/thead
                            (dom/tr
                              ; (dom/th  "When")
                              (dom/th  "Location")
                              (dom/th  "Amount")))
                          (map hist->tr (:previous-payments data)))
               ))))

(defn handle-change
  [e owner k]
  (om/set-state! owner k (.. e -target -value)))

(defn enter-payment [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:location ""
       :amount   ""})
    om/IRenderState
    (render-state [this {:keys [location amount] :as state}]
      (let [actions (om/get-shared owner :actions)]
        (dom/div {:class "col-sm-6 component"}
                 (dom/form
                   (obi/input {:type "text" :placeholder "Location" :value location :on-change #(handle-change % owner :location)})
                   (obi/input {:type "text" :placeholder "Amount" :value amount :on-change #(handle-change % owner :amount)})
                   (obb/button-group {:justified? true}
                                     (obb/button-group {} (obb/button { :bs-style "success" :on-click (fn [] (put! actions {:type :add-payment :args state})) } "Add")))))))))

(defn button-bar [app owner]
  (reify om/IRender
    (render [this]
      (let [actions (om/get-shared owner :actions)]
        (dom/div {:class "col-sm-6 component"}
                 (obb/button-group {:justified? true}
                                   (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (put! actions {:type :enter-payment})) } "Enter Payment"))
                                   (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (put! actions {:type :view-history})) } "View History"))))))))

(defn running-total [data owner]
  (reify om/IRender
    (render [this]
      (dom/h3 {:class "col-sm-6 currenttotal"} 
               (str "Current Total: " (apply - (:start-amount data) (map :amount (:previous-payments data))))))))

(defn main-view [app owner]
  (reify om/IRender
    (render [this]
      (dom/div
        (om/build running-total (:data app))
        (om/build button-bar app)
        (case (:state app)
          :loading ()
          :enter-payment (om/build enter-payment app)
          :view-history (om/build view-history (:data app))))))) 

(defn log [s]
  (.log js/console s))

(defn GET
  [url]
  (let [resp (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (-> event .-target .getResponseText)]
                  (go (>! resp res)
                      (close! resp)))))
    resp))

(def actions (chan))

(go (loop []
      (let [action (<! actions)]
        (case (:type action)
          :enter-payment (swap! app-state assoc :state :enter-payment)
          :view-history (swap! app-state assoc :state :view-history)
          :add-payment (.log js/console (clj->js action)))
        (recur))))

(defn keywordify
  [datamap]
  (into {} (for [[k v] datamap] [(keyword k) v])))

(defn cleanup
  [datamap]
  {:start-amount (get datamap "start-amount")
   :previous-payments (into [] (map keywordify (get datamap "previous-payments")))})

(go
  (let [data (<! (GET "http://localhost:8000/test"))
        cleaned (cleanup (js->clj (.parse js/JSON data)))]
    (swap! app-state assoc :state :enter-payment :data cleaned)))

(om/root
  main-view
  app-state
  {:target (. js/document (getElementById "app"))
   :shared {:actions actions}})
