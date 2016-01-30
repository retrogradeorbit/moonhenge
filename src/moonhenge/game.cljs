(ns moonhenge.game
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
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

(def rotate-speed 0.08)

(def state
  (atom
   {
    :pos (vec2/zero)
    :vel (vec2/zero)
    }))

(defn left? []
  (events/is-pressed? :left))

(defn right? []
  (events/is-pressed? :right))

(defn quit? []
  (events/is-pressed? :q))

(defn run [canvas player]
  (go
    (loop [heading 0]
      (<! (e/next-frame))

      (s/set-rotation! player heading)

      (starfield/set-position! (-> state deref :pos vec2/as-vector))

      ;; update atom pos?
      (swap! state update-in [:pos] #(vec2/add % (vec2/vec2 0 0.1)) )

      (when-not (quit?)
        (recur
         (cond
           (left?)
           (- heading rotate-speed)

           (right?)
           (+ heading rotate-speed)

           :default
           heading))))))
