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
		Freesound.token="5a837b803eb5a6da25dd3b42346fd6550080b919";// change it to own token

		//todo: add an envelope for fade in & out (transitions)
		SynthDef(\playbuf_mono_fs, {
			|bufnum, buf, amp = 1, out = 0, rate = 1, da = 0, loop = 1, trig = 0|
			var sig;
			sig = PlayBuf.ar(1, bufnum, BufRateScale.kr(buf) * rate,  doneAction: da, loop: loop, trigger: trig);
			sig = sig * amp;
			Out.ar(out, sig!2);
		}).add;

		//this.scope;

	}

	// SEARCH METHODS OF A SINGLE SOUND OR POOL OF SOUNDS (PUBLIC)

	// This function gets from one to multiple sounds (size) by random, and plays them
	getsoundbyrandom { |size = 1|

		// if ( debugging == True, {postln("Sounds selected by random: " ++ size);} );
		sndid = rrand (1, 369281); //todo: retrieve the highest # of FS sound dynamically
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
									if ( counter < size, { this.getsoundbyrandom(size); } );
								}
							);
						}
					);
				},
			{
				if ( debugging == True, {"SoundID does not exist".postln;} );
				this.getsoundbyrandom(size);
			} );
		} );
	}

	// This function gets from one to a multiple sounds (size) by one defined tag, and plays them
	getsoundbytag { |tagquery = "noise", size = 1|

		if ( debugging == True, {
			postln("Sounds selected by tag: " ++ size);
		});
		FSSound.textSearch( query: tagquery, filter: "type:wav", params: ('page': 2),
			action: { |p|
				size.do { |index|
					snd = p[index];
					postln("found sound by tag, id: " ++ snd["id"] ++ "name: " ++ snd["name"]);
					this.getsound(snd.id, 1); // so that each sound is loaded directly played
				}
		});
	}

	// This function gets from one to multiple similar sounds (number defined by size) from a target sound, usually the first sound from the dictionary
	getsoundbysimilarity { |numsnd = 1, size = 1|

		target = poolsnd[numsnd - 1];

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

	getsoundbycontent { |numsnd = 1, queryfilter = '.lowlevel.pitch_instantaneous_confidence.mean:[0.8 TO 1]', size = 1 |

		sndid = poolsnd[numsnd - 1].id;

		FSSound.contentSearch(
			target: sndid,
			filter: queryfilter,
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


	// RETRIEVAL OF A SINGLE SOUND OR POOL OF SOUNDS (PUBLIC)

	// This function gets either one sound or multiple sounds by ID and plays them
	// params: id, size
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
			}{ // size > 1
				if ( (poolsnd.size - poolsizeold) == size, // need to wait until asynchronous call is ready! once all sounds are added in the dictionary, they can be retrieved
					{
						this.retrievepool(poolsnd, size);
					}
				);
			}
		} );
	}


	// OPERATIONS ON LOADED SOUNDS (PUBLIC)

	// This function plays the first sound of the class Dictionary collection play(1), otherwise it plays all
	//TODO: make it play from the beginning, tried startpos, trigger, w/o success
	play {
		size = playb.size;
		size.do( { |index|
			playb[index].set(\amp, 1);
		});
	}

	// This function plays sounds sequentially, one after the other
	sequence {

		sequential = True;
		index = 0;

		this.solo(1);
		playb[0].set(\loop, 0, \da, 2);
		playb.size.do{ |b|
			if( b>0,
				{this.free(b)}
			);
		};
		//playb.add (index -> Synth.new(\playbuf_mono_fs, [\buf, buffers[0], \numChannels, buffers[0].numChannels, \bufnum, buffers[0].bufnum, \loop, 0, \da, 2]) );
		this.sequencemachine(index);

	}

	sequencemachine { |index = 0|
			playb[index].onFree {
			if ( (index+1) < buffers.size, {index = index + 1}, {index = 0} );
			playb.add (index -> Synth.new(\playbuf_mono_fs, [\buf, buffers[index], \numChannels, buffers[index].numChannels, \bufnum, buffers[index].bufnum, \loop, 0, \da, 2]) );
			indexold = index;
			if (sequential == True, {this.sequencemachine(index)} );
		}
	}

	// This function plays sounds in parallel, all of them looping at the same time
	parallel {
		sequential = False;
		playb[indexold].onFree {

			buffers.size.do{ |index|
				playb[index] = Synth.new(\playbuf_mono_fs, [\buf, buffers[index], \numChannels, buffers[index].numChannels, \bufnum, buffers[index].bufnum, \loop, 1]) ;
			};
		}
	}

		/*buffers.do{ |buf|
				playb[buf] = Synth.new(\playbuf_mono_fs, [\buf, buf, \numChannels, buf.numChannels, \bufnum, buf.bufnum, \loop, 1]) ;
			}*/

		/*playb[indexold].onFree {
			playb.size{|index|
				playb[index].freeall;
			}*/

			/*buffers.do{ |buf|
				playb[buf] = Synth.new(\playbuf_mono_fs, [\buf, buf, \numChannels, buf.numChannels, \bufnum, buf.bufnum, \loop, 1]) ;
			}
		}*/

		/*postln(indexold);
		playb.removeAt(indexold).postln;
		playb.do{|index|
			playb.removeAt(index).postln;
		}*/
		/*postln(index);
		this.freeall;
		playb.do{|index|
			playb.removeAt(index).postln;
		}*/
		//postln("playb.size " ++ playb.size);
		/*buffers.do{ |buf|
			playb.add (buf -> Synth.new(\playbuf_mono_fs, [\buf, buf, \numChannels, buf.numChannels, \bufnum, buf.bufnum, \loop, 1]) );
		}*/


	// This function stops the first sound of the class Dictionary collection play(1), otherwise it plays all
	stop {
		size = playb.size;
		size.do( { |index|
			playb[index].set(\amp, 0);
		});
	}

	solo { |numsnd=1|
		playb[numsnd-1].set(\amp, 1);
	}

	soloall { |numsnd=1|
		playb.size.do( { |index|
			if (index == (numsnd-1),
				{playb[index].set(\amp, 1)},
				{playb[index].set(\amp, 0)}
			);
		});
	}

	mute { |numsnd=1|
		playb[numsnd-1].set(\amp, 0);
	}

	muteall { |numsnd=1|
		playb.size.do( { |index|
			if (index == (numsnd-1),
				{playb[index].set(\amp, 0)},
				{playb[index].set(\amp, 1)}
			);
		});
	}

	freeall {
		size = playb.size;
		size.do( { |index|
			playb[index].free;
		});
	}

	free {|index|
		playb[index].free;
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
	retrievepool { |dict, numsnds = 1|

		postln("poolsizeold: " ++ poolsizeold);

		numsnds.do ({ |index|
			if ( debugging == True, {
				postln("index + poolsizeold: " ++ (index + poolsizeold) );
			});
			this.load(dict, (index + poolsizeold) );
		});
		poolsizeold = dict.size;
		counter = 0;
	}

	// This function is similar to getsound() but with more than one sound
	populatepool { |dict, size=1|
		size.do( { |index|
			this.getsoundbyrandom(index);
		} );
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

	printpool {
		postln("The size of this dictionary is: " ++ poolsnd.size);
		poolsnd.size.do ({ |index|
			postln("[" ++ index ++ "]: " ++ poolsnd[index].name ++ " by " ++ poolsnd[index].username);
		});
	}

	printplayb {
		playb.size.do ({ |index|
			postln("[" ++ index ++ "]: " ++ playb[index]);
		});
	}


	// :::TO REVISE:::


	// RETRIEVAL OF A MULTIPLE SOUNDS (PUBLIC)
	createpool { |size = 1|
		counter = size;
		if (size == 1) {
			// 1 unic so, crida funcio get ID via metode X, crida funcio carrega so ID
			this.getsoundbyrandom();
		}{
			size.do( { |index|
			//sndid = rrand (1, 369281); //todo: convert it to a getter/setter

			/*FSSound.getSound(sndid,
			{ |f|
				f["name"].postln;
				dict.add(index + 1 -> f);
				counter = counter - 1;//todo: counter--;
				counter.postln;
				if ( counter == 0, // need to wait until asynchronous call is ready!
					{ this.retrievepool(poolsnd, poolsnd.size);}
				);
			} );*/
		} );
		};
		//change method if required
	}


	// retrieval from one original sound
	// propagate (meth 2, # sounds = n)
	propagate { |meth2 = "random", size = 1|
		switch (meth2.postln,
			"random", { this.getsoundbyrandom(size) },
			"similarity", { boolsimil = 1 }
		).postln;
	}

	//deprecated
	/*pool { |size=1|
		this.createpool(poolsnd, size);
	}*/

		// This function plays one sound from the collection, by default the first sound of the class Dictionary is played
	// by default it loops, alternatively getsound().playsound() should be possible
	/*playsound {|numsnd = 1|
		this.retrievepool(poolsnd, numsnd);
	}*/


/*	//todo: add looping for size
	soundbytag { |tagquery = "tuning", size = 1|

		FSSound.textSearch( query: tagquery, filter: "type:wav", params:('page':2), action:{|p|
			snd = p[0]; // first result
			//snd.id.postln;
			//this.getsound(snd.id);
			poolsizeold = poolsnd.size;
			poolsnd.add(0 -> snd);
			this.retrievepool(poolsnd, poolsnd.size);
		});

	}*/

/*	getsoundbyquery { |tagquery = "noise", size = 1|

		FSSound.textSearch( query: tagquery, filter: "type:wav", params:('page':2),
			action:{ |p|
			snd = p[0]; // first result
			poolsizeold = poolsnd.size;
			poolsnd.add(0 -> snd);
			this.getsound(poolsnd[0].id);
				if (boolsimil==1, { this.createpool2(poolsnd, size-1) } );
		});
	}*/


	pick { |size = 1, meth1 = "random", meth2 = "random", tag = "noise"|

		switch (meth1.postln,
			"random", { this.getsoundbyrandom() },
			"query", { this.getsoundbyquery(tag, size)  }
		).postln;
		switch (meth2.postln,
			"random", { this.createpool(poolsnd, size) },
			"similarity", { boolsimil = 1 }
		).postln;
	}

/*	// sounds stored in dict, method of selection: random / query
	// dict: where do we store sounds, size: how many sounds do we store
	// once all sounds are stored (is it possible to store them differently?),
	// we load them with retrievepool
	createpool { |dict, size=1|
		"Pool function, looping!".postln;

/*		counter = size;
		rndnums = Array.new(size);
		while (counter > 0,
			sndid = rrand (1, 369281);
			FSSound.getSound(sndid, {|f|
				sound = f;
				if ( sound.detail == "Not found.",
					{},
					{rndnums.add(sndid); counter = counter - 1}
				);
			});
		);*/

		counter = size;
		//change method if required
		size.do( { |index|
			sndid = rrand (1, 369281); //todo: convert it to a getter/setter
			FSSound.getSound(sndid,
			{ |f|
				f["name"].postln;
				dict.add(index + 1 -> f);
				counter = counter - 1;//todo: counter--;
				counter.postln;
				if ( counter == 0, // need to wait until asynchronous call is ready!
					{ this.retrievepool(poolsnd, poolsnd.size);}
				);
			} );
		} );
	}*/

	//todo: merge with createpool and distinguish by method
	createpool2 { |dict, size=1|
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

	}

/*	addsoundstopool { |dict, size=1, id|
		FSSound.getSound(id,
		{ |f|
			dict.add(index -> f);
		} );
	}

*/

	//TBDeveloped
	/*sequence { |dict|
		dict.size.do ({ |index|
            this.load(dict[index], dict.size);
		});
	}*/


}

//todo: output stereo (from mono)
//todo: print authors names of audio clips
//todo: commute between 2 modes of play: sequence and parallel
//todo: add loop vs nonloop when playing the sound
//todo: transitions with envelopes, more methods on synth
		//x = Synth(\foobar, [\bufnum, b.bufnum]);
		//x.set(\speed, 0);    // pause
		//x.set(\t_trig, 1);   // rewind
		//x.set(\speed, 1);    // play
// pick (meth1, #sound = 1)
// pick().propagate() should be possible