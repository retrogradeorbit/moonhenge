(ns moonhenge.enemy
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
            [moonhenge.boid :as boid]
            [moonhenge.bullet :as bullet]
            [moonhenge.explosion :as explosion]
            [moonhenge.moon :as moon]
            [moonhenge.rune :as rune]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def enemy-choice [:enemy-0 :enemy-1 :enemy-2 :enemy-3
                   :enemy-4 :enemy-5 :enemy-6])

(def enemies (atom {}))

(defn empty-enemies! []
  (reset! enemies {})
  )

(defn shot?
  "returns nil if the enemy is not shot. else returns
  the key of the bullet that hit it"
  [enemy]
  (let [pos (s/get-pos enemy)
        bullets @bullet/bullets
        bull (first
              (filter #(< (% 1) 200)
                      (map (fn [[k v]]
                             (do
                               (let [ds (-> v s/get-pos (vec2/distance-squared pos))]
                                 ;(log "ds:" ds)
                                 [k ds])))
                           bullets)))]
    (first bull)))

(defn any-collide?
  "return nil if no enemy collides with this position
  else return the enemy key that collides with it"
  [pos]
  ;(log "any colide" pos)
  (let [result (first (first
                       (filter #(< (% 1) 800)
                               (map (fn [[k v]]
                                      (let [ds (-> v s/get-pos (vec2/distance-squared pos))]
                                        ;(log "ds:" k ds "," pos "," (s/get-pos v))
                                        [k ds]))
                                    @enemies))))]

    (when result (log "result:" result pos (s/get-pos (result @enemies))))
    result))

(defn spawn [canvas layer state-atom kill]
  (go
    (let [start-pos (-> @state-atom
                           :pos
                           (vec2/add (vec2/scale (vec2/random-unit) 2000)))]
      (m/with-sprite canvas layer
        [enemy (s/make-sprite (rand-nth enemy-choice) :scale 4
                              :x (vec2/get-x start-pos)
                              :y (vec2/get-y start-pos))]
        (let [ekey (keyword (gensym))]
          (swap! enemies assoc ekey enemy)
          (loop [b {:mass 10.0
                    :pos start-pos
                    :vel (vec2/zero)
                    :max-force 1.0
                    :max-speed 3.0}]
            (<! (e/next-frame))

            (s/set-pos! enemy (:pos b))
            (s/set-rotation!
             enemy
             (+ (vec2/heading (:vel b)) (/ Math/PI 2)))

            (if-let [bull (shot? enemy)]
              ;; die
              (do
                (explosion/explosion canvas enemy)
                (swap! enemies dissoc ekey)
                (bullet/remove! bull))

              ;; still alive
              (do ;(log kill "=" @kill)
                  (when (not @kill)
                    (recur
                     (if (:alive @state-atom)
                       (boid/seek b (:pos @state-atom))
                       (boid/wander b 6 3 0.1))

                     ))))))))))

(def levels
  [
   [1 3 ;10 30 70 100
    ]
   [3 5 ;50 100 300
    ]
   [5 9 ;100 300 500
    ]
   ])

(defn level [canvas state-atom kill]
  (go
    (<! (timeout 300))

    (loop [[lh & lt] levels
           level-num 0]
      (loop [[h & t] lh]
        (loop [n h]
          (spawn canvas :world state-atom kill)
          (<! (timeout 1000))
          (when (and (pos? n) (not @kill))
            (recur (dec n))))

        ;; TODO: wait for no enemies
        (while (not (empty? @enemies))
          (<! (timeout 1000)))


        (when (and (not @kill) t) (recur t)))

      ;; level complete
      ;; add rune
      (rune/add-rune! level-num)

      (when (and lt (not @kill)) (recur lt (inc level-num))))

    ;; game complete
    ;; moon
    (moon/spawn canvas)


    )
  )
