(ns moonhenge.explosion
  (:require [moonhenge.assets :as assets]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.console :refer [log]]
            [infinitelives.utils.vec2 :as vec2]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def explosion-frames [:explosion-0 :explosion-1 :explosion-2
                       :explosion-3 :explosion-4 :explosion-5])

(def explosion-speed 6)

(defn explosion [canvas entity]
  (go
    (let [pos (s/get-pos entity)
          x (vec2/get-x pos)
          y (vec2/get-y pos)]
      (m/with-sprite canvas :float
        [explosion (s/make-sprite (first explosion-frames)
                                  :scale 4 :x x :y y
                                  :rotation (* (rand) Math/PI 2))]
        (loop [n 0]
          (s/set-texture! explosion (get explosion-frames n))

          ;; when explosion is maximum size, we can disappear the
          ;; underlying entity
          (when (= 3 n)
            (s/set-visible! entity false))

          (<! (e/wait-frames explosion-speed))
          (when (< n (dec (count explosion-frames)))
            (recur (inc n))))))))
