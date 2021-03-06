var background_music = []

var song1_fast = new Howl({
  urls: ['assets/techno_180.mp3', 'assets/techno_180.ogg'],
  loop: true,
  volume: .4,
  playing: false
})

background_music.push(song1_fast)

var song2_fast = new Howl({
  urls: ['assets/dnb_180.mp3', 'assets/dnb_180.mp3'],
  loop: true,
  playing: false
})

background_music.push(song2_fast)

var song1_medium = new Howl({
  urls: ['assets/techno_140.mp3', 'assets/techno_140.ogg'],
  loop: true,
  volume: .4,
  playing: false
})

background_music.push(song1_medium)

var song2_medium = new Howl({
  urls: ['assets/dnb_160.mp3', 'assets/dnb_160.ogg'],
  loop: true,
  playing: false
})

background_music.push(song2_medium)

var song1_slow = new Howl({
  urls: ['assets/techno_100.mp3', 'assets/techno_100.ogg'],
  loop: true,
  volume: .4,
  playing: false
})

background_music.push(song1_slow)

var song2_slow = new Howl({
  urls: ['assets/dnb_80.mp3', 'assets/dnb_80.ogg'],
  loop: true,
  playing: false
})

background_music.push(song2_slow)


function playBackgroundMusic(song,tempo) {

  var track = (song + 1) + 2*tempo - 1

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

