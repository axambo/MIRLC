MIRLC {

	classvar <>server;
	var boolsimil, debugging, poolsizeold, sequential;
	var poolsnd, buffers, playb;
	var snd, preview, buf, target;
	var index, indexold, keys, counter, sndid, numsnds, size, rndnums;
	var window, viewoscil;

	*new {
        ^super.new.init
    }

	init {
		server = Server.local;
		server.boot;
		poolsnd = Dictionary.new;
		buffers = Dictionary.new;
		playb = Dictionary.new;
		debugging = True;
		poolsizeold = 0;
		counter = 0;
		indexold = 0;

		Freesound.authType = "token";// default, only needed if you changed it
		//Freesound.token="<your_api_key>";// change it to own API key token
		Freesound.token="5a837b803eb5a6da25dd3b42346fd6550080b919";// change it to own API key token

		SynthDef(\playbuf_mono_fs, {
			|bufnum, buf, amp = 1, out = 0, rate = 1, da = 0, loop = 1, trig = 0, gate = 1|
			var sig, env;
			sig = PlayBuf.ar(1, bufnum, BufRateScale.kr(buf) * rate,  doneAction: da, loop: loop, trigger: trig);
			//env = EnvGen.kr(Env.asr(20.0,2.0,10.0, 'sine'), gate, doneAction: 2);
			sig = sig * amp;
			Out.ar(out, sig!2);
		}).add;

		//this.scope;

	}

	// QUERY BY EXAMPLE
	// SEARCH & FIND METHODS OF A SINGLE SOUND OR POOL OF SOUNDS (PUBLIC)
	// FUNCTIONS: randomseed, tagseed, getsimilar, getbyfilter


	// GET EXAMPLE SEED
	// This function gets from one to multiple sounds (size) by random, and plays them
	randomseed { |size = 1|

		// if ( debugging == True, {postln("Sounds selected by random: " ++ size);} );
		sndid = rrand (1, 394004); //todo: retrieve the highest # of FS sound dynamically
		FSSound.getSound ( sndid,
		{ |f|

			snd = f;

			if ( snd["detail"] == nil,
				{
					if ( debugging == True, {
						postln("potential sound candidate: ");
						snd["name"].postln;
					});
					postln("counter value is: " + counter);
					counter = counter + 1;
					if (size == 1,
						{ this.getsound(sndid, size); },
						{//size > 1
							//this.getsound(sndid, size);
							postln("group size is greater than 1");
								postln("( counter - size ): " ++ ( counter - size ));
							if ( counter <= size ,
							//if ( (poolsnd.size - poolsizeold - size) < 0 ,
								{
									this.getsound(sndid, size);
									if ( counter < size, { this.randomseed(size); } );
								}
							);
						}
					);
				},
			{
				if ( debugging == True, {"SoundID does not exist".postln;} );
				this.randomseed(size);
			} );
		} );
	}

	// GET EXAMPLE SEED
	// This function gets from one to a multiple sounds (size) by one defined tag, and plays them
	tagseed { |tag = "noise", size = 1|

		if ( debugging == True, {
			postln("Sounds selected by tag: " ++ size);
		});
		FSSound.textSearch( query: tag, filter: "type:wav", params: ('page': 2),
			action: { |p|
				size.do { |index|
					snd = p[index];
					postln("found sound by tag, id: " ++ snd["id"] ++ "name: " ++ snd["name"]);
					this.getsound(snd.id, 1); // so that each sound is loaded directly played
				}
		});
	}

	// GET SIMILAR SOUNDS FROM SEED
	// This function gets from one to multiple similar sounds (number defined by size) from a target sound, usually the first sound from the dictionary
	getsimilar { | size = 1, targetnumsnd = 1 |

		target = poolsnd[targetnumsnd - 1];

		target.getSimilar(
			action: { |p|
				postln("target: " ++ target["name"]);
				postln("new: " ++ snd["name"]);
				postln("found sound by similarity, id: " ++ snd["id"] ++ "name: " ++ snd["name"]);
				size.do { |index|
					snd = p[index+1];
					this.getsound(snd.id, 1); // so that each sound is loaded directly played
				}
		});

	}

	// GET SIMILAR SOUNDS BY RANGE
	getbyfilter { |size = 1, targetnumsnd = 1, fx = '.lowlevel.pitch_instantaneous_confidence.mean:[0.8 TO 1]' |

		sndid = poolsnd[targetnumsnd - 1].id;

		FSSound.contentSearch(
			target: sndid,
			filter: fx,
			params: ('page':2),
			action: {|p|
			size.do { |index|
					snd = p[index];
					snd.name.postln;
					this.getsound(snd.id, 1); // so that each sound is loaded directly played
				}
			}
		);
	}


	// QUERY BY CONTENT:
	// SEARCH & FIND METHODS OF A SINGLE SOUND OR POOL OF SOUNDS (PUBLIC)
	// FUNCTIONS: contentseed

	contentseed { |size = 1, feature = '.lowlevel.pitch.mean:600', fx = '.lowlevel.pitch_instantaneous_confidence.mean:[0.8 TO 1]' |

		FSSound.contentSearch(
			target: feature,
			filter: fx,
			params: ('page':2),
			action: {|p|
			size.do { |index|
					snd = p[index];
					snd.name.postln;
					this.getsound(snd.id, 1); // so that each sound is loaded directly played
				}
			}
		);
	}

	// RETRIEVAL & PLAY OF A SINGLE SOUND OR POOL OF SOUNDS (PUBLIC)
	// FUNCTIONS: getsound

	// This function gets either one sound or multiple sounds by ID and plays them
	// params: id, size
	// This function can be used as a standalone, and it is also used by randomseed, tagseed, getsimilar, getbyfilter, contentseed
	getsound { |id = 31362, size = 1|

		FSSound.getSound(id,
		{ |f|

			snd = f;
			index = poolsnd.size;

			if ( debugging == True, {
				postln("Loading sound... " ++ snd["name"] ++ " by " ++ snd["username"]);
				postln("Tags: " ++ snd["tags"]);
				postln("overall size of this group is: " ++ size);
			});

			poolsnd.add(index -> f);

			if ( debugging == True, {
				postln("size of poolsnd.size is: " ++ poolsnd.size);
				postln("size of this group is: " ++ size);
			});

			if (size == 1) {
				this.retrievepool(poolsnd, size);
				this.printfspool;
			}{ // size > 1
				if ( (poolsnd.size - poolsizeold) == size, // need to wait until asynchronous call is ready! once all sounds are added in the dictionary, they can be retrieved
					{
						this.retrievepool(poolsnd, size);
						this.printfspool;
					}
				);
			}
		} );
	}


	// OPERATIONS ON LOADED SOUNDS (PUBLIC)
	// FUNCTIONS: play

	// This function plays the first sound of the class Dictionary collection play(1), otherwise it plays all
	//TODO: make it play from the beginning, tried startpos, trigger, w/o success
	play {
		size = playb.size;
		size.do( { |index|
			playb[index].set(\gate, 1, \da, 0);
		});
	}

	// This function plays sounds sequentially, one after the other
	sequence {

		sequential = True;
		index = 0;
		playb[index].set(\loop, 0, \da, 2);
		playb.size.do{ |b|
			if( b>0,
				{
					this.free(b);
				}
			);
		};
		this.sequencemachine(index);
	}

	sequencemachine { |index = 0|
			playb[index].onFree {
			if ( (index+1) < buffers.size, {index = index + 1}, {index = 0} );
			playb.add (index -> Synth.new(\playbuf_mono_fs, [\buf, buffers[index], \numChannels, buffers[index].numChannels, \bufnum, buffers[index].bufnum, \loop, 0, \da, 2]) );
			postln("playing: " ++ playb[index]);
			this.printplayb;
			indexold = index;
			if (sequential == True,
				{this.sequencemachine(index)},
				{ // what to do if it is false
					playb[index].free;
			} );
		}
	}

	// This function plays sounds in parallel, all of them looping at the same time
	parallel {
		sequential = False;
		playb[indexold].onFree {

			poolsizeold = 0;
			this.retrievepool(poolsnd, poolsnd.size);
			this.printall;

		}
	}

	// This function stops the first sound of the class Dictionary collection play(1), otherwise it plays all

	stop {
		size = playb.size;
		size.do( { |index|
			playb[index].set(\amp, 0);
		});
	}

	solo { |targetnumsnd=1|
		playb[targetnumsnd-1].set(\amp, 1);
	}

	soloall { |targetnumsnd=1|
		playb.size.do( { |index|
			if (index == (targetnumsnd-1),
				{playb[index].set(\amp, 1)},
				{playb[index].set(\amp, 0)}
			);
		});
	}

	mute { |targetnumsnd=1|
		playb[targetnumsnd-1].set(\amp, 0);
	}

	muteall { |targetnumsnd=1|
		playb.size.do( { |index|
			if (index == (targetnumsnd-1),
				{playb[index].set(\amp, 0)},
				{playb[index].set(\amp, 1)}
			);
		});
	}

	// private function
	freeall {
		size = playb.size;
		size.do( { |index|
			playb[index].free;
			//buffers[index]?
			//poolsnd[index]?
		});
	}

	// private function
	free {|index|
		playb[index].free;
	}


	// This function retrieves all content-based descriptors listed in the Analysis Descriptor Documentation from the FreeSound API: "https://www.freesound.org/docs/api/analysis_docs.html#analysis-docs"
	// The result can be filtered using the descriptors request parameter passing a list of comma separated descriptor names chosen from the available descriptors e.g. 'descriptors=lowlevel.mfcc,rhythm.bpm'
	analyze {|descriptors, action|

		poolsnd.size.do( { |index|
			poolsnd[index].getAnalysis( descriptors, action, {|val|
            val.postln;
		}, true)
		});

	}

	// This function
	analyzepitch { |feature = "lowlevel.pitch" |

		poolsnd.size.do( { |index|
			poolsnd[index].getAnalysis( feature, {|val|
            val.lowlevel.pitch.mean.postln;
		}, true)
		});

	}

	// SINGLE SOUND SEARCH/PLAY METHODS (PRIVATE)

	//This function parses the Freesound information of each sound and converts it to the SuperCollider language, storing all the info in two dictionaries (buffers and Synths). The result is a sound that plays once is correctly stored in the playb dictionary.
	load { |dict, index|
		dict[index].retrievePreview("/tmp/", {
			buf = Buffer.readChannel(server, "/tmp/" ++ dict[index].previewFilename,
				channels: [0],
				action: { |buf|
					playb.add (index -> Synth.new(\playbuf_mono_fs, [\buf, buf, \numChannels, buf.numChannels, \bufnum, buf.bufnum, \loop, 1]) );
					buffers.add(index -> buf);
			});
		});
	}


	// MANAGING DICTIONARY OF SOUNDS METHODS (PRIVATE)

	// This function is in charge of the storage of a new group of sounds by managing the right index number when calling the function load() for each new sound from a dictionary of json info and resetting the counter.
	retrievepool { |dict, targetnumsnds = 1|

			if ( debugging == True, {
				postln("poolsizeold: " ++ poolsizeold);
			});

		targetnumsnds.do ({ |index|
			if ( debugging == True, {
				postln("index + poolsizeold: " ++ (index + poolsizeold) );
			});
			this.load(dict, (index + poolsizeold) );
		});
		poolsizeold = dict.size;
		counter = 0;
	}

	// VISUALIZATION

	//TODO: check this function, not working
	scope {
		/*window = Window.new("MIRLC scope", Rect(200, 200, 1200, 500));
		window.view.decorator = FlowLayout(window.view.bounds);
		viewoscil = Stethoscope.new(server, view:window.view);
		window.onClose = { viewoscil.free }; // don't forget this
		window.front;*/
		Stethoscope.new(server);
	}

	// PRINTING

	plotserver {
		server.plotTree;
	}

	printfspool {
		postln("The size of this dictionary is: " ++ poolsnd.size);
		poolsnd.size.do ({ |index|
			postln("[" ++ index ++ "]: " ++ "id: " ++ poolsnd[index].id ++ " name: " ++ poolsnd[index].name ++ " by: " ++ poolsnd[index].username ++ " dur: " ++ poolsnd[index].duration);
		});
	}

	printplayb {
		playb.size.do ({ |index|
			postln("[" ++ index ++ "]: " ++ playb[index]);
		});
	}

	printbuffers {
		buffers.size.do ({ |index|
			postln("[" ++ index ++ "]: " ++ buffers[index]);
		});
	}

	printall {
		postln("FS poolsnd dictionary: ");
		this.printfspool;
		postln("buffers buffers dictionary: ");
		this.printbuffers;
		postln("synths playb dictionary: ");
		this.printplayb;
	}


	// :::TO REVISE:::

	//deprecated?

	// RETRIEVAL OF A MULTIPLE SOUNDS (PUBLIC)

	// retrieval from one original sound
	// propagate (meth 2, # sounds = n)
/*	propagate { |meth2 = "random", size = 1|
		switch (meth2.postln,
			"random", { this.randomseed(size) },
			"similarity", { boolsimil = 1 }
		).postln;
	}*/

/*	pick { |size = 1, meth1 = "random", meth2 = "random", tag = "noise"|

		switch (meth1.postln,
			"random", { this.randomseed() },
			"query", { this.getsoundbyquery(tag, size)  }
		).postln;
		switch (meth2.postln,
			"random", { this.createpool(poolsnd, size) },
			"similarity", { boolsimil = 1 }
		).postln;
	}*/

/*	createpool2 { |dict, size=1|
		counter = size;

			size.do( { |index|
				"similar sound is..."++snd["name"].postln;
				//change method if required
				poolsnd[0].getSimilar( action: {
				|p|
				snd = p[index];
				//snd["name"].postln;
				dict.add(index + 1 -> snd);
				counter = counter - 1;//todo: counter--;
				counter.postln;
				if ( counter == 0, // need to wait until asynchronous call is ready!
				{ this.retrievepool(poolsnd, poolsnd.size);}
				);
				} );
		} );

	}*/


}

//todo: list all the descriptors e.g. analyze function
//todo: in getsound, if I replace poolsnd.size with index in line "if ( (poolsnd.size - poolsizeold) == size" it does not work / sound, why?
//todo: add an envelope for fade in & out (transitions) playbufmono sythdef
//todo: implement freeall: free sounds from playb, buffers, poolsnd
//todo: implement nowplaying: reports what sounds are playing e.g. sequence scenario
//todo: print authors names of audio clips
//todo: commute between 2 modes of play: sequence and parallel
//todo: add loop vs nonloop when playing the sound
//todo: transitions with envelopes, more methods on synth, should not apply when from parallel to sequence or the other way around
		//x = Synth(\foobar, [\bufnum, b.bufnum]);
		//x.set(\speed, 0);    // pause
		//x.set(\t_trig, 1);   // rewind
		//x.set(\speed, 1);    // play
//todo: rethink the all-purpose functions propagate, pick, createpool2
      // createpool2: merge with createpool and distinguish by method?
      // pick (meth1, #sound = 1)
      // pick().propagate() should be possible