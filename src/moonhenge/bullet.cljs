(ns moonhenge.bullet
  (:require [moonhenge.assets :as assets]
            [moonhenge.starfield :as starfield]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def frames [:bullet-1 :bullet-2 :bullet-3])
(def anim-speed 3)

(def bullets (atom {}))

(defn remove! [bkey]
  (swap! bullets dissoc bkey))

(defn spawn [canvas layer pos heading speed live-for]
  (let [update (-> (vec2/vec2 0 1)
                   (vec2/rotate heading)
                   (vec2/scale speed))
        [x y] [(vec2/get-x pos) (vec2/get-y pos)]]
    (go
      (m/with-sprite canvas layer
        [bullet (s/make-sprite (first frames)
                               :scale 4 :rotation (+ heading Math/PI)
                               :x x :y y)]
        (let [bkey (keyword (gensym))]
          (swap! bullets assoc bkey bullet)

          (loop [n live-for
                 pos pos]
            (s/set-pos! bullet pos)
            (<! (e/next-frame))

            (s/set-texture! bullet (frames (mod n (count frames))))

            (when (and (pos? n) (@bullets bkey))
              (recur (dec n) (vec2/add pos update)))

            ;; bullet dies
            (swap! bullets dissoc bkey)

            ))))))
