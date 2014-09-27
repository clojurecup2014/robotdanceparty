var background_music = []

var song1_fast = new Howl({
  urls: ['assets/techno_180.wav'],
  loop: true,
  volume: .4,
  playing: false
})

background_music.push(song1_fast)

var song2_fast = new Howl({
  urls: ['assets/dnb_180.wav'],
  loop: true,
  playing: false
})

background_music.push(song2_fast)

function playBackgroundMusic(track) {

  var this_bgm = background_music[track]

  if(!this_bgm.playing) {
    stopBackgroundMusic()
    background_music[track].play()
    this_bgm.playing = true
  }
}

function stopBackgroundMusic() {

  for (var i = 0; i < background_music.length; i++) {
    var this_bgm = background_music[i]
    if (this_bgm.playing)
      this_bgm.stop()
    this_bgm.playing = false
  }

}

