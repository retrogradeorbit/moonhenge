(ns moonhenge.titlescreen
  (:require [moonhenge.assets :as assets]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
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
(def press-any-key-y 200)

(defn run [canvas]
  (go
    (m/with-sprite-set canvas :ui
      [stars (for [{:keys [x y z depth]} stars-set]
               (s/make-sprite
                (nth star-choice depth)
                :x (* scale x)
                :y (* scale y)
                :scale scale))]

      (m/with-sprite canvas :ui
        [press-key-shadow
         (s/make-sprite :press-any-key-shadow :scale 2
                        :x 4
                        :y (+ press-any-key-y 4))
         press-key
         (s/make-sprite :press-any-key :scale 2
                        :y press-any-key-y
                        )]

        (loop [c 0]
          (<! (e/next-frame))

          (let [w (.-innerWidth js/window)
                h (.-innerHeight js/window)
                hw (/ w 2)
                hh (/ h 2)
                speed 1]

            (doall
             (map
              (fn [{:keys [x y z] :as old} sprite]
                (s/set-pos! sprite
                            (- (mod (- (* 4 x) (* speed c z)) w) hw)
                            (- (mod (* 4 y) h) hh)))
              stars-set
              stars)))

          (recur (inc c)))))))
