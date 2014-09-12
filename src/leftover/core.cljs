(ns leftover.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]
            [om-bootstrap.table :as obt]))

(enable-console-print!)

(def app-state (atom {
                      :state "enter-payment"
                      :start-amount 524.15
                      :previous-payments [
                                          {:when "yesterday"
                                           :location "Rite-aid"
                                           :amount 15.22}
                                          {:when "last week"
                                           :location "Vons"
                                           :amount 105.41}
                                          {:when "earlier"
                                           :location "Starbucks"
                                           :amount 3.25}
                                          ]}))

(defn hist->tr
  [hist]
  (dom/tr
    (dom/td  (:when hist))
    (dom/td  (:location hist))
    (dom/td  (:amount hist))))

(defn view-history [app owner]
  (reify om/IRender
    (render [this]
      (dom/div {:class "col-sm-6 component"}
               (obt/table {:striped? true :bordered? true}
                          (dom/thead
                            (dom/tr
                              (dom/th  "When")
                              (dom/th  "Location")
                              (dom/th  "Amount")))
                          (map hist->tr (:previous-payments app)))
               ))))

(defn enter-payment [app owner]
  (reify om/IRender
    (render [this]
      (dom/div {:class "col-sm-6 component"}
               (dom/form
                 (obi/input {:type "text" :placeholder "Location"})
                 (obi/input {:type "text" :placeholder "Amount"})
                 (obb/button-group {:justified? true}
                                 (obb/button-group {} (obb/button { :bs-style "success" :on-click (fn [] (js/alert "Add")) } "Add"))))))))

(defn button-bar [app owner]
  (reify om/IRender
    (render [this]
      (dom/div {:class "col-sm-6 component"}
               (obb/button-group {:justified? true}
                                 (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (om/transact! app :state (fn [_] "enter-payment"))) } "Enter Payment"))
                                 (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (om/transact! app :state (fn [_] "view-history"))) } "View History")))))))

(defn main-view [app owner]
  (reify om/IRender
    (render [this]
      (dom/div
        (om/build button-bar app)
        (case (:state app)
          "enter-payment" (om/build enter-payment app)
          "view-history" (om/build view-history app)))))) 

(om/root
  main-view
  app-state
  {:target (. js/document (getElementById "app"))})
