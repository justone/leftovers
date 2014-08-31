(ns leftover.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-bootstrap.button :as obb]))

(enable-console-print!)

(def app-state (atom {:text "Hello world!"}))

(om/root
  (fn [app owner]
    (reify om/IRender
      (render [_]
        (dom/div #js {:id "content" :className "col-sm-6"}
                 (obb/toolbar {}
                              (obb/button {
                                           :bs-style "primary"
                                           :onClick (fn [] (js/alert "test"))
                                           }
                                          (:text app)))))))
  app-state
  {:target (. js/document (getElementById "app"))})
