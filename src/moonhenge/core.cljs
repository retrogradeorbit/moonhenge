(ns moonhenge.core
  (:require [moonhenge.assets :as assets]
            [moonhenge.titlescreen :as titlescreen]
            [moonhenge.starfield :as starfield]
            [moonhenge.game :as game]
            [moonhenge.rune :as rune]
            [moonhenge.enemy :as enemy]

            [infinitelives.pixi.canvas :as c]
            [infinitelives.pixi.events :as e]
            [infinitelives.pixi.resources :as r]
            [infinitelives.pixi.texture :as t]
            [infinitelives.pixi.sprite :as s]
            [infinitelives.utils.math :as math]
            [infinitelives.utils.sound :as sound]

            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [infinitelives.pixi.macros :as m]))

(defonce canvas
  (c/init {:layers [:bg :stars :world :runes :moon :player :float :ui] :background 0x000000 :expand true :origins {:runes :bottom}}))


(defonce main-thread
  (go
    (<! (r/load-resources canvas :ui ["img/sprites.png"
                                      "sfx/shoot-0.ogg"
                                      "sfx/shoot-1.ogg"
                                      "sfx/shoot-2.ogg"
                                      "sfx/shoot-3.ogg"
                                      "sfx/portal-0.ogg"
                                      "sfx/moon-rumble-0.ogg"
                                      "sfx/rune-0.ogg"
                                      "sfx/rune-1.ogg"
                                      "sfx/rune-2.ogg"
                                      "sfx/rune-3.ogg"
                                      "sfx/rune-4.ogg"
                                      "sfx/blip-0.ogg"
                                      "sfx/blip-1.ogg"
                                      "sfx/blip-2.ogg"
                                      "sfx/explode-0.ogg"
                                      "sfx/explode-1.ogg"
                                      "sfx/explode-2.ogg"
                                      "sfx/explode-3.ogg"
                                      "sfx/explode-4.ogg"
                                      "sfx/explode-5.ogg"
                                      "sfx/explode-6.ogg"
                                      "sfx/explode-7.ogg"
                                      "sfx/explode-8.ogg"
                                      "sfx/explode-9.ogg"
                                      "sfx/wave-respawn-0.ogg"
                                      "music/moonhenge.ogg"]))

    (js/console.log (sound/play-sound :moonhenge 0.25 true))

    (t/load-sprite-sheet!
     (r/get-texture :sprites :nearest)
     assets/sprites-assets)

    ;; starfield
    (m/with-sprite-set canvas :stars
      [stars (starfield/get-sprites)]

      (m/with-sprite canvas :player
        [player (s/make-sprite :ship :scale 4 :y 0 :yhandle 0.25)]

        (starfield/set-star-positions! stars [0 0])

        ;; start the star update thread
        (starfield/star-thread stars)

        (rune/run canvas)

        (loop [kill (atom false)]
          (s/set-visible! player false)
          (<! (titlescreen/run canvas))
          (reset! kill true)
          (enemy/empty-enemies!)
          (<! (e/next-frame))
          (s/set-visible! player true)
          (<! (game/run canvas player kill))
          (recur kill)))) ))
