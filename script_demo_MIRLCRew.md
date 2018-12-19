```
// instantiation
a = MIRLCRew.new // audio in mode
a = MIRLCRew.new (0) // audio in mode
b = MIRLCRew.new(1) // audio sample by default: "sounds/a11wlk01.wav"
c = MIRLCRew.new(1, "/Users/annaxambo/Desktop/MIRLC/22032__acclivity__numbers-englishfemale.wav") // path to an audio sample

// sound source
a.source // pure audio source

// sound from audio features

a.onsets
a.onsets('perc')
a.onsets('perc', 90)
a.onsets('spark')
a.onsets('beep')
a.onsets('beep', 1)
a.onsets('spark', 'freqend', 20000)
a.onsets('beep', 1)
b.onsets('beep', 4, 'ampin', 0.9 )

a.beats

a.pitch

a.key

a.amps
a.amps('perc')
a.amps('spark')
a.amps('beep')

// overlay sound source
a.overlay
a.overlay('on')
a.overlay(off)

// visualization
a.scope

// finish
a.free
b.free

```
