(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!"))

; 'declare' globals
(def player #js {})
(def speed 2)
(def game #js{})
(def ticker (atom 0))
(def recording (atom false))
(def looping (atom false))
(def loop-length (atom nil))

(def robot nil)

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

(defn move [entity dir]
  (case dir
    "left" (do
             (aset entity "x" (- (aget entity "x") speed))
             (.setImage entity (.next (aget entity "anim_left")))
             (if-not (aget entity "flipped")
               (.flip entity)))
    "right"(do
             (aset entity "x" (+ (aget entity "x") speed))
             (.setImage entity (.next (aget entity "anim_right")))
             (if (aget entity "flipped")
               (.flip entity)))
    "up"   (do
             (aset entity "y" (- (aget entity "y") speed))
             (.setImage entity (.next (aget entity "anim_up"))))
    "down" (do
             (aset entity "y" (+ (aget entity "y") speed))
             (.setImage entity (.next (aget entity "anim_down"))))))

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

    (if (or @looping @recording) (swap! ticker inc))

    (.setImage player (.next (aget player "anim_default")))

    (if (.pressedWithoutRepeat js/jaws "space")
      (if @recording
        (do
          (println "recording: " (reset! recording false))

          (reset! looping true)
          (println "loop-length: " (reset! loop-length @ticker))
          (reset! ticker 0)
          )
        (do
          (println "recording: " (reset! recording true)))))

    (if (.pressed js/jaws "left")
        (do
          (move player "left")
          (replay-add player "left")
          ))
    (if (.pressed js/jaws "right")
        (do
          (move player "right")
          (replay-add player "right")
          ))
    (if (.pressed js/jaws "up")
        (do
          (move player "up")
          (replay-add player "up")))
    (if (.pressed js/jaws "down")
        (do
          (move player "down")
          (replay-add player "down")))

    (if (= 0 (mod @ticker 60))
      (let [fps (.getElementById js/document "fps")]
        (aset fps "innerHTML" (str (aget (aget js/jaws "game_loop") "fps")
                                 ". player: "
                                 (aget player "x")
                                 "/"
                                 (aget player "y")
                                 ". timer: "
                                 (/ @ticker 60)))))

    (if @looping
      (do
        (if (aget player "replay")
          (if-let [tick ((aget player "replay") (str @ticker))]
            (move player (first tick))))
        (if (> @ticker @loop-length) (reset! ticker 0))))


    ))


;(set! robot (.create js/Object player))

; define game.draw()
(aset game "draw"
  (fn []
    (.clear js/jaws)
    (.log js/jaws player)
    (if robot (.log js/jaws robot))
    (.draw player)
    (if robot (.draw robot))
    ))

; define jaws.onload()
(aset js/jaws "onload"
  (fn []
    (let [j-assets (aget js/jaws "assets")]
      (.add j-assets "assets/robot1_spritesheet.gif")
      (println "preloaded assets"))
    (.start js/jaws game)))

