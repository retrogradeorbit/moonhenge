(ns moonhenge.enemy
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
            [moonhenge.boid :as boid]
            [moonhenge.bullet :as bullet]
            [moonhenge.explosion :as explosion]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def enemy-choice [:enemy-0 :enemy-1 :enemy-2 :enemy-3
                   :enemy-4 :enemy-5 :enemy-6])

(def enemies (atom {}))

(defn shot?
  "returns nil if the enemy is not shot. else returns
  the key of the bullet that hit it"
  [enemy]
  (let [pos (s/get-pos enemy)
        bullets @bullet/bullets
        bull (first
              (filter #(< (% 1) 200)
                      (map (fn [[k v]] [k (-> v s/get-pos (vec2/distance-squared pos))])
                           bullets)))]
    (first bull)))

(defn any-collide?
  "return nil if no enemy collides with this position
  else return the enemy key that collides with it"
  [pos]
  (first (first
          (filter #(< (% 1) 800)
                  (map (fn [[k v]] [k (-> v s/get-pos (vec2/distance-squared pos))])
                       @enemies)))))

(defn spawn [canvas layer]
  (go
    (m/with-sprite canvas layer
      [enemy (s/make-sprite (rand-nth enemy-choice) :scale 4)]
      (let [ekey (keyword (gensym))]
        (swap! enemies assoc ekey enemy)
        (loop [b {:mass 10.0
                  :pos (vec2/zero)
                  :vel (vec2/zero)
                  :max-force 1.0
                  :max-speed 2.0}]
          (<! (e/next-frame))

          (s/set-pos! enemy (:pos b))
          (s/set-rotation!
           enemy
           (+ (vec2/heading (:vel b)) (/ Math/PI 2)))

          (if-let [bull (shot? enemy)]
            ;; die
            (do
              (explosion/explosion canvas enemy)
              (bullet/remove! bull))

            ;; still alive
            (recur (boid/wander b 6 3 0.1))))))))
