```
// instantiation
a = MIRLCRew.new(0)
b = MIRLCRew.new(1)
c = MIRLCRew.new(1, "/Users/annaxambo/Desktop/MIRLC/22032__acclivity__numbers-englishfemale.wav")
d = MIRLCRew.new


a.key
a.source
a.scope
a.onsets('perc', 90)
a.onsets('spark', 'freqend', 20000)
a.onsets('beep', 1)
b.onsets('beep', 4, 'ampin', 0.9 )
b.beats

(
a.source;
a.amps;
)

(
a.source;
a.amps('spark');
)

(
a.source;
a.amps('beep');
)

(
a.pitch;
a.onsets('beep', 1)
)

a.free
b.free
c.free
d.free

```
