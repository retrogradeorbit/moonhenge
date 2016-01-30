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
   {:pos [0 0]
    :size [16 16]}

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

   :title-words
   {:pos [0 32]
    :size [288 32]}})
