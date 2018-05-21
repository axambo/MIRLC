MIRLCRew {

    classvar server;
	classvar <>mode, <>soundfile; // 0 => audio in; 1 => audio recording
	var bufferin, bufferbeattrack, bufferonsets, bufferkeytrack;
	var numspeakers;
	var audiosource;
	var osctr;
	var audioout;

	*new {|mod = 0, filename|
		^super.new.init(mod, filename);
    }

    //------------------//
    // INIT
    //------------------//
    init {|mod, filename|
        server = Server.local;
        server.boot;

		server.waitForBoot({

			// Prepare the buffers
			bufferbeattrack = Buffer.alloc(server, 1024, 1); //  for sampling rates 44100 and 48000
			bufferonsets = Buffer.alloc(server, 512);
			bufferkeytrack =  Buffer.alloc(server, 4096, 1); // for sampling rates 44100 and 48000

			soundfile = filename;
			// Load the sound
			if (soundfile.isNil,
				{soundfile = Platform.resourceDir +/+ "sounds/a11wlk01.wav";});
			//soundfile = filename;
			bufferin = Buffer.read(server, soundfile);

			// Config vars
			mode = mod;
			"mode is".postln;
			mode.postln;
			numspeakers = 2;

			// Synths here

			SynthDef(\sourcedef, { | out = 0, amp = 0.7 |
				var sig;
				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});

				Out.ar(out, sig!numspeakers * amp);
			}).send(server);

			SynthDef(\onsetsdef, {|out = 0, amp = 0.7, onsets = 1|

				var sig;
				var chain, trigger;
				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});
				chain = FFT(bufferonsets, sig);
				onsets = Onsets.kr(chain, 0.5, \complex);
				SendTrig.kr(onsets).poll(label:\onset);

			}).send(server);

			SynthDef(\ampsdef, {|out = 0|

				var sig;
				var amps;
				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});
				amps = Amplitude.ar(sig).poll(label:\amp);
				SendTrig.kr(Dust.kr(20), value:amps);

			}).send(server);

			SynthDef('beattrkdef', { | out = 0, amp = 0.7 |

				var sig;
				var trackb, trackh, trackq, tempo;
				var beep;
				var bsound, hsound, qsound;

				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});

				#trackb, trackh, trackq, tempo = BeatTrack.kr(FFT(bufferbeattrack.bufnum, sig));

				beep = PinkNoise.ar(Decay.kr(trackb, 0.05));

				Out.ar(0, beep!numspeakers);

			}).send(server);


			SynthDef(\pitchdef, {|out = 0|

				var sig;
				var amp, freq, hasFreq;
				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});
				amp = Amplitude.ar(sig).poll(label:\amp);
				 #freq, hasFreq = Pitch.kr(sig).poll(label:\pitch);
				Out.ar(out, LFTri.ar(freq!numspeakers) + sig * amp);

			}).send(server);

			SynthDef(\keydef, {|out = 0, amp = 0.7|

				var sig, chain, key;

				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});

				chain = FFT(bufferkeytrack, sig);
				key = KeyTrack.kr(chain, 2.0, 0.5).poll;
				Out.ar(out, sig!numspeakers * amp);

			}).send(server);


			SynthDef('percdef', { | out = 0, freq = 90, onset = 0, amp = 0.7 |

				var sig;
				var env, envsh, envshape, envlist;

				sig = Pulse.ar(freq, 0.3);

				envlist = [1, 2, 3, 4];

				envshape = envlist.choose;

				envsh = case
				{ envshape == 1} { Env.perc([0.001, 1, 1, -4]) } // \normal
				{ envshape == 2 } { Env.perc([0.001, 1, 1, -4]) } // \sharp
				{ envshape == 3 } { Env.perc([0.001, 1, 1, -8]) } // \short
				{ envshape == 4 } { Env.perc(1, 0.01, 1, 4) }; // \rev

				env = EnvGen.kr(envsh, onset, doneAction: 2);
				Out.ar(out, sig*env!numspeakers * amp);

			}).send(server);


			SynthDef('sparkdef', { | out = 0, onset = 0, amp = 0.7, freq1 = 300, freq2 = 3000 |

				var sig;
				var env, trig;

				trig = Dust.kr(onset);
				sig =  SinOsc.ar(TRand.kr(freq1, freq2, trig)) * 0.1;
				env = EnvGen.kr(Env.perc([0.001, 1, 1, -4]), onset, doneAction: 2);

				Out.ar(out, sig*env!numspeakers * amp);

			}).send(server);


			SynthDef('beepdef', { | out = 0, onset = 0, amp = 0.7, freq = 1 |

				var sig;
				var env;

				sig =  Impulse.ar(freq, 0.0, 0.5, 0);
				env = EnvGen.kr(Env.perc([0.001, 1, 1, -4]), onset, doneAction: 2);

				Out.ar(out, sig*env!numspeakers * amp);

			}).send(server);

		}); // end of waitForBoot()
	}//--// end of init()

	// Functions here

   //------------------//
    // SOURCE
    //------------------//
	source {
		audiosource = Synth.new(\sourcedef);
	} //--//


	//------------------//
    // FREE
    //------------------//
	free {
		audiosource.free;
		audioout.free;
	} //--//

   //------------------//
    // SCOPE
    //------------------//
	scope {
		server.scope;
	} //--//

    //------------------//
    // ONSETS
    //------------------//
		onsets { |instr = 'perc', freqin = 90, envshapein = 'normal', freqini = 200, freqend = 2000|
		audiosource = Synth.new(\onsetsdef);
		osctr = OSCFunc({ arg msg, time;
			msg.postln;

			case
				{instr == \perc} { audioout =  Synth.new(\percdef, [\onset, 1, \freq, freqin]); }
			{instr == \spark} { audioout =  Synth.new(\sparkdef, [\onset, 1, \freq1, freqini, \freq2, freqend]); }
			{instr == \beep} { audioout =  Synth.new(\beepdef, [\onset, 1, \freq, freqin]); };

		},'/tr', server.addr);

	} //--//


    //------------------//
    // BEATS
    //------------------//
	beats {
		audioout =  Synth.new(\beattrkdef);
	} //--//

	//------------------//
    // PITCH
    //------------------//
	pitch {
		audioout =  Synth.new(\pitchdef);
	} //--//

    //------------------//
    // KEY
    //------------------//
	key {
		audioout =  Synth.new(\keydef);
	} //--//

    //------------------//
    // AMPS
    //------------------//
	amps { |instr = 'perc'|
		var ampin;
		audioout =  Synth.new(\ampsdef);
		osctr = OSCFunc({ arg msg, time;
		ampin = msg[3];

		case
			{instr == \perc} { audioout =  Synth.new(\percdef, [\onset, 1, \amp, ampin]); }
			{instr == \spark} { audioout =  Synth.new(\sparkdef, [\onset, 1, \amp, ampin]); }
			{instr == \beep} { audioout =  Synth.new(\beepdef, [\onset, 1, \amp, ampin]); };

		},'/tr', server.addr);
	} //--//

}
	