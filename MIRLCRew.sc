MIRLCRew {

    classvar server;
	var audiosource;
	var overlaysig;
	classvar <>mode, <>soundfile; // 0 => audio in; 1 => audio recording
	var audioout;
	classvar <counter = 0;
	var bufferin, bufferbeattrack, bufferonsets, bufferkeytrack;
	var numspeakers;
	var osctr;
	var bypasschannel = 100;
	var miredsound = 0;

	*new {|mod = 0, filename|
		^super.new.init(mod, filename);
    }

    //------------------//
    // INIT
    //------------------//
    init { |mod, filename|
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
				{
					//soundfile = Platform.resourceDir +/+ "sounds/a11wlk01.wav";
					soundfile = "/Users/annaxambo/Desktop/MIRLC/128629__bigjoedrummer__afro-beat-6-8-toms-mono.wav";
			});
			bufferin = Buffer.read(server, soundfile);

			// Config vars
			mode = mod;
			if (mode ==0,
				{"Listening to audio in...".postln},
				{"Listening to an audio track...".postln}
			);
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

			SynthDef(\onsetsdef, {|out = 0, onsets = 1|

				var sig;
				var chain;

				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});
				chain = FFT(bufferonsets, sig);
				onsets = Onsets.kr(chain, 0.5, \complex);
				SendTrig.kr(onsets, value: 1).poll(label:\onset);
				Out.ar(bypasschannel, sig);

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
				Out.ar(bypasschannel, sig);

			}).send(server);

			SynthDef(\beattrkdef, { | out = 0, amp = 0.7 |

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

				Out.ar(out, beep!numspeakers);
				Out.ar(bypasschannel, sig);

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
				Out.ar(bypasschannel, sig);

			}).send(server);

			SynthDef(\keydef, {|out = 0, amp = 0.7|

				var sig;
				var chain, key;

				if (mode == 1, {
					sig = PlayBuf.ar(1, bufferin, BufRateScale.kr(bufferin), loop: 1);
				},
				{
					sig = SoundIn.ar(0);
				});

				chain = FFT(bufferkeytrack, sig);
				key = KeyTrack.kr(chain, 2.0, 0.5).poll;
				Out.ar(out, sig!numspeakers * amp);
				Out.ar(bypasschannel, sig);

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
				Out.ar(bypasschannel, sig);

			}).send(server);


			SynthDef('sparkdef', { | out = 0, onset = 0, amp = 0.7, freq1 = 300, freq2 = 3000 |

				var sig;
				var env, trig;

				trig = Dust.kr(onset);
				sig =  SinOsc.ar(TRand.kr(freq1, freq2, trig)) * 0.1;
				env = EnvGen.kr(Env.perc([0.001, 1, 1, -4]), onset, doneAction: 2);

				Out.ar(out, sig*env!numspeakers * amp);
				Out.ar(bypasschannel, sig);

			}).send(server);


			SynthDef('beepdef', { | out = 0, onset = 0, amp = 0.7, freq = 1 |

				var sig;
				var env;

				sig =  Impulse.ar(freq, 0.0, 0.5, 0);
				env = EnvGen.kr(Env.perc([0.001, 1, 1, -4]), onset, doneAction: 2);

				Out.ar(out, sig*env!numspeakers * amp);
				Out.ar(bypasschannel, sig);

			}).send(server);

			SynthDef(\overlaydef, { | in=100, out = 0, amp = 0.7 |
				var sig;
				sig = In.ar(in, 1);
				Out.ar(out, sig!numspeakers * amp);
			}).send(server);



		}); // end of waitForBoot()
	}//--// end of init()

	// Functions here

   //------------------//
    // SOURCE
    //------------------//
	source {

		"Listening to original sound source...".postln;
		if (audiosource.notNil,
			{

				miredsound = 0;
				audiosource.free;
				audiosource = Synth.new(\sourcedef);
		},
			{
				audiosource = Synth.new(\sourcedef);
			}
		);

	} //--//

   //------------------//
    // OVERLAY
    //------------------//
	overlay { |mode = 'on'|
		if (  miredsound == 0 ,
			{
				"Use source to listen to the original source or apply an effect first (e.g. onsets, beats, and so on)".postln;
			},
			{
				case
				{mode == \off} {
					"Desactivating overlay of the original source to the audio signal...".postln;
					if ( overlaysig.notNil,  { overlaysig.free; "bypass sig free".postln } );
				}
				{mode == \on} {
					if (overlaysig.notNil,
						{overlaysig.free});
					"Overlaying original source to the audio signal...".postln;
					overlaysig = Synth.new(\overlaydef, [\in, bypasschannel], addAction:\addToTail);
				}
			}
		);

	} //--//

	//------------------//
    // FREE
    //------------------//
	free {
		audiosource.free;
		audioout.free;
		osctr.free;
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
		onsets { |instr = 'beep', freqin = 90, envshapein = 'normal', freqini = 200, freqend = 2000|
		miredsound = 1;
		if (audiosource.notNil,
				{audiosource.free});
		if (osctr.notNil,
				{osctr.free});
		if (audioout.notNil,
			{audioout.free});
		audiosource = Synth.new(\onsetsdef);
		osctr = OSCFunc({ arg msg, time;
			msg.postln;
			time.postln;
			counter = counter + 1;
			counter.postln;
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
		miredsound = 1;
		if (audioout.notNil,
			{audioout.free});
		if (audiosource.notNil,
				{audiosource.free});
		audioout =  Synth.new(\beattrkdef);
	} //--//

	//------------------//
    // PITCH
    //------------------//
	pitch {
		miredsound = 1;
		if (audioout.notNil,
			{audioout.free});
		if (audiosource.notNil,
				{audiosource.free});
		audioout =  Synth.new(\pitchdef);
	} //--//

    //------------------//
    // KEY
    //------------------//
	key {
		miredsound = 1;
		if (audioout.notNil,
			{audioout.free});
		if (audiosource.notNil,
				{audiosource.free});
		audioout =  Synth.new(\keydef);
	} //--//

    //------------------//
    // AMPS
    //------------------//
	amps { |instr = 'perc'|
		var ampin;
		miredsound = 1;
		if (audiosource.notNil,
			{audiosource.free});
		if (osctr.notNil,
			{osctr.free});
		if (audioout.notNil,
			{audioout.free});
		audiosource =  Synth.new(\ampsdef);
		osctr = OSCFunc({ arg msg, time;
		ampin = msg[3];

		case
			{instr == \perc} { audioout =  Synth.new(\percdef, [\onset, 1, \amp, ampin]); }
			{instr == \spark} { audioout =  Synth.new(\sparkdef, [\onset, 1, \amp, ampin]); }
			{instr == \beep} { audioout =  Synth.new(\beepdef, [\onset, 1, \amp, ampin]); };

		},'/tr', server.addr);
	} //--//


}
	