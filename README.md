
MIRLC
===
A SuperCollider extension for using MIR techniques in live coding. 

(c) 2016-2017 by Anna Xambó (<anna.xambo@gatech.edu>).


Introduction
----

This SC class is designed to provide a high-level approach to using MIR techniques in live coding. 


Application Start
----

Drag the `MIRLC.sc` file to the Extensions folder of SuperCollider (suggested to create a subfolder with the same name): `/Users/{username}/Library/Application Support/SuperCollider/Extensions` (in Mac)

Either recompile the class library (`Language>Recompile Class Library`) or restart SuperCollider.

Version History
----

The code used in the concert of Noiselets 2017 corresponds to the following commit hash to GitHub: [https://github.com/axambo/MIRLC/commit/3d0d27a0d1098b34694d7ef29439dac7443d5167](https://github.com/axambo/MIRLC/commit/3d0d27a0d1098b34694d7ef29439dac7443d5167)


Audio Retrieval Module
----

### Requirements

* Make sure you have an Internet connection.
* Make sure you have a [Freesound.org](http://freesound.org) account.
* Make sure to obtain an [API key](http://www.freesound.org/api/apply/).
* Install [Freesound quark](https://github.com/g-roma/Freesound.sc), which is a SuperCollider client for accessing the Freesound API and operate with sounds from Freesound.org.
* In order to connect with Freesound.org, the type of authentication used in MIRLC is Token. Make sure to introduce your API key in the class `MIRLC.sc` and recompile the class library. 

### Additions to the Freesound quark

* If retrieval by random, automatic retrieval of existing sounds (skipping those that do not exist from the collection).
* Hierarchy of sounds and groups of sounds, which can be played either sequentially or simultaneously.
* Call of functions that are easy-to-use and easy-to-memorize.


### Code Examples

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

### Live Performance Examples

* Here you can listen to the [live performance rehearsal for the concert Noiselets 2017](https://soundcloud.com/petermann-plays/noiselets-2017-liveset-rehearsal), Freedonia, Barcelona, Spain. Here is the [sound credit list](sound_credits_rehearsal_Noiselets17.md).


License
----

The MIT License (MIT).




