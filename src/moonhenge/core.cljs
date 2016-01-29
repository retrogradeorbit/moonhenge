(ns moonhenge.core
    (:require [infinitelives.pixi.canvas :as c]
              [infinitelives.pixi.events :as e]
              [infinitelives.pixi.resources :as r]
              [infinitelives.pixi.texture :as t]
              [infinitelives.pixi.sprite :as s]
              [cljs.core.async :refer [<!]])
    (:require-macros [cljs.core.async.macros :refer [go]]
                     [infinitelives.pixi.macros :as m]))

(defonce canvas
  (c/init {:layers [:bg :world :float :ui] :expand true :background 0x000000}))

(defonce main-thread
  (go
    (<! (r/load-resources canvas :bg ["img/bunny.png"]))

    (t/set-texture! :rabbit (r/get-texture :bunny :nearest))

    (m/with-sprite canvas :bg
      [rabbit (s/make-sprite :rabbit)]
      (loop [angle 0]
        (s/set-rotation! rabbit angle)
        (<! (e/next-frame))
        (recur (+ 0.1 angle))))))
