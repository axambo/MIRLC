MIRLCRex {

    classvar server;
	var bufferin1, bufferin2, bufferin3, bufferin4;
	var busbuffer1, busbuffer2, busbuffer3, busbuffer4, busbuffersscope;
	var scope4;
	var busspectral1, busspectral2, busspectral3, busspectral4;
	var <g1, <g2, <g3, <g4;
	var msg;
	var path = "/Users/annaxambo/Desktop/MIRLCRex/"; // path to the audio streams
	var diskincond, mixcond, spatialcond, eqcond; // boolean
	var stream1, stream2, stream3, stream4; // diskin audio files
	var spatial1, spatial2, spatial3, spatial4;
	var equalized1, equalized2, equalized3, equalized4;
	var numchannels = 2;
	var numstreams = 4;
	var amp1func, amp2func, amp3func, amp4func;
	var osctr1, osctr2, osctr3, osctr4;
	var oscfreq1, oscfreq2, oscfreq3, oscfreq4;
	var minamp = 0.0;
	var maxamp;
	var amp1, amp2, amp3, amp4;
	var normamp1, normamp2, normamp3, normamp4;
	var mode, pastmode; // spatial mode
	var pos1, pos2, pos3, pos4, posneutral;
	var busnamelowpf, busnamehighpf, busnamebandpf1, busnamebandpf2;
	var spectrum1, spectrum2, spectrum3, spectrum4;
	var freq1, freq2, freq3, freq4;
	var freq1eq, freq2eq, freq3eq, freq4eq;

	*new {
		^super.new.init;
    }

    //------------------//
    // INIT
    //------------------//
    init {
        server = Server.local;
        server.boot;

		server.waitForBoot({

			// Groups
			g1 = Group.tail(server); // ~sources
			g2 = Group.tail(server); // ~effects: eq
			g3 = Group.tail(server); // ~effects: spatial
			g4 = Group.tail(server); // ~scope

			// Buses
			busbuffer1 =  Bus.audio(server, numchannels); // private audio bus for each audio stream
			busbuffer2 =  Bus.audio(server, numchannels);
			busbuffer3 =  Bus.audio(server, numchannels);
			busbuffer4 =  Bus.audio(server, numchannels);
			("busbuffer1 "+busbuffer1.index).postln;
			("busbuffer2 "+busbuffer2.index).postln;
			("busbuffer3 "+busbuffer3.index).postln;
			("busbuffer4 "+busbuffer4.index).postln;

			busbuffersscope =  Bus.audio(server, numstreams);
			("busbuffersscope "+busbuffersscope.index).postln;

			busspectral1 = Buffer.alloc(server, 2048, 1);
			busspectral2 = Buffer.alloc(server, 2048, 1);
			busspectral3 = Buffer.alloc(server, 2048, 1);
			busspectral4 = Buffer.alloc(server, 2048, 1);

			// Buffers => link to filenames of audio streams
			bufferin1 = Buffer.cueSoundFile(server, path +/+ "507773__laspaziale__overnight-train-from-trondheim-to-oslo_mono.wav", 0, 1);
			"loading stream1...".postln;
			"507773__laspaziale__overnight-train-from-trondheim-to-oslo_mono.wav".postln;
			bufferin2 = Buffer.cueSoundFile(server, path +/+ "478008__laspaziale__soundwalk-with-an-induction-microphone_mono.wav", 0, 1);
			"loading stream2...".postln;
			"478008__laspaziale__soundwalk-with-an-induction-microphone_mono.wav".postln;
			bufferin3 = Buffer.cueSoundFile(server, path +/+ "447539__laspaziale__spatial-audio-soundflower-feedback_mono.wav", 0, 1);
			"loading stream3...".postln;
			"447539__laspaziale__spatial-audio-soundflower-feedback_mono.wav".postln;
			bufferin4 = Buffer.cueSoundFile(server, path +/+ "435001__laspaziale__testing-dato-duo_mono.wav", 0, 1);
			"loading stream4...".postln;
			"435001__laspaziale__testing-dato-duo_mono.wav".postln;

			// Other vars
			maxamp = 1.0 / numstreams;
			mode = 0;
			posneutral = 0.0; // default value of \spatializestream \posinit
			diskincond = 0;
			mixcond = 0;
			spatialcond = 0;
			eqcond = 0;
			pos1 = -1.0;
			pos2 = -0.5;
			pos3 = 0.5;
			pos4 = 1.0;

			// Synths here

			SynthDef(\readdiskin, { |obs = 0, bufnum = 0, amp = 0.75|
				var sig;
				sig =  DiskIn.ar(1, bufnum, loop:1);
				Out.ar(obs, sig!2 * amp);
			}).add;

			SynthDef(\spatializestream, { | ibs = 0, obs = 0, pos = 0.0, posinit = 0.0 |
				var sig;
				// sig = Pan2.ar(In.ar(ibs), 0.0); // for testing
				sig = Pan2.ar(In.ar(ibs), Line.kr(posinit, pos, 5));
				Out.ar(obs, sig);
			}).add;

			SynthDef(\amps1def, {|ibs = 0|
				var sig, amps, trig;
				sig = In.ar(ibs);
				amps = Amplitude.kr(sig);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/tramps1', amps);
			}).add;

			SynthDef(\amps2def, {|ibs = 0|
				var sig, amps, trig;
				sig = In.ar(ibs);
				amps = Amplitude.kr(sig);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/tramps2', amps);
			}).add;

			SynthDef(\amps3def, {|ibs = 0|
				var sig, amps, trig;
				sig = In.ar(ibs);
				amps = Amplitude.kr(sig);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/tramps3', amps);
			}).add;

			SynthDef(\amps4def, {|ibs = 0|
				var sig, amps, trig;
				sig = In.ar(ibs);
				amps = Amplitude.kr(sig);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/tramps4', amps);
			}).add;

			SynthDef(\lowpass_filter, {
				arg ibs, obs, cutfreq = 1300,
				atk=0.1, sus=1, rel=0.2, crv=2, gate=1;
				var source, sig, env;
				env = EnvGen.ar(Env.asr(atk, sus, rel, crv), gate);
				source = In.ar([ibs, ibs]);
				sig = LPF.ar(source, cutfreq);
				ReplaceOut.ar(obs, sig*env);
			}).add;

			SynthDef(\highpass_filter, {
				arg ibs, obs, cutfreq = 4000,
				atk=0.1, sus=1, rel=0.2, crv=2, gate=1;
				var source, sig, env;
				env = EnvGen.ar(Env.asr(atk, sus, rel, crv), gate);
				source = In.ar([ibs, ibs]);
				sig = HPF.ar(source);
				Out.ar(obs, sig*env);
			}).add;

			SynthDef(\bandpass_filter_low, {
				arg ibs, obs, cutfreq = 450, q = 2.0,
				atk=0.1, sus=1, rel=0.2, crv=2, gate=1, freq;
				var source, sig, env;
				env = EnvGen.ar(Env.asr(atk, sus, rel, crv), gate);
				source = In.ar([ibs, ibs]);
				sig = BPF.ar(source, cutfreq, q);
				Out.ar(obs, sig*env);
			}).add;

			SynthDef(\bandpass_filter_high, {
				arg ibs, obs, cutfreq = 900, q = 2.0,
				atk=0.1, sus=1, rel=0.2, crv=2, gate=1, freq;
				var source, sig, env;
				env = EnvGen.ar(Env.asr(atk, sus, rel, crv), gate);
				source = In.ar([ibs, ibs]);
				sig = BPF.ar(source, cutfreq, q);
				Out.ar(obs, sig*env);
			}).add;

			SynthDef(\spectralcentroid1, { |ibs = 0, obs = 0|
				var sig, chain, centroid, trig;
				sig = In.ar(ibs);
				// sig = WhiteNoise.ar(0.1);
				chain = FFT(LocalBuf(2048), sig, wintype:1);
				centroid = SpecCentroid.kr(chain);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/freqs1', centroid);
				// centroid.poll(1);
				// sig.dup * 0.1;
			}).add;

			SynthDef(\spectralcentroid2, { |ibs = 0, obs = 0|
				var sig, chain, centroid, trig;
				sig = In.ar(ibs);
				chain = FFT(LocalBuf(2048), sig, wintype:1);
				centroid = SpecCentroid.kr(chain);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/freqs2', centroid);
			}).add;

			SynthDef(\spectralcentroid3, { |ibs = 0, obs = 0|
				var sig, chain, centroid, trig;
				sig = In.ar(ibs);
				chain = FFT(LocalBuf(2048), sig, wintype:1);
				centroid = SpecCentroid.kr(chain);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/freqs3', centroid);
			}).add;

			SynthDef(\spectralcentroid4, { |ibs = 0, obs = 0|
				var sig, chain, centroid, trig;
				sig = In.ar(ibs);
				chain = FFT(LocalBuf(2048), sig, wintype:1);
				centroid = SpecCentroid.kr(chain);
				trig = Dust.kr(0.5);
				SendReply.kr(trig, '/freqs4', centroid);
			}).add;

			SynthDef(\scopebuses, { |ibs1 = 0, ibs2 = 0, ibs3 = 0, ibs4 = 0, obs = 0|
				var sig;
				sig = [In.ar(ibs1), In.ar(ibs2), In.ar(ibs3), In.ar(ibs4)];
				Out.ar(obs, sig);
			}).add;

		}); // end of waitForBoot()
	}//--// end of init()

	// Functions here

   //------------------//
    // ORIGINAL STREAMS
    //------------------//
	original { // Always start with this function

		"Original streams (without effects)".postln;
		if ( diskincond == 1,
			{
				// if spatial = TRUE, transition from a) or from b) normalized
				if ( mode == 1,
					{
						spatial1.free; spatial2.free; spatial3.free; spatial4.free;
						spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, posneutral, \posinit, pos1, \amp, maxamp], g3);
						spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, posneutral, \posinit, pos2, \amp, maxamp], g3);
						spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, posneutral, \posinit, pos3, \amp, maxamp], g3);
						spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, posneutral, \posinit, pos4, \amp, maxamp], g3);
					},{
						spatial1.free; spatial2.free; spatial3.free; spatial4.free;
						spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, posneutral, \posinit, posneutral], g3);
						spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, posneutral, \posinit, posneutral], g3);
						spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, posneutral, \posinit, posneutral], g3);
						spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, posneutral, \posinit, posneutral], g3);
					}
				);
				mode = 0;
				pastmode = 0;
				osctr1.free; osctr2.free; osctr3.free; osctr4.free;
				oscfreq1.free; oscfreq2.free; oscfreq3.free; oscfreq4.free;
				equalized1.free; equalized2.free; equalized3.free; equalized4.free;
				equalized1 = nil; equalized2 = nil; equalized3 = nil; equalized4 = nil;
				"Back to no effects, only original streams...".postln;
			},
			{ // if diskincond = 0
				diskincond = 1;
				// "hello2".postln;

				(numstreams + "original streams currently playing").postln;
				stream1 = Synth.new(\readdiskin, [\bufnum, bufferin1, \obs, busbuffer1.index, \amp, maxamp], g1);
				stream2 = Synth.new(\readdiskin, [\bufnum, bufferin2, \obs, busbuffer2.index, \amp, maxamp], g1);
				stream3 = Synth.new(\readdiskin, [\bufnum, bufferin3, \obs, busbuffer3.index, \amp, maxamp], g1);
				stream4 = Synth.new(\readdiskin, [\bufnum, bufferin4, \obs, busbuffer4.index, \amp, maxamp], g1);

				spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, posneutral], g3);
				spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, posneutral], g3);
				spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, posneutral], g3);
				spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, posneutral], g3);

				this.scope;

			}
		);
	} //--//


   //------------------//
    // SCOPE
    //------------------//
	scope {

		scope4 = Synth.new(\scopebuses, [\ibs1, busbuffer1.index, \ibs2, busbuffer2.index, \ibs3, busbuffer3.index, \ibs4, busbuffer4.index, \obs, busbuffersscope.index], g4);
		busbuffersscope.scope;
	} //--//


   //------------------//
    // MIX STREAMS
    //------------------//
	mix {

		"Automatic mix based on the amplitudes of the streams".postln;

		normamp1 = maxamp;
		normamp2 = maxamp;
		normamp3 = maxamp;
		normamp4 = maxamp;

		if (mixcond == 0, { // only applies once, then ampfuncs remain listening until the end of the program..
			amp1func = Synth.new(\amps1def, [\ibs, busbuffer1.index], g2);
			amp2func = Synth.new(\amps2def, [\ibs, busbuffer2.index], g2);
			amp3func = Synth.new(\amps3def, [\ibs, busbuffer3.index], g2);
			amp4func = Synth.new(\amps4def, [\ibs, busbuffer4.index], g2);
		});

		mixcond = 1;

		// OSC Functions

		osctr1 = OSCFunc ( { arg msg, time;
			amp1 = msg[3];
			("amplitude of stream1:"+amp1).postln;
			normamp1 = ControlSpec(minamp, amp1, \lin).unmap(amp1) / numstreams;
			("normamp1 " + normamp1).postln;
			// normalized1.set(\amp, normamp1);
			stream1.set(\amp, normamp1);
		},'/tramps1', server.addr);

		osctr2 = OSCFunc ( { arg msg, time;
			amp2 = msg[3];
			("amplitude of stream2:"+amp2).postln;
			normamp2 = ControlSpec(minamp, amp2, \lin).unmap(amp2) / numstreams;
			("normamp2 " + normamp2).postln;
			// normalized2.set(\amp, normamp2);
			stream2.set(\amp, normamp2);
		},'/tramps2', server.addr);

		osctr3 = OSCFunc ( { arg msg, time;
			amp3 = msg[3];
			("amplitude of stream3:"+amp3).postln;
			normamp3 = ControlSpec(minamp, amp3, \lin).unmap(amp3)  / numstreams;
			("normamp3 " + normamp3).postln;
			// normalized3.set(\amp, normamp3);
			stream3.set(\amp, normamp3);

		},'/tramps3', server.addr);

		osctr4 = OSCFunc ( { arg msg, time;
			amp4 = msg[3];
			("amplitude of stream4:"+amp4).postln;
			normamp4 = ControlSpec(minamp, amp4, \lin).unmap(amp4) / numstreams;
			("normamp4 " + normamp4).postln;
			// normalized4.set(\amp, normamp4);
			stream4.set(\amp, normamp4);
		},'/tramps4', server.addr);

	} //--//

	//------------------//
    // SPATIALIZE STREAMS
    //------------------//
	spatialize { |mod = 1|

		"Automatic spatialization of the streams".postln;

		mode = mod;

		("mode value " + mode).postln;

		// mode = 1 => mode = 0 or mode = 0 => mode = 1 TRANSITION, else NO TRANSITION, need to track previous state and boolean of whether transition is needed or not

		if ( mode == 1,
			{
				spatial1.free; spatial2.free; spatial3.free; spatial4.free;

				if (  mode == pastmode, // no transition needed
					{
						"spatialized condition is ON without transition, type spatialize(0) to turn it off".postln;
						spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, pos1, \posinit, pos1, \amp, maxamp], g3);
						spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, pos1, \posinit, pos2, \amp, maxamp], g3);
						spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, pos1, \posinit, pos3, \amp, maxamp], g3);
						spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, pos1, \posinit, pos4, \amp, maxamp], g3);
					},
					{ // transition needed
						"spatialized condition is ON with transition, type spatialize(0) to turn it off".postln;
						spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, pos1, \posinit, posneutral, \amp, maxamp], g3);
						spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, pos2, \posinit, posneutral, \amp, maxamp], g3);
						spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, pos3, \posinit, posneutral, \amp, maxamp], g3);
						spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, pos4, \posinit, posneutral, \amp, maxamp], g3);
					}
				);
				pastmode = 1;
			}
		);

		if ( mode == 0,
			{
				spatial1.free; spatial2.free; spatial3.free; spatial4.free;

				if ( mode == pastmode, // no transition needed
					{
					"spatialized condition is OFF without transition, type spatialize(1) to turn it on".postln;
					 spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, posneutral, \posinit, posneutral, \amp, maxamp], g3);
					 spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, posneutral, \posinit, posneutral, \amp, maxamp], g3);
					 spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, posneutral, \posinit, posneutral, \amp, maxamp], g3);
					 spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, posneutral, \posinit, posneutral, \amp, maxamp], g3);
					},
					{ // transition needed
						"spatialized condition is OFF with transition, type spatialize(1) to turn it on".postln;
						spatial1 = Synth.new(\spatializestream, [\ibs, busbuffer1.index, \obs, 0, \pos, posneutral, \posinit, pos1, \amp, maxamp], g3);
						spatial2 = Synth.new(\spatializestream, [\ibs, busbuffer2.index, \obs, 0, \pos, posneutral, \posinit, pos2, \amp, maxamp], g3);
						spatial3 = Synth.new(\spatializestream, [\ibs, busbuffer3.index, \obs, 0, \pos, posneutral, \posinit, pos3, \amp, maxamp], g3);
						spatial4 = Synth.new(\spatializestream, [\ibs, busbuffer4.index, \obs, 0, \pos, posneutral, \posinit, pos4, \amp, maxamp], g3);
					}
				);
				pastmode = 0;
			}
		);


	} //--//


   //------------------//
    // EQUALIZE STREAMS
    //------------------//
	equalize {

		"Automatic equalization based on the spectral centroids of the streams".postln;

		if (eqcond == 0, { // only applies once, then 'spectrum' Synths remain listening until the end of the program..
			spectrum1 = Synth.new(\spectralcentroid1, [\ibs, busbuffer1.index], g2);
			spectrum2 = Synth.new(\spectralcentroid2, [\ibs, busbuffer2.index], g2);
			spectrum3 = Synth.new(\spectralcentroid3, [\ibs, busbuffer3.index], g2);
			spectrum4 = Synth.new(\spectralcentroid4, [\ibs, busbuffer4.index], g2);
		});

		eqcond = 1;
		freq1eq = 0; freq2eq = 0; freq3eq = 0; freq4eq = 0;

	    // OSC functions

		oscfreq1 = OSCFunc ( { arg msg, time;
		freq1 = msg[3];
		("spectral centroid of stream1:"+freq1).postln;

		if ( freq1 < 1300.0, // LPF => freq1eq = 1
		{
				if ( freq1eq != 1, {
						equalized1.free;
						equalized1 = nil;
					});
				freq1eq = 1;

				if (equalized1.isNil,
				{
					equalized1 = Synth.new(\lowpass_filter, [\ibs, busbuffer1.index, \obs,  busbuffer1.index], g2);

					"LPF for stream 1".postln;
				},
				{
					"LPF for stream 1, still there".postln;
				});
		});

			if ( (freq1 > 1300.0) && (freq1 < 2200.0), // BPF1 => freq1eq = 2
			{
				if ( freq1eq != 2, {
						equalized1.free;
						equalized1 = nil;
					});
				freq1eq = 2;

				if (equalized1.isNil,
				{
					equalized1 = Synth.new(\bandpass_filter_low, [\ibs, busbuffer1.index, \obs,  busbuffer1.index], g2);

					"BPF-low for stream 1".postln;
				},
				{
					"BPF-low for stream 1, still there".postln;
				});
			});


		if ( (freq1 > 2200.0) && (freq1 < 4000.0), // BPF2 => freq1eq = 3
		{
				if ( freq1eq != 3, {
						equalized1.free;
						equalized1 = nil;
					});
				freq1eq = 3;
				if (equalized1.isNil,
				{
					equalized1 =  Synth.new(\bandpass_filter_high, [\ibs, busbuffer1.index, \obs,  busbuffer1.index], g2);

					"BPF-high for stream 1".postln;
				},
				{
					"BPF-high for stream 1, still there".postln;
				});
		});

		if ( freq1 > 4000.0, // HPF => freq1eq = 4
		{
				if ( freq1eq != 4, {
						equalized1.free;
						equalized1 = nil;
					});
				freq1eq = 4;
				if (equalized1.isNil,
				{
					equalized1 = Synth.new(\highpass_filter, [\ibs, busbuffer1.index, \obs,  busbuffer1.index], g2);

					"HPF for stream 1".postln;
				},
				{
					"HPF for stream 1, still there".postln;
				});
		});
		},'/freqs1', server.addr);


		oscfreq2 = OSCFunc ( { arg msg, time;
			freq2 = msg[3];
			("spectral centroid of stream2:"+freq2).postln;

			if ( freq2 < 1300.0, // LPF => freq2eq = 1
			{

				if ( freq2eq != 1, {
						equalized2.free;
						equalized2 = nil;
					});
				freq2eq = 1;

				if (equalized2.isNil,
				{
					equalized2 = Synth.new(\lowpass_filter, [\ibs, busbuffer2.index, \obs,  busbuffer2.index], g2);

					"LPF for stream 2".postln;
				},
				{
					"LPF for stream 2, still there".postln;
				});
			});

			if ( (freq2 > 1300.0) && (freq2 < 2200.0), // BPF1 => freq2eq = 2
			{
				if ( freq2eq != 2, {
						equalized2.free;
						equalized2 = nil;
					});
				freq2eq = 2;

				if (equalized2.isNil,
				{
					equalized2 = Synth.new(\bandpass_filter_low, [\ibs, busbuffer2.index, \obs,  busbuffer2.index], g2);

					"BPF-low for stream 2".postln;
				},
				{
					"BPF-low for stream 2, still there".postln;
				});
			});

			if ( (freq2 > 2200.0) && (freq2 < 4000.0), // BPF2 => freq2eq = 3
			 {
				if ( freq2eq != 3, {
						equalized2.free;
						equalized2 = nil;
					});
				freq2eq = 3;
				if (equalized2.isNil,
				{
					equalized2 = Synth.new(\bandpass_filter_high, [\ibs, busbuffer2.index, \obs,  busbuffer2.index], g2);

					"BPF-high for stream 2".postln;
				},
				{
					"BPF-high for stream 2, still there".postln;
				});
			});

			 if ( freq2 > 4000.0, // HPF => freq2eq = 4
			{
				if ( freq2eq != 4, {
						equalized2.free;
						equalized2 = nil;
					});
				freq2eq = 4;
				if (equalized2.isNil,
				{
					equalized2 = Synth.new(\highpass_filter, [\ibs, busbuffer2.index, \obs,  busbuffer2.index], g2);

					"HPF for stream 2".postln;
				},
				{
					"HPF for stream 2, still there".postln;
				});
			});


		},'/freqs2', server.addr);

		oscfreq3 = OSCFunc ( { arg msg, time;
			freq3 = msg[3];
			("spectral centroid of stream3:"+freq3).postln;

			if ( freq3 < 1300.0, // LPF => freq3eq = 1
			{

				if ( freq3eq != 1, {
						equalized3.free;
						equalized3 = nil;
					});
				freq3eq = 1;

				if (equalized3.isNil,
				{
					equalized3 = Synth.new(\lowpass_filter, [\ibs, busbuffer3.index, \obs,  busbuffer3.index], g2);

					"LPF for stream 3".postln;
				},
				{
					"LPF for stream 3, still there".postln;
				});
			});

			if ( (freq3 > 1300.0) && (freq3 < 2200.0), // BPF1 => freq3eq = 2
			{
				if ( freq3eq != 2, {
						equalized3.free;
						equalized3 = nil;
					});
				freq3eq = 2;

				if (equalized3.isNil,
				{
					equalized3 = Synth.new(\bandpass_filter_low, [\ibs, busbuffer3.index, \obs,  busbuffer3.index], g2);

					"BPF-low for stream 3".postln;
				},
				{
					"BPF-low for stream 3, still there".postln;
				});
			});

			if ( (freq3 > 2200.0) && (freq3 < 4000.0), // BPF2 => freq3eq = 3
			 {
				if ( freq3eq != 3, {
						equalized3.free;
						equalized3 = nil;
					});
				freq3eq = 3;
				if (equalized3.isNil,
				{
					equalized3 = Synth.new(\bandpass_filter_high, [\ibs, busbuffer3.index, \obs,  busbuffer3.index], g2);

					"BPF-high for stream 3".postln;
				},
				{
					"BPF-high for stream 3, still there".postln;
				});
			});

			 if ( freq3 > 4000.0, // HPF => freq3eq = 4
			{
				if ( freq3eq != 4, {
						equalized3.free;
						equalized3 = nil;
					});
				freq3eq = 4;
				if (equalized3.isNil,
				{
					equalized3 = Synth.new(\highpass_filter, [\ibs, busbuffer3.index, \obs,  busbuffer3.index], g2);

					"HPF for stream 3".postln;
				},
				{
					"HPF for stream 3, still there".postln;
				});
			});


		},'/freqs3', server.addr);

		oscfreq4 = OSCFunc ( { arg msg, time;
			freq4 = msg[3];
			("spectral centroid of stream4:"+freq4).postln;

			if ( freq4 < 1300.0, // LPF => freq4eq = 1
			{

				if ( freq4eq != 1, {
						equalized4.free;
						equalized4 = nil;
					});
				freq4eq = 1;

				if (equalized4.isNil,
				{
					equalized4 = Synth.new(\lowpass_filter, [\ibs, busbuffer4.index, \obs,  busbuffer4.index], g2);

					"LPF for stream 4".postln;
				},
				{
					"LPF for stream 4, still there".postln;
				});
			});

			if ( (freq4 > 1300.0) && (freq4 < 2200.0), // BPF1 => freq4eq = 2
			{
				if ( freq4eq != 2, {
						equalized4.free;
						equalized4 = nil;
					});
				freq4eq = 2;

				if (equalized4.isNil,
				{
					equalized4 = Synth.new(\bandpass_filter_low, [\ibs, busbuffer4.index, \obs,  busbuffer4.index], g2);

					"BPF-low for stream 4".postln;
				},
				{
					"BPF-low for stream 4, still there".postln;
				});
			});

			if ( (freq4 > 2200.0) && (freq4 < 4000.0), // BPF2 => freq3eq = 3
			 {
				if ( freq4eq != 3, {
						equalized4.free;
						equalized4 = nil;
					});
				freq4eq = 3;
				if (equalized4.isNil,
				{
					equalized4 = Synth.new(\bandpass_filter_high, [\ibs, busbuffer4.index, \obs,  busbuffer4.index], g2);

					"BPF-high for stream 4".postln;
				},
				{
					"BPF-high for stream 4, still there".postln;
				});
			});

			 if ( freq4 > 4000.0, // HPF => freq3eq = 4
			{
				if ( freq4eq != 4, {
						equalized4.free;
						equalized4 = nil;
					});
				freq4eq = 4;
				if (equalized4.isNil,
				{
					equalized4 = Synth.new(\highpass_filter, [\ibs, busbuffer4.index, \obs,  busbuffer4.index], g2);

					"HPF for stream 4".postln;
				},
				{
					"HPF for stream 4, still there".postln;
				});
			});


		},'/freqs4', server.addr);

	} //--//

    //------------------//
    // CMD PERIOD
    //------------------//
    // This function is activated when stopping the code / recompiling / etc.
    cmdPeriod {
        currentEnvironment.clear;
		busbuffer1.free; busbuffer2.free; busbuffer3.free; busbuffer4.free;
		osctr1.free; osctr2.free; osctr3.free;	osctr4.free;
		oscfreq1.free; oscfreq2.free; oscfreq3.free; oscfreq4.free;
		amp1func.free; amp2func.free; amp3func.free; amp4func.free;
		spectrum1.free; spectrum2.free; spectrum3.free; spectrum4.free;
    } //--//


}