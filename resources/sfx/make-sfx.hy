#!/usr/bin/env hy
; pip install --user hy

(def sfx {:shoot ["11111JoJGpRLuPRzPWmoeRAiCanxCstej7ZcukyMBm3f3gtT21jyoyYqFo3toRNJF2ZiNLVseLa1dR26vRFTHR1Dk6ip5uMFLatSngvqMoFG23LjgWrqhGZ5"
                  "34T6PkwjtyBv5FUzHAwF233hNbntF47CbWA5m6a3eq1eSCA3f2iLZYkAD2b7kqXSxuacoPH9Q447xHkag1FmzY5ksmRMxdwXiw9WgoSZEZnLPm874K7bYqrDu"
                  "11111FT2WbT8zventhWGvyWxe79JAsHoNBVLtMX8XcZBmSng1HVDVcEJGhaswSfFKewCQzwsZaC8dphRrkmUfT7FXnz5YeTL7CuQ2pb9VFVFMwHjuaQkKnM5"
                  "11111wz6dt5BX6DELxV3uLnWEgRTf4rfBQi98YaEyQmMwJgaUuntgrgvx4JC2MNf3v9Bg8wHL6tq9AHbReXa9e83PTKafzRmzQv1uawr3b8dKy31NkeG8hm"]
          :explode ["7BMHBGLomHw2g2T5dvPHWge21gYyrgqCUGV9EFeaHfGRLjt1gVVUUYJgsKdDHnpzt6hazVA44ySPEZR2cpzVrqLzEtHsYRak8pRc5sEz1Ehs5cBMTzgW2Mj3M"
                    "7BMHBGGJG9Jbk4p8kKy77p92isdbmiTZNszvW4S1NVDSSUqxTff9QGqfRvtVSQKKbABLx1vjZA4aewtbM7H8dtrvaWPzWkPXn5x7ngc5KQC2FUbGpMRXuUwrP"
                    "7BMHBGK1km2K43XTdFSkLamxY94AjHbJmYzf3fwUEd6PinXMweuybRsa6mq2S7uFWscNudKMNbviLFdKWbtPwvqtv5nEpKDnrZe1GyvuYgdrYbxr2TU2H9QhM"
                    "7BMHBGJps16ov3DzGy2jCFgaCZ1PDRd6q9xXao3TkpRDrLGBwY3ZZrfWT9XWcxRHQkt5spg5Px5cEzA8MPwUvFo1Zujwgc5okbAdWeE6gKvHjf8gxDjBWpYpP"
                    "7BMHBGKSmFVrZEN3bkL9r96HtuwVwAG3d4QNu4eDN98xcXkLH4D3VL1hYv9jcYjwaeDNEgMKKdLAkwKgNkHFCKksPAqUSiLWbRgEshfEgUDDwa6YX7hiTVNwq"
                    "7BMHBGGoBBuS1p4QUrvx7a6vZn4K4Poymr4N9s6ZfZTWjLSn3p4gJ8d5RdC8yySQjC588XQEfa1pS8qardUNihx8gRLHXBHjRmvZFnFPNsfonmNjcyFpVyEbR"
                    "7BMHBGRogtY8UJP2DAn3JTThxxNxydCFUV5PVByuhMag5sfSqLCRHFYDnizJUPriwnwcynALKkRuGRvoYJzrnmTnH3QwXNBe4k21rxp1ZRU7SnbTdJt1NNpRV"
                    "7BMHBGNT7wQ93cMPzXRBxkvMiSioc2ESzCdL4THGY3nFSEdSqeoUDzbjeErPpN2NgA7L326wRURL6SncpbfPcKmacQExvhc4pTH2BR9PVoJau9fuFDaL82N6s"
                    "7BMHBGQfXs8f5aAdxv9zCu9wXapR6FZpdc3GmdnHqguqaAsjtRoprH8HbAX5aQ9JvEg5F9wTSKfj4Hs1HcZoYULLXuMSBPZxD8efwAykzZQxvHVuVn6LXxLhm"
                    "7BMHBGJmF7kDa8u7UBiDDy9nsbCqggq1jzt8A8uLc8RSAJ3bwGLCovjtDR4GAPhnj1aS9pwhrmAj4YDKpPhwkW2cBEMpgq9o7YEgMECqA8PJKYW9KciN7ZBQb"]
          :blip ["34T6PknRACj8PmFniE8VHTGqAECcthMVuKuRf7JYr2hfpSTjP3dWGcgWNuR6d59pLRx8XHKCuWpTSuQVG5Q8zMmL3oXBLDZbBv9Yu5jEKzQkh6RDF8f82wVAf"
                 "11111BFZZ5cz8hjuexeJAJF6XNpLKQgjAfqTz7AWB6pPdc1aR7bcGagvm8wfpHH267j9u64cdRW6NQuz8pS1fiETpvnjwivzkoMevnWQsdiqnZuvbeD3BJvj"
                 "11111CGBfCCCHXaKoQNm9AHBLCQGKWZ3TJ7UWyJxbaY7Nup4nCMCNamYPkpBXrQbM719YACPgLhQdxjm6NEEVfKuNbp1zxAUSLJvwyKmvSnhbcTcuvUPjTdZ"]
          :wave-respawn ["34T6Pkknfqhk7mnzFUaQCUi61eqsnpJkz49VEGUHaZz9KPP2wmf5sRt32NMo4EnNLwwaPUGqXciboFhebNcvJxijJeK9iPPNRYjZfEyQva1HqJEyDLUXgpLoZ"]
          :rune ["4EtjmkzoSTzY5EBQamWjeps8f8NGoonvLNesawbu7q9JRZChkhrXZ7Ms6QMegJ8mqatjfBmHHg5ApbEreA3yARc8QdDtborzZMv6LyHc4NAwmWsAjeuzeyvTt"
                 "7AUZNDXTL3mdbAyCuTXbRgTXjEpFFQPrcEZQNz3e5sSgMxtYH1r8sTxw6i2WEnakWEx6xkwq6eU9ACQMqMNfQMLtYjjJeZQR7sQyQnvRVsCC8oX59hZBb4wWq"
                 "6mtNKHWRamWs26xBdGThoZLGFxNJzQGL49Lmv1AHCh7x8CmD81gB7KPNaMqNSAftyRBFdUCPSXKa7NkWZPhFJ2izEZ1qzKw57ieNJC31aoo612kANtBPtmkHy"
                 "131dw63CQYRFDFUNwWp9y6X7KPuLgPKERfrn37kc7LgVNNkVTKXZRdQfcaAW7BhYLf7R1GsAivmWNFipbxpEtRRw2vU8Pbo2QxLkgtHUfh5iYTFF3BYjeVYjck"
                 "1HnnkB1ALXZXzQw76q1JGXj8rFkKWhDudqHggFh9cHJuk9M784QY6DmDa8QBEPLQXKqmtHgwxzYWPTgqaTKtyP4s5Lc7T9qhKjn9UayZCrwoi5L3DiNFBTh58"]
          ; call these multiple times overlapping
          :moon-rumble ["7BMHBG9qJwv9NFqd6qCocrrzbf83pCCjjXfV3oyurFG8htRNZftgwScfufYKu6MfXrr2JpRmPZbcPc4snP4vMx3u1GyLfLHfUHWZ7QaS2CP8tnQvTcx9fJXLs"]
          :portal ["1uhTjaKtq2kfFXS6QPjs4t27MQy9q4f3xooRHS5TBg1qMCeoYwTM5Cw8iErBD1S4Bz4w6LCfRFG9jXdBVXbEpYV8hQKdXCY14FQzgKdQJedrSm9sqJhXynSQw"]})

(import [subprocess [call]])
(import os)

(if (os.path.isdir "jsfxr")
  (for [sound-name sfx]
    (print (name sound-name))
    (for [sfxr-def-num (range (len (get sfx sound-name)))]
      (let [[wav-name (% "%s-%d.wav" (, (name sound-name) sfxr-def-num))]
            [ogg-name (.replace wav-name ".wav" ".ogg")]]
        (call ["./jsfxr/sfxr-to-wav" (get  (get sfx sound-name) sfxr-def-num) wav-name])
        (call ["oggenc" wav-name "-o" (+ "../public/sfx/" ogg-name)]))))
  (print "You need jsfxr:\ngit clone https://github.com/chr15m/jsfxr.git"))

