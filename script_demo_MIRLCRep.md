```
// instantiation
~a = MIRLCRep.new
~b = MIRLCRep.new


// GET SOUNDS BY TEXT

// getsound(id=31362, size=1)
~a.id(323399)
~a.id(19246)
~a.id(19247)
~b.id(19248)
~b.id(192468)

// random(size=1)
~a.random
~a.random(2)
~a.random(3)
~b.random

// tag(tag="noise", size=1)
~a.tag("nail", 3)
~a.tag("chimes", 2)
~a.tag("noise", 2)
~a.tag("hammer", 2)
~b.tag("grain", 2)
~b.tag("humming", 3)


// GET SOUNDS BY CONTENT & GET SOUNDS BY CONTENT WITH FILTER

// content(size=1, feature = 'dur', fvalue = 1, fx = 'conf', fxvalue = 'hi')
~a.content(10, 'dur') // sounds of 10 sec of duration with high confidence
~a.content // sounds of 1 sec of duration with high confidence
~a.content(1, 'dur', 1, 'key', 'A')
~a.content(1, 'dur', 4, 'scale', 'minor')
~a.content(1, 'dur', 1, 'conf', 'lo')
~a.content(2, 'pitch', 100, 'conf', 'lo')
~a.content(1, 'key', 'Asharp')
~b.content(1, 'bpm', 120)


// GET SIMILAR SOUNDS BY EXAMPLE

// similar(targetnumsnd=0, size=1)

~a.similar
~a.similar(0)
~a.similar(0, 2)
~b.similar(1)


// GET SIMILAR SOUNDS BY FILTER

// filter (targetnumsnd=0, size=1, fx = 'conf', fxvalue = 'hi')

~a.content(1, 'dur', 4, 'scale', 'minor')
~a.filter(1, 1,'conf','lo')
~a.filter(1, 1,'conf','hi')
~a.filter(2, 1,'conf','hi')

~b.content(1, 'dur', 2)
~b.filter(1, 1,'mfcc0','neg')
~b.filter(1, 1,'mfcc1','1720')
~b.filter(1, 1,'mfcc1','2040')


// ANALYZE SOUNDS BY CONTENT

~a.random(1)
~a.whatpitch
~a.content(1, 'pitch', 660)
~a.similar

~a.random(1)
~a.whatbpm
~a.content(1, 'bpm', 116)
~a.similar
~a.whatpitch
~a.content(1,'pitch', 220)


~a.analyze; // full list as it comes from the Freesound quark


// PLAYING WITH SOUNDS

~a.sequence
~a.parallel

~a.solo(4)

~a.mute(2)

~a.muteall

~a.stop

~a.play



// VISUALIZING SOUNDS

~a.plotserver // it plots all the sounds that are playing, no matter the instace
~a.printpool
~a.scope
~b.printpool
~a.printall

```
