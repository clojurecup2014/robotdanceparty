(ns robotdanceparty.core)

(enable-console-print!)

(defn ^:export main []
  (println "Hello, Clojure Cup!"))

; 'declare' globals
(def player nil)
(def speed 2)
(def game #js{})
(def ticker (atom 0))
(def recording (atom false))
(def looping (atom false))
(def loop-length (atom nil))

(def robots (atom []))

(def ^:export robot1 nil)
(def ^:export robot2 nil)
(def ^:export robot3 nil)

(def hand #js {})

(defn ^:export activate-hand [anim robot]
  ; setup hand
  (println "calling activate-hand()")
  (set! hand (.Sprite js/jaws
                      #js {"x" 50
                           "y" 50
                           "scale" 2
                           "anchor" "bottom_center"}))
  (aset hand "anim_default" (.slice anim 0 3))
  (aset hand "active" true)
  (aset hand "robot" robot)


  ;(.setImage player (.next (aget player "anim_default")))
  (.setImage hand (.next (aget hand "anim_default")))
  (.preventDefaultKeys js/jaws
                       #js ["up" "down" "left" "right" "space"]))

(defn spawn-player [x y robot]
  (let [anim (case robot
               "robot1" robot1
               "robot2" robot2
               "robot3" robot3)]
    ;re-def global player
    (set! player (.Sprite js/jaws
                          #js {"x" x
                               "y" y
                               "scale" 2
                               "anchor" "bottom_center"}))
    ; setup player stuff
    (aset player "active" true)
    (aset player "ticker" 0)
    (aset player "loop-length" 0)

    ; setup player animations
    (aset player "anim_default" (.slice anim 0 3))
    (aset player "anim_up" (.slice anim 3 6))
    (aset player "anim_down" (.slice anim 3 6))
    (aset player "anim_left" (.slice anim 3 6))
    (aset player "anim_right" (.slice anim 3 6))

    ; setup player replay property
    (aset player "replay" {})))

; define game.setup()
(aset game "setup"
  (fn []
    (println "in game setup")
    (set! robot1
          (.Animation js/jaws
                      #js {"sprite_sheet" "assets/robot1_spritesheet.gif"
                           "frame_duration" 100
                           "frame_size" #js [42 39]}))
    (set! robot2
          (.Animation js/jaws
                      #js {"sprite_sheet" "assets/robot2_spritesheet.gif"
                           "frame_duration" 100
                           "frame_size" #js [57 22]}))
    (set! robot3
          (.Animation js/jaws
                      #js {"sprite_sheet" "assets/robot3_spritesheet.gif"
                           "frame_duration" 100
                           "frame_size" #js [27 36]}))


    ))

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
 (let [replay (aget entity "replay")
       p-ticker (aget entity "ticker")]
   (if (replay (str p-ticker))
     (aset player "replay"
           (assoc (aget entity "replay")
                  (str p-ticker)
                  (conj (replay (str p-ticker)) dir)))
     (aset player "replay"
           (assoc (aget player "replay")
                  (str p-ticker)
                  #{dir})))))

(defn replay-action [robot dirs]
  (if (contains? dirs "left")
    (move robot "left"))
  (if (contains? dirs "right")
    (move robot "right"))
  (if (contains? dirs "up")
    (move robot "up"))
  (if (contains? dirs "down")
    (move robot "down")))

; define game.update()
(aset game "update"
  (fn []

    (if @recording (let [player-ticker (aget player "ticker")]
      (aset player "ticker" (inc player-ticker))))

    (if (not (nil? player))
      (.setImage player (.next (aget player "anim_default"))))

    (if (aget hand "active")
      (do
        (aset hand "x" (aget js/jaws "mouse_x"))
        (aset hand "y" (aget js/jaws "mouse_y"))))

    (if (and (aget hand "active")
             (.pressedWithoutRepeat js/jaws "left_mouse_button"))
      (do
        (println "pressed mouse")
        (spawn-player (aget js/jaws "mouse_x")
                      (aget js/jaws "mouse_y")
                      (aget hand "robot"))
        (aset hand "active" false)))

    (if (.pressedWithoutRepeat js/jaws "space")
      (if @recording
        (do
          (println "recording: " (reset! recording false))
          (aset player "active" false)
          (println "looping: " (reset! looping true))
          (println "loop-length: "
                   (aset player "loop-length"
                         (aget player "ticker")))
          (aset player "ticker" 0)
          (swap! robots conj player)
          (aset (last @robots) "x" (aget (last @robots) "init_x"))
          (aset (last @robots) "y" (aget (last @robots) "init_y"))

          )
        (do
          (println "recording: " (reset! recording true))
          (aset player "ticker" 0)
          (aset player "init_x" (aget player "x"))
          (aset player "init_y" (aget player "y"))
          )))


    (if (and (not (nil? player )) (aget player "active") (.pressed js/jaws "left"))
        (do
          (move player "left")
          (replay-add player "left")))
    (if (and (not (nil? player )) (aget player "active") (.pressed js/jaws "right"))
        (do
          (move player "right")
          (replay-add player "right")))
    (if (and (not (nil? player )) (aget player "active") (.pressed js/jaws "up"))
        (do
          (move player "up")
          (replay-add player "up")))
    (if (and (not (nil? player )) (aget player "active") (.pressed js/jaws "down"))
        (do
          (move player "down")
          (replay-add player "down")))

    (if (not (nil? player ))
      (let [fps (.getElementById js/document "fps")]
        (aset fps "innerHTML" (str (aget (aget js/jaws "game_loop") "fps")
                                 ". player: "
                                 (aget player "x")
                                 "/"
                                 (aget player "y")))))

    ;(if (or @recording @looping) (swap! ticker inc))

    (if @looping
      (loop [robot-vec @robots]
        (if (empty? robot-vec)
          nil;(println "all done")
          (do
            (.setImage (first robot-vec) (.next (aget (first robot-vec) "anim_default")))
            (aset (first robot-vec) "ticker" (inc (aget (first robot-vec) "ticker")))
            (if-let [dirs ((aget (first robot-vec) "replay")
                           (str (aget (first robot-vec) "ticker")))]
              (replay-action (first robot-vec) dirs))
            (if (> (aget (first robot-vec) "ticker") (aget (first robot-vec) "loop-length"))
              (do
                (println "ticker is 0")
                (aset (first robot-vec) "x" (aget (first robot-vec) "init_x"))
                (aset (first robot-vec) "y" (aget (first robot-vec) "init_y"))
                (aset (first robot-vec) "ticker" 0)))
            (recur (rest robot-vec))))))))

; define game.draw()
(aset game "draw"
  (fn []
    (.clear js/jaws)

    (loop [robot-vec @robots]
      (if (empty? robot-vec)
        nil ;(println "no more to draw")
        (do
          (.draw (first robot-vec))
          (recur (rest robot-vec)))))

    (if (and (not (nil? player )) (aget player "active"))
      (.draw player))

    (if (aget hand "active")
      (.draw hand))


;    (if (not (empty? @robots))
;      (.draw (@robots 0)))
    ))

; define jaws.onload()
(aset js/jaws "onload"
  (fn []
    (let [j-assets (aget js/jaws "assets")]
      (.add j-assets "assets/robot1_spritesheet.gif")
      (.add j-assets "assets/robot2_spritesheet.gif")
      (.add j-assets "assets/robot3_spritesheet.gif")
      (println "preloaded assets"))
    (.start js/jaws game)))

