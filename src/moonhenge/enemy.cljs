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
            [infinitelives.utils.sound :as sound]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def enemy-choice [:enemy-1 :enemy-2 :enemy-3
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
                        (vec2/add (vec2/scale (vec2/random-unit) 1000)))]
      (m/with-sprite canvas layer
        [enemy (s/make-sprite (rand-nth enemy-choice) :scale 4
                              :x (vec2/get-x start-pos)
                              :y (vec2/get-y start-pos))]
        (let [ekey (keyword (gensym))]
          (swap! enemies assoc ekey enemy)
          (loop [b (case (rune/num)
                     0 {:mass 10.0
                        :pos start-pos
                        :vel (vec2/zero)
                        :max-force 1.0
                        :max-speed 3.0}
                     1 {:mass 10.0
                        :pos start-pos
                        :vel (vec2/zero)
                        :max-force 1.0
                        :max-speed 3.5}
                     2 {:mass 10.0
                        :pos start-pos
                        :vel (vec2/zero)
                        :max-force 1.0
                        :max-speed 4}
                     3 {})]
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
              (do                       ;(log kill "=" @kill)
                (when (not @kill)
                  (recur
                   (if (:alive @state-atom)
                     (boid/seek b (:pos @state-atom))
                     (boid/wander b 6 3 0.1))

                   ))))))))))

(def levels
  [
   [1 3 5 7 9 10]
   [3 5 10 15 15 20 20]
   [5 9 20 40 40 60 60 100]])

(defn level [canvas state-atom kill]
  (go
    (<! (timeout 300))

    (loop [[lh & lt] levels
           level-num 1]
      (loop [[h & t] lh]
        (loop [n h]
          (spawn canvas :world state-atom kill)
          (let [to (/ (case level-num 1 1000 2 500 3 100) 60)]
            (loop [c to]
              (<! (e/next-frame))
              (when (and (not @kill) (pos? c))
                (recur (dec c)))))
          (when (and (> n 1) (not @kill))
            (recur (dec n))))

        ;; TODO: wait for no enemies
        (while (and (not (empty? @enemies))
                    (not @kill))
          (<! (e/next-frame)))

        ;; wave complete
        (when (not (empty? @enemies))
          (sound/play-sound :wave-respawn-0 0.5 false))

        (when (and (not @kill) t) (recur t)))

      ;; level complete
      ;; add rune
      (when (not @kill)
        (rune/add-rune! level-num)
        (swap! moonhenge.game/state #(-> %
                          (update-in [:max-speed] + 0.025)
                          (update-in [:thrust] + 0.025)
                          (update-in [:fire-delay] - 2.5)
                          (update-in [:bullet-life] + 10)))
        (sound/play-sound (rand-nth [:rune-0 :rune-1 :rune-2 :rune-3 :rune-4])
                          0.5 false)

        (when (and lt (not @kill)) (recur lt (inc level-num)))))

    ;; game complete
    ;; moon
    (when (not @kill)
      (loop [n (/ 3000 60)]
        (<! (e/next-frame))
        (when (pos? n) (recur (dec n))))

      (sound/play-sound :moon-rumble-0 0.8 false)
      (moon/spawn canvas)

      (<! (timeout 52000))
      (swap! moonhenge.game/state assoc :alive false)
      )))
