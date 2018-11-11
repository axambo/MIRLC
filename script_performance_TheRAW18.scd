// This is the script used for the live performance presented in the concert The RAW, Inter/sections 2018 / Café 1001 - London, UK (28.11.2018) by peterMann (aka Anna Xambó).
// The code used in the concert corresponds to the following commit hash to GitHub:
// [https://github.com/axambo/MIRLC/commit/d32c89de9256cf73409038c0f788b380719cde25](https://github.com/axambo/MIRLC/commit/d32c89de9256cf73409038c0f788b380719cde25)

(
o = Server.default.options;
o.device ="Scarlett 6i6 USB";
o.numInputBusChannels = 4;
s.reboot;
)
s.meter;

a = MIRLCRep.new
a.content(1, 'dissonance', 1, 'conf', 'lo')
a.fadeout

b = MIRLCRep.new
b.tag("dissonance")
b.fadeout

c= MIRLCRep.new
c.content(2, 'dur', 2)
c.volume(0.05)
c.sequence
c.parallel
c.fadeout

d = MIRLCRep.new
d.tag("bass+hiphop")
d.similar
d.sequence
d.fadeout

e = MIRLCRep.new
e.tag("kraftwerk")
e.similar
e.sequence
e.parallel
e.fadeout

f = MIRLCRep.new
f.tag("bass+kraut")
f.volume(0.05)
f.fadeout

g = MIRLCRep.new
g.content(1, 'pitch', 220, 'conf', 'lo')
g.volume(0.08)
g.fadeout

h = MIRLCRep.new
h.tag("acid")
h.volume(0.02)
h.fadeout

i = MIRLCRep.new
i.tag("bass+acid")
i.whatpitch
i.fadeout

j = MIRLCRep.new
j.content(1, 'pitch', 761, 'conf', 'lo')
j.mute(0)


// this is a risk!!!

// even more...

k = MIRLCRep.new
k.random
k.similar
k.fadeout

// perhaps another random??
l = MIRLCRep.new
l.random
l.fadeout

m = MIRLCRep.new
m.tag("bass+fruity")
m.fadeout

n = MIRLCRep.new
n.tag("turntable")
n.fadeout
o = MIRLCRep.new
o.tag("scratching")
o.fadeout
p = MIRLCRep.new
p.tag("sirens")
p.tag("firealarm")

