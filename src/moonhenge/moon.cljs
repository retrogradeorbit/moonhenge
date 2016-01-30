(ns moonhenge.moon
  (:require [moonhenge.assets :as assets]
            [moonhenge.boid :as boid]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(defn druid [canvas start-pos kill-atom]
  (go
    (m/with-sprite canvas :moon
      [astro (s/make-sprite :astronaut-1 :scale 4)]
      (loop [frame 0
             b {:mass 10.0
                :pos start-pos
                :vel (vec2/zero)
                :max-force 1.0
                :max-speed 1.0}]
        (s/set-pos! astro (:pos b))
        (s/set-texture! astro
         (nth [:astronaut-1 :astronaut-2] (mod (/ frame 13) 2)))
        (<! (e/next-frame))
        (when-not @kill-atom
          (recur (inc frame)
                 (boid/arrive b (vec2/vec2 0 150) 300)))))))

(defn spawn [canvas]
  (go
    (let [kill (atom false)]
      (m/with-sprite canvas :moon
        [moon (s/make-sprite :moon-surface
                             :scale 6
                             :y 1000)
         henge (s/make-sprite :henge
                              :scale 4
                              :x -20
                              :y 800)]

        ;; bring on moon surface and henge
        (loop [y 1000]
          (s/set-pos! moon 0 y)
          (s/set-pos! henge -20 (- y 200))
          (<! (e/next-frame))
          (when (> y 300)
            (recur (dec y))))

        ;; bring in some druids
        (loop [num 50]
          (druid canvas
                 (-> (vec2/vec2 0 1000)
                     (vec2/rotate
                      (+ (/ Math/PI -2) (* (/ Math/PI 20)
                                           (rand-nth (range 3 18))))))
                 kill)
          (<! (timeout 200))
          (when (pos? num)
            (recur (dec num))))

        ;; wait for the druids to finish arriving
        (<! (timeout 30000))

        ;; kill the druid go-threads
        (reset! kill true)))))
