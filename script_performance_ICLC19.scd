// This is the script used for the live performance presented in the concert The RAW, Inter/sections 2018 / Café 1001 - London, UK (28.11.2018) by peterMann (aka Anna Xambó).
// The code used in the concert corresponds to the following commit hash to GitHub:
// [https://github.com/axambo/MIRLC/commit/7a23c54dd06e0b348790e9d9aac87edb1e3f678e](https://github.com/axambo/MIRLC/commit/7a23c54dd06e0b348790e9d9aac87edb1e3f678e)

(
o = Server.default.options;
o.device ="Scarlett 6i6 USB";
s.reboot;
)

a = MIRLCRep.new
a.tag("crowd"+"smelling")
a.similar
//it can take some time to download the sound
a.whatpitch
a.content(1, 'pitch', 227, 'conf', 'lo')
a.fadeout

b = MIRLCRep.new
b.tag("crowd"+"mantra"+"audiencia")
b.fadeout

c = MIRLCRep.new
c.tag("crowd"+"scratching")
c.similar
c.similar(1)
c.whatbpm
//so slow!!
c.content(1, 'bpm', 105, 'conf', 'lo')
c.printall
c.fadeout

d = MIRLCRep.new
d.tag("crowd"+"rubbing")
d.printall
d.similar
d.similar(1)
d.fadeout

e = MIRLCRep.new
e.tag("crowd"+"keys")
e.similar
e.whatpitch
e.fadeout

f = MIRLCRep.new
f.tag("crowd"+"eggs") // not easter eggs, I think...
f.similar
f.whatpitch
f.content(1, 'pitch', 989, 'conf', 'lo')
f.fadeout

g = MIRLCRep.new
g.tag("crowd"+"cellular")
g.similar
g.printall
g.similar(1)
g.fadeout

h = MIRLCRep.new
h.tag("crowd"+"wind")
h.similar
h.printall
h.mute(0)
h.play
h.fadeout

i = MIRLCRep.new
i.tag("crowd"+"jump")
i.similar
i.fadeout

j = MIRLCRep.new
j.tag("crowd"+"mini"+"clap")
j.similar
j.fadeout

k = MIRLCRep.new
k.tag("crowd"+"patata")
k.fadeout

l = MIRLCRep.new
l.tag("crowd"+"kissing")
l.similar
l.whatbpm
l.content(1, 'bpm', 126, 'conf', 'lo')
l.fadeout

m = MIRLCRep.new
m.tag("crowd"+"shhhh")
m.similar
m.fadeout

n = MIRLCRep.new
n.tag("crowd"+"fireworks")
n.similar
n.fadeout

o = MIRLCRep.new
o.tag("crowd"+"coding")
o.similar
o.whatpitch
o.content(1, 'pitch', 331, 'conf', 'lo')
o.fadeout

p = MIRLCRep.new
p.tag("crowd"+"sourcing")
p.tag("crowd"+"sourcing")
p.fadeout

q = MIRLCRep.new
q.tag("crowd"+"marathon")
q.printall
q.similar

r = MIRLCRep.new
r.tag("crowd"+"clapping"+"shoe")
r.tag("crowd"+"clapping"+"body")
// and even...
r.tag("crowd"+"clapping"+"tooth")
r.similar(0,2)
r.similar(1,2)
r.similar(2,2)

s = MIRLCRep.new
s.tag("crowd"+"clapping")
s.similar
//thanks
