(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!"))

; 'declare' the game state object and player
(def player #js {})
(def game #js{})

; define game.setup()
(aset game "setup"
  (fn []
    (println "in game setup")

    (let [anim (.Animation js/jaws
                           #js {"sprite_sheet" "assets/droid.png"
                                "frame_duration" 100
                                "frame_size" #js [11 15]})]
      ;re-def global player
      (set! player (.Sprite js/jaws
                            #js {"x" 100
                                 "y" 100
                                 "scale" 3
                                 "anchor" "bottom_center"}))

      (aset player "anim_default" (.slice anim 0 5))
      (.setImage player (.next (aget player "anim_default"))))))

; define game.draw()
(aset game "draw"
  (fn []
    (.clear js/jaws)
    (.log js/jaws player)
    (.draw player)))

; define jaws.onload()
(aset js/jaws "onload"
  (fn []
    (let [j-assets (aget js/jaws "assets")]
      (.add j-assets "assets/droid.png")
      (println "preloaded assets"))
    (.start js/jaws game)))

