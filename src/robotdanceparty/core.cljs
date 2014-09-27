(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!"))

; 'declare' globals
(def player #js {})
(def speed 2)
(def game #js{})
(def ticker (atom 0))

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

      ; setup player replay property
      (aset player "replay" {})

      (.setImage player (.next (aget player "anim_default")))
      (.preventDefaultKeys js/jaws
                           #js ["up" "down" "left" "right" "space"]))))
(println (aget player "replay"))

(defn replay-add [entity dir]
 (let [replay (aget entity "replay")]
   (if (replay (str @ticker))
     (aset player "replay"
           (assoc (aget entity "replay")
                  (str @ticker)
                  (conj (replay (str @ticker)) dir)))
     (aset player "replay"
           (assoc (aget player "replay")
                  (str @ticker)
                  #{dir})))))

; define game.update()
(aset game "update"
  (fn []

    (swap! ticker inc)

    (.setImage player (.next (aget player "anim_default")))

    (if (.pressed js/jaws "left")
        (do
          (aset player "x" (- (aget player "x") speed))
          (.setImage player (.next (aget player "anim_left")))
          (replay-add player "left")
          (if-not (aget player "flipped")
            (.flip player))))
    (if (.pressed js/jaws "right")
        (do
          (aset player "x" (+ (aget player "x") speed))
          (.setImage player (.next (aget player "anim_right")))
          (replay-add player "right")
          (if (aget player "flipped")
            (.flip player))))
    (if (.pressed js/jaws "up")
        (do
          (aset player "y" (- (aget player "y") speed))
          (.setImage player (.next (aget player "anim_up")))
          (replay-add player "up")))
    (if (.pressed js/jaws "down")
        (do
          (aset player "y" (+ (aget player "y") speed))
          (.setImage player (.next (aget player "anim_down")))
          (replay-add player "down")))

    (if (= 0 (mod @ticker 60))
      (let [fps (.getElementById js/document "fps")]
        (aset fps "innerHTML" (str (aget (aget js/jaws "game_loop") "fps")
                                 ". player: "
                                 (aget player "x")
                                 "/"
                                 (aget player "y")
                                 ". timer: "
                                 (/ @ticker 60)))))))

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

