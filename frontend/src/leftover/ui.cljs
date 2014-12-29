(ns leftover.ui
  (:require-macros  [cljs.core.async.macros :refer [go]])
  (:require 
    [om.core :as om :include-macros true]
    [om-tools.dom :as dom :include-macros true]
    [cljs.core.async :refer [put! chan <! >! close!]]
    [goog.string :as gstring]
    [om-bootstrap.button :as obb]
    [om-bootstrap.input :as obi]
    [om-bootstrap.table :as obt]))

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

(defn valid-string
  [name str]
  (cond
    (empty? str) {name "is empty"}
    (< (.-length str) 5) {name "is too short"})) 

(defn valid-number
  [name str]
  (cond
    (empty? str) {name "is empty"}
    (< (.-length str) 2) {name "is too short"})) 

(defn payment-errors
  [{:keys [location amount]}]
  (merge (valid-string :location location) (valid-number :amount amount)))

(defn add-payment
  [actions owner state]
  (let [errors (payment-errors state)]
    (if (empty? errors)
      (put! actions {:type :add-payment :args state})
      (om/set-state! owner :errors errors))))

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
                                     (obb/button-group {} (obb/button { :bs-style "success" :on-click (fn [] (add-payment actions owner state)) } "Add")))))))))

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
               (str "Current Total: " (gstring/format "$%.02f" (apply - (:start-amount data) (map :amount (:previous-payments data)))))))))

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
