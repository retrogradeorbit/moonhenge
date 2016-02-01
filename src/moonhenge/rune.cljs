(ns moonhenge.rune
  (:require [moonhenge.assets :as assets]
            [moonhenge.boid :as boid]
            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.vec2 :as vec2]
            [infinitelives.utils.events :as events]
            [infinitelives.utils.console :refer [log]]
            [cljs.core.async :refer [<! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(def rune-state
  (atom
   {
    :runes [false false false]

    }))

(defn set-rune! [num state]
  (swap! rune-state assoc-in [:runes num] state)
  )

(defn toggle-rune! [num]
  (swap! rune-state update-in [:runes num] not)
  (log (str @rune-state))
  )

(defn add-rune! [num]
  (swap! rune-state assoc :runes [(> num 0) (> num 1) (> num 2)]))

(defn reset-runes! []
  (reset! rune-state {
    :runes [false false false]
    }))

(defn num []
  (count (filter identity (:runes @rune-state))))

(defn run [canvas]
  (go
    (m/with-sprite-set canvas :runes
      [runes (vec (map #(s/make-sprite (keyword (str "rune-" (inc %)))
                                       :visibility false
                                       :scale 3
                                       :x (case %
                                            0 -300
                                            1 0
                                            2 300)
                                       :y 400)
                       (range 3)))]
      (loop []
        (doall
         (map-indexed
          #(do
             ;(log %1 %2 (runes %1))
             (s/set-visible! (runes %1) %2)
             )
          (:runes @rune-state)))

        (<! (e/next-frame))
        (recur)

        )
      ))
  )
