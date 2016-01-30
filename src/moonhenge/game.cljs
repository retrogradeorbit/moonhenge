(ns moonhenge.game
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
            [moonhenge.bullet :as bullet]
            [moonhenge.enemy :as enemy]
            [moonhenge.explosion :as explosion]
            [moonhenge.moon :as moon]
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
    :max-speed 0.15
    :thrust 0.014
    :drag 0.99
    :rotate-speed 0.09

    ;; bullets
    :fire-delay 5
    :bullet-speed 20
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

(defn run [canvas player]
  (go
    (loop [frame 0
           heading Math/PI
           fire-cooldown 0]
      (<! (e/next-frame))

      (when (events/is-pressed? :p)
        (while (events/is-pressed? :p) (<! (e/next-frame)))
        (enemy/spawn canvas :world))

      (when (events/is-pressed? :m)
        (while (events/is-pressed? :m) (<! (e/next-frame)))
        (moon/spawn canvas))

      (s/set-rotation! player (+ heading Math/PI))
      (let [pos (:pos @state)
            x (vec2/get-x pos)
            y (vec2/get-y pos)]
        (s/set-pivot! (-> canvas :layer :world) x y)
        (s/set-pivot! (-> canvas :layer :player) x y)
        (s/set-pivot! (-> canvas :layer :float) x y)
        (s/set-pos! player x y)
        ;(log "player" x y)
        )

      (starfield/set-position!
       (-> state deref :pos (vec2/scale (/ -1 6)) vec2/as-vector))

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

      (if (or (quit?)
              ;; only check for collision every third frame (HACK WARNING)
              (if (zero? (mod frame 3))
                false
                (enemy/any-collide? (:pos @state))))
        ;; player quits
        (<! (explosion/explosion canvas player))

        ;; next loop
        (recur
         (inc frame)

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
                           (:pos @state) heading
                           (:bullet-speed @state)
                           (:bullet-life @state))
             (:fire-delay @state))

           (if (not (fire?))
             0
             (max 0 (dec fire-cooldown))))

         )))))
