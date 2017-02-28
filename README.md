
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


Browsing sounds module
----

Install Freesound quark, the SuperCollider client for the database of sound samples Freesound.org.
Make sure you have an Internet connection.

### Examples

```
// instantiation
~mirlc = MIRLC.new;
~mirlc2= MIRLC.new;

// GET TARGET SOUNDS

// getsound (id = 31362, size = 1)
~mirlc.getsound(323399); // loop = 1
~mirlc.getsound(19246); // loop = 1
~mirlc.getsound(19247); // loop = 1
~mirlc.getsound(19248); // loop = 1
~mirlc2.getsound(192468);


// getsoundbyrandom ( size = 1 )
~mirlc.getsoundbyrandom();
~mirlc.getsoundbyrandom(2);
~mirlc.getsoundbyrandom(3);
~mirlc2.getsoundbyrandom();


// getsoundbytag ( tagquery = "noise", size = 1 )
~mirlc.getsoundbytag("nail", 3);
~mirlc.getsoundbytag("chimes", 2);
~mirlc.getsoundbytag("noise", 2);
~mirlc.getsoundbytag("hammer", 2);
~mirlc.getsoundbytag("grain", 2);
~mirlc.getsoundbytag("humming", 3);


// GET SIMILAR SOUNDS FROM TARGET SOUNDS

~mirlc.getsoundbysimilarity();
~mirlc.getsoundbysimilarity(1);
~mirlc.getsoundbysimilarity(numsnd:2, size:2);
~mirlc.getsoundbysimilarity(1);

~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]')
~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]', size:2)
~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[1]:[17 TO 20]')
~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[4]:[20 TO 40]')

// PLAYING WITH SOUNDS

~mirlc.solo(4);
~mirlc.soloall(4);
~mirlc.mute(2);
~mirlc.muteall(3);
~mirlc.stop();
~mirlc.play();
~mirlc.sequence();
~mirlc.free();
~mirlc2.stop();
~mirlc2.free();


// VISUALIZING SOUNDS

~mirlc.plotserver(); // it plots all the sounds that are playing, no matter the instace
~mirlc.printpool;
~mirlc.scope;
~mirlc2.printpool;


// propagation of sounds from existing sounds
~mirlc.propagate("random", 3); / deprecated?


//~mirlc.pool(2); // deprecated?
//~mirlc.getsoundbyquery(); // deprecated?
//~mirlc.getsoundbyquery("noise"); // deprecated?
//~mirlc.soundbytag("noise"); // deprecated?
//~mirlc.pick (1); // deprecated?
//~mirlc.pick (2, "random", "random") // deprecated?
//~mirlc.pick (1, meth1: "query"); // deprecated?
//~mirlc.pick (1, meth1: "query", tag:"noise"); // deprecated?
//~mirlc.pick (2, "random", "random") // deprecated?
//~mirlc.pick (2, meth1: "query", tag:"noise", meth2:"similarity") // deprecated?
//~mirlc.pick (4, meth1: "query", tag:"tuning", meth2:"similarity") // deprecated?
//~mirlc.pick (4, meth1: "query", tag:"machines", meth2:"similarity") // deprecated?
//~mirlc.pick (6, meth1: "query", tag:"ambient", meth2:"similarity") // deprecated?
//~mirlc2.pick (4, meth1: "query", tag:"machines", meth2:"similarity") // deprecated?
//~mirlc.createpool(1); // revise
//~mirlc.playsound();
//~mirlc.playsound(2);
```


License
----

The MIT License (MIT).




