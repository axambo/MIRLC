// This is the script used for the live performance presented in the concert Noiselets (8.1.2017) by peterMann (aka Anna Xambó) at Freedonia, Barcelona, Spain.

// part 1, interferences
~nz = MIRLC.new
~nz.getsound(50699)
~nz.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]')
~nz.getsoundbytag("cell-phone", 2)
~nz.getsoundbysimilarity(2)
~nz.getsoundbycontent(4)
~nz.getsoundbysimilarity(5)
~nz.printpool
~nz.sequence
~nz.parallel
~nz.stop

// part 2, digits, digit, hammer, random
~nz2 = MIRLC.new
~nz2.getsoundbytag("digits")
~nz2.getsoundbytag("digit", 2)
~nz2.getsoundbytag("hammer")
~nz2.getsoundbyrandom(2)
~nz2.printpool
~nz2.mute(5)
~nz2.mute(6)
~nz2.getsoundbyrandom(2)
~nz2.mute(7)
~nz2.sequence
~nz2.stop

// part 3, saw, squarewave, scratches
~nz3 = MIRLC.new
~nz3.getsoundbytag("saw")
~nz3.getsoundbytag("scratches", 3)
~nz3.getsoundbysimilarity(1)
~nz3.printpool
~nz3.sequence
~nz3.parallel
~nz3.stop

// part 4, digits, digit, digital
~nz10 = MIRLC.new
~nz10.getsoundbytag("digits", 6)
~nz10.getsoundbysimilarity(1)
~nz10.getsoundbyrandom(4)
~nz10.getsoundbytag("digit", 1)
~nz10.getsoundbytag("noise", 2)
~nz10.printpool
~nz10.getsoundbysimilarity(19)
~nz10.sequence


// GET TARGET SOUNDS
//~mirlc.getsoundbyrandom();
//~mirlc.getsoundbytag("nail", 3);

// GET SIMILAR SOUNDS FROM TARGET SOUNDS
//~mirlc.getsoundbysimilarity();
//~mirlc.getsoundbysimilarity(1);
//~mirlc.getsoundbysimilarity(numsnd:2, size:2);

//~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]')
//~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[0]:[-1124 TO -1121]', size:2)
//~mirlc.getsoundbycontent(queryfilter: 'lowlevel.mfcc.mean[1]:[17 TO 20]')
//~nz.getsoundbycontent(queryfilter: '.lowlevel.mfcc.mean[0]:[-1124 TO -1121]')
//~nz.getsoundbycontent(queryfilter: '.ltonal.key_scale:"major')
//~nz.getsoundbycontent(queryfilter: '.lowlevel.spectral_centroid:0.5')
//~nz.getsoundbycontent(queryfilter: '.lowlevel.barkbands.mean')
//~nz.getsoundbycontent(queryfilter: 'lowlevel.pitch_salience.mean:[0.6 TO *]')


// PLAYING WITH SOUNDS
//~mirlc.solo(4);
//~mirlc.mute(2);
//~mirlc.soloall(4);
//~mirlc.muteall(2);
//~mirlc.stop;
//~mirlc.play;
//~mirlc.sequence;
//~mirlc.parallel;
//~mirlc.free();

// VISUALIZING SOUNDS
//~mirlc.plotserver();
//~mirlc.printpool;
//~mirlc.scope;

// RECORDING
// Server.default.makeGui
