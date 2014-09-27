(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!"))

; 'declare' globals
(def player #js {})
(def speed 2)
(def game #js{})

; define game.setup()
(aset game "setup"
  (fn []
    (println "in game setup")

    (let [anim (.Animation js/jaws
                           #js {"sprite_sheet" "assets/robot1_spritesheet.gif"
                                "frame_duration" 100
                                "frame_size" #js [42 39]})]
      ;re-def global player
      (set! player (.Sprite js/jaws
                            #js {"x" 100
                                 "y" 100
                                 "scale" 2
                                 "anchor" "bottom_center"}))

      ; setup player animations
      (aset player "anim_default" (.slice anim 0 3))
      (aset player "anim_up" (.slice anim 3 6))
      (aset player "anim_down" (.slice anim 3 6))
      (aset player "anim_left" (.slice anim 3 6))
      (aset player "anim_right" (.slice anim 3 6))
      (.setImage player (.next (aget player "anim_default")))
      (.preventDefaultKeys js/jaws
                           #js ["up" "down" "left" "right" "space"]))))

; define game.update()
(aset game "update"
  (fn []

    (.setImage player (.next (aget player "anim_default")))

    (if (.pressed js/jaws "left")
        (do
          (aset player "x" (- (aget player "x") speed))
          (.setImage player (.next (aget player "anim_left")))
          (if-not (aget player "flipped")
            (.flip player))))
    (if (.pressed js/jaws "right")
        (do
          (aset player "x" (+ (aget player "x") speed))
          (.setImage player (.next (aget player "anim_right")))
          (if (aget player "flipped")
            (.flip player))))
    (if (.pressed js/jaws "up")
        (do
          (aset player "y" (- (aget player "y") speed))
          (.setImage player (.next (aget player "anim_up")))))
    (if (.pressed js/jaws "down")
        (do
          (aset player "y" (+ (aget player "y") speed))
          (.setImage player (.next (aget player "anim_down")))))

    (let [fps (.getElementById js/document "fps")]
      (aset fps "innerHTML" (str (aget (aget js/jaws "game_loop") "fps")
                                 ". player: "
                                 (aget player "x")
                                 "/"
                                 (aget player "y"))))))

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
      (.add j-assets "assets/robot1_spritesheet.gif")
      (println "preloaded assets"))
    (.start js/jaws game)))

