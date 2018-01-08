
Code Examples
----

* [Script used for the live performance](script_Noiselets.scd)
* [Version of the code used (old version of the MIRLCRep module)](https://github.com/axambo/MIRLC/commit/3d0d27a0d1098b34694d7ef29439dac7443d5167)

```
// instantiation
~mirlc = MIRLC.new;
~mirlc2= MIRLC.new;

// GET SOUNDS (BY EXAMPLE)

// getsound (id = 31362, size = 1) // loop = 1
~mirlc.getsound(323399);
~mirlc.getsound(19246);
~mirlc.getsound(19247);
~mirlc.getsound(19248);
~mirlc2.getsound(192468);


// randomseed ( size = 1 )
~mirlc.randomseed();
~mirlc.randomseed(2);
~mirlc.randomseed(3);
~mirlc2.randomseed();


// tagseed ( tagquery = "noise", size = 1 )
~mirlc.tagseed("nail", 3);
~mirlc.tagseed("chimes", 2);
~mirlc.tagseed("noise", 2);
~mirlc.tagseed("hammer", 2);
~mirlc.tagseed("grain", 2);
~mirlc.tagseed("humming", 3);


// GET SOUNDS (BY CONTENT)

//todo...

// contentseed ( size = 1, feature = '.lowlevel.pitch.mean:600', fx = '.lowlevel.pitch_instantaneous_confidence.mean:[0.8 TO 1]' )

~mirlc.contentseed(1, 'rhythm.bpm:120')

~mirlc.contentseed(1, 'rhythm.bpm:240', 'lowlevel.pitch.mean:369')

~mirlc.contentseed(1, 'lowlevel.pitch.mean:220')


// ANALYZE SOUNDS (BY CONTENT)

~mirlc.analyze;



// GET SIMILAR SOUNDS FROM TARGET SOUNDS

// getsimilar ( size = 1, targetnumsnd = 1 )

~mirlc.getsimilar();
~mirlc.getsimilar(1);
~mirlc.getsimilar(2, 2);
~mirlc.getsimilar(1);

// GET SIMILAR SOUNDS BY RANGE

//todo...

// getbyfilter ( size = 1, targetnumsnd = 1, fx = '.lowlevel.pitch_instantaneous_confidence.mean:[0.8 TO 1]' )

~mirlc.getbyfilter(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]')
~mirlc.getbyfilter(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]', size:2)
~mirlc.getbyfilter(queryfilter: 'lowlevel.mfcc.mean[1]:[17 TO 20]')
~mirlc.getbyfilter(queryfilter: 'lowlevel.mfcc.mean[4]:[20 TO 40]')

// PLAYING WITH SOUNDS

~mirlc.sequence;
~mirlc.parallel;

~mirlc.solo(4);
~mirlc.soloall(4);

~mirlc.mute(2);

~mirlc.muteall(3);

~mirlc.stop();

~mirlc.play();

~mirlc2.stop();


// VISUALIZING SOUNDS

~mirlc.plotserver(); // it plots all the sounds that are playing, no matter the instace
~mirlc.printpool;
~mirlc.scope;
~mirlc2.printpool;
~mirlc.printall;

```
