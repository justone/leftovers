(ns leftover.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-bootstrap.button :as obb]
            [om-bootstrap.input :as obi]))

(enable-console-print!)

(def app-state (atom {:text "Hello world!"}))

(om/root
  (fn [app owner]
    (reify om/IRender
      (render [_]
        (dom/div {:class "col-sm-6"}
                 (obb/button-group {:justified? true}
                                   (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (js/alert "Add Receipt!")) } "Add Receipt"))
                                   (obb/button-group {} (obb/button { :bs-style "primary" :on-click (fn [] (js/alert "View History!")) } "View History")))
                 (dom/form
                   (obi/input {:type "text" :placeholder "Location"}))))))
  app-state
  {:target (. js/document (getElementById "app"))})
