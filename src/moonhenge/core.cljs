(ns moonhenge.core
  (:require [moonhenge.assets :as assets]
            [moonhenge.titlescreen :as titlescreen]
            [moonhenge.starfield :as starfield]

            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]

            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(defonce canvas
  (c/init {:layers [:bg :stars :ui] :background 0x000000 :expand true}))



(defonce main-thread
  (go
    (<! (r/load-resources canvas :ui ["img/sprites.png" "img/sprites-2.png"]))

    (t/load-sprite-sheet!
     (r/get-texture :sprites :nearest)
     assets/sprites-assets)

    (t/load-sprite-sheet!
     (r/get-texture :sprites-2 :nearest)
     assets/sprites-2-assets)

    ;; starfield
    (m/with-sprite-set canvas :stars
      [stars (starfield/get-sprites)]
      (starfield/set-positions! stars 0)

      (<! (titlescreen/run canvas stars)))))
