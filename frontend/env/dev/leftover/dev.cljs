(ns leftover.dev
  (:require [leftover.core :as core]
            [figwheel.client :as fw]))

(fw/start {:on-jsload (fn [] (core/main))})

(core/main)
