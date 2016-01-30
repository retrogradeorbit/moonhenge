(ns moonhenge.game
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
            [moonhenge.bullet :as bullet]
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

    ;; bullets
    :fire-delay 50
    :bullet-speed 13
    :bullet-life 50
    }))

(defn left? []
  (events/is-pressed? :left))

(defn right? []
  (events/is-pressed? :right))

(defn thrust? []
  (events/is-pressed? :up))

(defn quit? []
  (events/is-pressed? :q))

(defn fire? []
  (or
   (events/is-pressed? :space)
   (events/is-pressed? :z)))

(def explosion-frames [:explosion-0 :explosion-1 :explosion-2
                       :explosion-3 :explosion-4 :explosion-5])

(def explosion-speed 6)

(defn explosion [canvas layer entity]
  (go
    (m/with-sprite canvas layer
      [explosion (s/make-sprite (first explosion-frames) :scale 4)]
      (loop [n 0]
        (s/set-texture! explosion (get explosion-frames n))

        ;; when explosion is maximum size, we can disappear the
        ;; underlying entity
        (when (= 3 n)
          (s/set-visible! entity false))

        (<! (e/wait-frames explosion-speed))
        (when (< n (dec (count explosion-frames)))
          (recur (inc n)))))))

(defn run [canvas player]
  (go
    (loop [heading 0
           fire-cooldown 0]
      (<! (e/next-frame))

      (s/set-rotation! player heading)

      (starfield/set-position! (-> state deref :pos vec2/as-vector))

      ;; shoot?


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

      (if (quit?)
        ;; player quits
        (<! (explosion canvas :world player))

        ;; next loop
        (recur
         (cond
           (left?)
           (- heading (:rotate-speed @state))

           (right?)
           (+ heading (:rotate-speed @state))

           :default
           heading)

         (if (and (fire?) (zero? fire-cooldown))
           (do
             (bullet/spawn canvas :world
                           (vec2/zero) heading
                           (:bullet-speed @state)
                           (:bullet-life @state))
             (:fire-delay @state))

           (if (not (fire?))
             0
             (max 0 (dec fire-cooldown))))

         )))))
