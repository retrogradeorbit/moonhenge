(ns moonhenge.starfield
  (:require [moonhenge.assets :as assets]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def num-stars 150)
(def stars-set (sort-by
                :z
                (map (fn [n]
                       (let [depth (math/rand-between 0 5)]
                         {:x (math/rand-between 0 2048)
                          :y (math/rand-between 0 2048)
                          :z (+ depth (rand))
                          :depth depth})) (range num-stars))))
(def star-choice [:star-1 :star-2 :star-3 :star-4 :star-5 :star-6])

(def scale 4)

(def position (atom [0 0]))

(defn set-position! [pos]
  (reset! position pos))

(defn get-sprites []
  (for [{:keys [x y z depth]} stars-set]
    (s/make-sprite
     (nth star-choice depth)
     :x (* scale x)
     :y (* scale y)
     :scale scale)))


(defn set-star-positions! [stars [xp yp]]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)
        hw (/ w 2)
        hh (/ h 2)]
    (doall
     (map
      (fn [{:keys [x y z] :as old} sprite]
        (s/set-pos! sprite
                    (- (mod (+ (* 4 x) (* xp z)) w) hw)
                    (- (mod (+ (* 4 y) (* yp z)) h) hh)))
      stars-set
      stars))))


(defn star-thread [stars]
  (go
    (loop [c 0]
      (<! (e/next-frame))
      (set-star-positions! stars @position)
      (recur (inc c)))))
