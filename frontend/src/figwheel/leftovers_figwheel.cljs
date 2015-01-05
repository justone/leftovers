(ns leftovers-figwheel
  (:require [figwheel.client :as fw]))

; reload main UI when new js comes in
(fw/start {:on-jsload (fn [] (leftover.core/main))})
