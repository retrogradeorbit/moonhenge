(ns moonhenge.titlescreen
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


(def scale 4)
(def press-any-key-y 200)

(defn title
  "display titlescreen and wait for keypress"
  [canvas]
  (go
    (loop [c 0]
      (<! (e/next-frame))

    ;;;;;; move stars...


      (when-not (events/any-pressed?)
        (recur (inc c))))))

(defn move-titles
  "move titles out of the way"
  [canvas title-words press-key press-key-shadow]
  (go
    (loop [c 1]
      (<! (e/next-frame))

      (s/set-pos! title-words 0 (- -180 c))
      (s/set-pos! press-key 0 (+ press-any-key-y c))
      (s/set-pos! press-key-shadow 0 (+ press-any-key-y c 4))
      (recur (* 1.1 c)))))

(defn run [canvas]
  (go
    (m/with-sprite canvas :ui
      [press-key-shadow
       (s/make-sprite :press-any-key-shadow :scale 2
                      :x 4
                      :y (+ press-any-key-y 4))
       press-key
       (s/make-sprite :press-any-key :scale 2
                      :y press-any-key-y
                      )
       title-words
       (s/make-sprite :title-words :scale 2
                      :x 0
                      :y -180)

       ship (s/make-sprite :ship :scale 4 :y 25)
       ]
      (<! (title canvas))
      (<! (move-titles canvas title-words press-key press-key-shadow)))))
