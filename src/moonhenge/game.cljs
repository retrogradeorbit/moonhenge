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

(def state
  (atom
   {
    :pos (vec2/zero)
    :vel (vec2/zero)
    :acc (vec2/zero)
    :max-speed 0.05
    :thrust 0.003
    :drag 0.99
    :rotate-speed 0.08
    }))

(defn left? []
  (events/is-pressed? :left))

(defn right? []
  (events/is-pressed? :right))

(defn thrust? []
  (events/is-pressed? :up))

(defn quit? []
  (events/is-pressed? :q))

(defn run [canvas player]
  (go
    (loop [heading 0]
      (<! (e/next-frame))

      (s/set-rotation! player heading)

      (starfield/set-position! (-> state deref :pos vec2/as-vector))

      ;; update atom pos?
      (swap! state
             (fn [s]
               (-> s
                   ;; update
                   (update-in [:vel] #(vec2/add % (:acc s)))
                   (update-in [:pos] #(vec2/add % (:vel s)))

                   ;; drag
                   (update-in [:vel] #(vec2/scale % (:drag s)))

                   ;; thrust
                   (update-in [:acc]
                              #(if (thrust?)
                                 ;; thrusting
                                 (-> %
                                     (vec2/add
                                      (vec2/rotate
                                       (vec2/vec2 0 (:thrust s))
                                       heading))
                                     (vec2/truncate (:max-speed s))
                                     )

                                 ;; nothing
                                 (vec2/zero)

                                 )))))

      (when-not (quit?)
        (recur
         (cond
           (left?)
           (- heading (:rotate-speed @state))

           (right?)
           (+ heading (:rotate-speed @state))

           :default
           heading))))))
