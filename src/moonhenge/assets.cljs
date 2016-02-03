(ns moonhenge.assets)

(def sprites-assets
  {:star-1
   {:pos [8 8]
    :size [8 8]}

   :star-2
   {:pos [16 8]
    :size [8 8]}

   :star-3
   {:pos [24 8]
    :size [8 8]}

   :star-4
   {:pos [32 8]
    :size [8 8]}

   :star-5
   {:pos [40 8]
    :size [8 8]}

   :star-6
   {:pos [48 8]
    :size [8 8]}

   :press-any-key
   {:pos [32 40]
    :size [(- 110 32) 10]}

   :press-any-key-shadow
   {:pos [32 56]
    :size [(- 110 32) 10]}

   })

(def sprites-2-assets
  {:ship
   {:pos [160 0]
    :size [16 32]}

   :ship-thrust-0
   {:pos [112 0]
    :size [16 32]}

   :ship-thrust-1
   {:pos [128 0]
    :size [16 32]}

   :ship-thrust-2
   {:pos [144 0]
    :size [16 32]}

   :enemy-1
   {:pos [16 0]
    :size [16 16]}

   :enemy-2
   {:pos [32 0]
    :size [16 16]}

   :enemy-3
   {:pos [48 0]
    :size [16 16]}

   :enemy-4
   {:pos [64 0]
    :size [16 16]}

   :enemy-5
   {:pos [80 0]
    :size [16 16]}

   :enemy-6
   {:pos [96 0]
    :size [16 16]}

   :bullet-1
   {:pos [6 19]
    :size [3 12]}

   :bullet-2
   {:pos [22 19]
    :size [3 12]}

   :bullet-3
   {:pos [38 19]
    :size [3 12]}

   :rune-1
   {:pos [0 64]
    :size [32 32]}

   :rune-2
   {:pos [32 64]
    :size [32 32]}

   :rune-3
   {:pos [64 64]
    :size [32 32]}

   :explosion-0
   {:pos [0 96]
    :size [32 32]}

   :explosion-1
   {:pos [32 96]
    :size [32 32]}

   :explosion-2
   {:pos [64 96]
    :size [32 32]}

   :explosion-3
   {:pos [96 96]
    :size [32 32]}

   :explosion-4
   {:pos [128 96]
    :size [32 32]}

   :explosion-5
   {:pos [160 96]
    :size [32 32]}

   :moon
   {:pos [0 128]
    :size [128 128]}

   :astronaut-1
   {:pos [0 256]
    :size [16 16]}

   :astronaut-2
   {:pos [16 256]
    :size [16 16]}

   :henge
   {:pos [32 256]
    :size [64 64]}

   :henge-fire-0
   {:pos [96 256]
    :size [16 32]}

   :henge-fire-1
   {:pos [112 256]
    :size [16 32]}

   :henge-fire-2
   {:pos [128 256]
    :size [16 32]}

   :moon-surface
   {:pos [512 0]
    :size [512 128]}

   :title-words
   {:pos [0 32]
    :size [288 32]}})
