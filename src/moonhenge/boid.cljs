(ns moonhenge.boid
  (:require [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.console :refer [log]]))

(def example
  {:mass 10.0
   :pos (vec2/vec2 0 0)
   :vel (vec2/vec2 0 0)
   :max-force 1.0
   :max-speed 4.0})

(defn seek [{:keys [mass pos vel max-force max-speed] :as boid} target]
  (let [desired-vel
        (-> pos
            (vec2/direction target)
            (vec2/scale max-speed))
        steering (-> desired-vel
                     (vec2/sub vel)
                     (vec2/truncate max-force))
        accel (vec2/scale steering (/ 1 mass))
        new-vel (-> accel
                    (vec2/add vel)
                    (vec2/truncate max-speed))

        new-pos (vec2/add pos new-vel)]
    (assoc boid
           :pos new-pos
           :vel new-vel)))

(defn arrive [{:keys [mass pos vel max-force max-speed] :as boid} target slowing-distance]
  (let [target-offset (vec2/sub target pos)
        distance (+ 0.0001 (vec2/magnitude target-offset))
        ramped-speed (* max-speed (/ distance slowing-distance))
        clipped-speed (min ramped-speed max-speed)
        desired-vel (vec2/scale target-offset (/ clipped-speed distance))
        steering (-> desired-vel
                     (vec2/sub vel)
                     (vec2/truncate max-force))
        accel (vec2/scale steering (/ 1 mass))
        new-vel (-> accel
                    (vec2/add vel)
                    (vec2/truncate max-speed))
        new-pos (vec2/add pos new-vel)]
    (assoc boid
           :pos new-pos
           :vel new-vel)))

;;
;; wander helpers
;;
(defn constrain
  "old-poss is the last position constrained to the circle
  delta is a vector of the change
  center is the circles center
  radius is the radius of the circle
  returns the vector from the boid to the new point on the circle.
  "
  [pos center radius]
  (-> pos
      (vec2/sub center)
      (vec2/unit)
      (vec2/scale radius)
      (vec2/add center)))

(defn wander [{:keys [mass pos vel max-force max-speed last-steer]
               :as boid}
              d r delta]
  (let [last-steer (or last-steer
                       ;; front of circle to initialise
                       (vec2/vec2 (+ d r) 0))
        delta-vec (-> (vec2/random-unit)
                      (vec2/scale delta))
        new-steer (-> last-steer
                      (vec2/add delta-vec)
                      (constrain (vec2/vec2 d 0) r))
        direction (if (vec2/almost (vec2/zero) vel)
                    (vec2/random-unit)
                    (vec2/unit vel))

        ;; rotate into frame of reference of boid heading
        rotated-new-steer (vec2/rotate new-steer (vec2/heading direction))
        steering (-> rotated-new-steer
                     (vec2/sub vel)
                     (vec2/truncate max-force))
        accel (vec2/scale steering (/ 1 mass))
        new-vel (-> accel
                    (vec2/add vel)
                    (vec2/truncate max-speed))
        new-pos (vec2/add pos new-vel)]
    (assoc boid
           :pos new-pos
           :vel new-vel
           :last-steer new-steer)))
