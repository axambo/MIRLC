// @new users, control the following customizable vars:
// - Freesound.token = "<your_api_key>"
// - path: directory to store downloaded sounds and record the text file with the credits, or change it to "/tmp/"
// - debugging: True/False


MIRLCRep {

    classvar <>server;
    classvar <>file;
    classvar <>date;
    classvar <>debugging;
    var metadata, buffers, synths, <translation;
    var snd, preview, buf, target, sequential;
    var poolsizeold, index, indexold, keys, counter, sndid, numsnds, size, rndnums;
    var window, viewoscil;
    var backendClass;
    var databaseSize;
    var directoryPath;
	  var maxvol = 0.1; // audio samples

    *new {|backend = 0, dbSize = 394004, path = "~/Music/MIRLC/"|
        ^super.new.init(backend, dbSize, path)
    }

    init {|backend, dbSize, path|
        server = Server.local;
        server.boot;
        metadata = Dictionary.new;
        buffers = Dictionary.new;
        synths = Dictionary.new;
        translation = Dictionary.new;
        debugging = True;
        poolsizeold = 0;
        counter = 0;
        indexold = 0;
        sequential = False;
        databaseSize = dbSize;
        directoryPath = path;

        if(backend == 0){
            backendClass = FSSound;
            Freesound.authType = "token"; // default, only needed if you changed it
            Freesound.token="<your_api_key>"; // change it to own API key token
        }{
            backendClass = FLSound;
        };

        date = Date.getDate;

        file = File(path.standardizePath ++ date.stamp ++ "_credits" ++ ".txt","w");
        file.write("Sound samples used:\n");

        SynthDef(\synthsuf_mono_fs, {
            |bufnum, buf, amp = 0.1, out = 0, rate = 1, da = 0, loop = 1, trig = 0, gate = 1|
            var sig, env;
            sig = PlayBuf.ar(1, bufnum, BufRateScale.kr(buf) * rate,  doneAction: da, loop: loop, trigger: trig);
            //env = EnvGen.kr(Env.asr(20.0,2.0,10.0, 'sine'), gate, doneAction: 2);
            sig = sig * amp;
            Out.ar(out, sig!2);
        }).add;

        this.argstranslate;
        //this.plotserver; //TODO: revise these functions and perhaps move them to a more meaningful place, it should only print once, it should be a classvar
        //this.scope;

    } //--//


    //---------------------------------------------------//
    //SOUND GROUP MANAGEMENT (PRIVATE FUNCTIONS)
    //---------------------------------------------------//
    // FUNCTIONS: load, loadmetadata


    //------------------//
    // RETRIEVE SOUNDS
    //------------------//
    // This function manages the dictionary metadata (sounds with fs info) and loads the new sounds
    // This function is in charge of the storage of a new group of sounds by managing the right index number when calling the function load() for each new sound from a dictionary of json info and resetting the counter.
    loadmetadata { |totalsnds = 1|
        totalsnds.do ({ |index|
            this.loadsounds(metadata, (index + poolsizeold) );
        });
        poolsizeold = metadata.size;
        counter = 0; // used in random
        this.printmetadata;
    }

    //------------------//
    // LOAD SOUNDS
    //------------------//
    // This function parses the Freesound information of each sound and converts it to the SuperCollider language, storing all the info in two dictionaries (buffers and Synths). The result is a sound that plays once is correctly stored in the synths dictionary.
    loadsounds { |dict, index|
        dict[index].retrievePreview(directoryPath, {
            buf = Buffer.readChannel(server, directoryPath ++ dict[index].previewFilename,
                channels: [0],
                action: { |buf|
                    if (sequential == False,
                        { synths.add (index -> Synth.new(\synthsuf_mono_fs, [\buf, buf, \numChannels, buf.numChannels, \bufnum, buf.bufnum, \loop, 1, \amp, maxvol]) );
                        },
                        {
                            // do nothing if in sequential mode
                    });
                    buffers.add(index -> buf);
            });
        });
    }

    //---------------------------------------------------//
    //QUERIES TO SEED A POOL OF SOUNDS (TEXT, CONTENT)
    //---------------------------------------------------//
    // FUNCTIONS: random, tag, content


    //------------------//
    // GET SOUND BY ID
    //------------------//
    // This function can be used as a standalone public function to get [1..n] sounds by ID, and it is also used as a private function by random, tag, similar, filter, content to get sounds
    // params: id, size
    id { |id = 31362, size = 1|

        backendClass.getSound(id,
            { |f|
                //available metadata: "id","url","name","tags","description","geotag","created","license","type","channels","filesize""bitrate","bitdepth","duration","samplerate","username","Jovica","pack","pack_name","download","bookmark","previews","preview-lq-mp3","preview-hq-ogg","preview-hq-mp3","images","num_downloads","avg_rating","num_ratings","rate":,"comments","num_comments","comment","similar_sounds","analysis","analysis_frames","analysis_stats"
                snd = f;
                index = metadata.size;
                file.write(snd["name"] + " by " + snd["username"] + snd["url"] + "\n");

                metadata.add(index -> f);

                if (size == 1) {
                    this.loadmetadata(size);
                    //this.printmetadata;
                }{ // size > 1
                    if ( (metadata.size - poolsizeold) == size, // need to wait until asynchronous call is ready! once all sounds are added in the dictionary, they can be retrieved
                        {
                            this.loadmetadata(size);
                            //this.printmetadata;
                        }
                    );
                }
        } );
    } //--//

    //------------------//
    // QUERY BY RANDOM
    //------------------//
    // This function gets [1..n] sounds by random, and plays them
    random { |size = 1|

        // if ( debugging == True, {postln("Sounds selected by random: " ++ size);} );
        sndid = rrand (1, databaseSize); //todo: retrieve the highest # of FS sound dynamically
        backendClass.getSound ( sndid,
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
                            {
                                this.id(sndid, size);
                            },
                            {//size > 1
                                //this.id(sndid, size);
                                postln("group size is greater than 1");
                                postln("( counter - size ): " ++ ( counter - size ));
                                if ( counter <= size ,
                                    //if ( (metadata.size - poolsizeold - size) < 0 ,
                                    {
                                        this.id(sndid, size);
                                        if ( counter < size, { this.random(size); } );
                                    }
                                );
                            }
                        );
                    },
                    {
                        if ( debugging == True, {"SoundID does not exist".postln;} );
                        this.random(size);
                } );
        } );
    } //--//

    //------------------//
    // QUERY BY TAG
    //------------------//
    // This function gets [1..n] sounds by one defined tag, and plays them
    tag { |tag = "noise", size = 1|

        if ( debugging == True, {
            postln("Sounds selected by tag: " ++ size);
        });
        backendClass.textSearch( query: tag, params: ('page': 1),
            action: { |p|
                size.do { |index|
                    snd = p[index];
                    postln("found sound by tag, id: " ++ snd["id"] ++ "name: " ++ snd["name"]);
                    while (
                    {this.sndidexist(snd.id) == 1},
                    {
                      postln ("repeated sound, getting another sound...");
                      index = index + 1 + size;
                      snd = p[index];
                    });
                    this.id(snd.id, 1); // so that each sound is loaded & directly played
                }
        });
    } //--//


    //------------------//
    // QUERY BY CONTENT
    //------------------//
    // This function gets [1..n] sounds by one defined feature and fx, and plays them
    content { |size = 1, feature = 'dur', fvalue = 1, fx = 'conf', fxvalue = 'bypass' |
        var fconcat, fxconcat;
        if (feature != 'id',
          { fconcat = this.gettranslation(feature.asSymbol)++fvalue; },
          { fconcat = fvalue });
        fxconcat = this.gettranslation(fx.asSymbol) ++ this.gettranslation(fxvalue);
        backendClass.contentSearch(
            target: fconcat,
            filter: fxconcat,
            params: ('page':1),
            action: {|p|
                size.do { |index|
                    snd = p[index];
                    //check if snd.id already exists, if so, take next sound
                    if (metadata.size > 0,
                        {
                            while ( {this.sndidexist(snd.id) == 1},
                                {
                                    index = index + size;
                                    snd = p[index];
                                    postln ("repeated sound, getting another sound...");
                            });
                    });
                    this.id(snd.id, 1);  // so that each sound is loaded directly played;
                }
            }
        );
    } //--//


    //---------------------------------------------------//
    // QUERIES TO CONTINUE ADDING SOUNDS (QUERY BY EXAMPLE)
    //---------------------------------------------------//
    // FUNCTIONS: similar, filter

    //------------------//
    // SIMILAR SOUNDS
    //------------------//
    // This function gets [1..n] similar sounds from a target sound, usually the first sound from the dictionary
    similar { | targetnumsnd = 0, size = 1 |

        target = metadata[targetnumsnd];  // before: metadata[targetnumsnd - 1];

        target.getSimilar(
            action: { |p|
                size.do { |index|
                    snd = p[index+1]; // to avoid retrieving the same sound of the query
                    //check if snd.id already exists, if so, take next sound
                    if (metadata.size > 0,
                        {
                            while ( {this.sndidexist(snd.id) == 1},
                                {
                                    index = index + 1 + size;
                                    snd = p[index];
                                    postln ("repeated sound, getting another sound...");
                            });
                    });
                    this.id(snd.id, 1); // so that each sound is loaded directly played
                }
        });

    } //--//

    //------------------//
    // SIMILAR BY RANGE
    //------------------//
    // This function gets [1..n] similar sounds from a target sound filtered by a fx
    filter { |targetnumsnd = 0, size = 1, fx = 'conf', fxvalue = 'bypass' |

        var  fxconcat;
        fxconcat = this.gettranslation(fx.asSymbol) ++ this.gettranslation(fxvalue);

        sndid = metadata[targetnumsnd].id; // before: metadata[targetnumsnd - 1].id

        backendClass.contentSearch(
            target: sndid,
            filter: fxconcat,
            params: ('page':1),
            action: {|p|
                size.do { |index|
                    snd = p[index];
                    //snd.name.postln;
                    //check if snd.id already exists, if so, take next sound
                    if (metadata.size > 0,
                        {
                            while ( {this.sndidexist(snd.id) == 1},
                                {
                                    index = index + size;
                                    snd = p[index];
                                    postln ("repeated sound, getting another sound...");
                            });
                    });
                    this.id(snd.id, 1); // so that each sound is loaded directly played
                }
            }
        );
    } //--//

    //---------------------------------------------------//
    // ANALYZING SOUNDS
    //---------------------------------------------------//
    // FUNCTIONS:

    //------------------//
    // ANALYZE
    //------------------//
    // This function retrieves all content-based descriptors listed in the Analysis Descriptor Documentation from the FreeSound API: "https://www.freesound.org/docs/api/analysis_docs.html#analysis-docs"
    // The result can be filtered using the descriptors request parameter passing a list of comma separated descriptor names chosen from the available descriptors e.g. 'descriptors=lowlevel.mfcc,rhythm.bpm'
    analyze {|descriptors, action|

        metadata.size.do( { |index|
            metadata[index].getAnalysis( descriptors, action, {|val|
                val.postln;
            }, true)
        });

    }//--//

    //------------------//
    // WHAT PITCH
    //------------------//
    whatpitch { |feature = "lowlevel.pitch.mean" |
        this.analyze(feature);
    }//--//

    //------------------//
    // WHAT KEY
    //------------------//
    whatkey { |feature = "tonal.key_key" |
        this.analyze(feature);
    }//--//

    //------------------//
    // WHAT BPM
    //------------------//
    whatbpm { |feature = "rhythm.bpm" |
        this.analyze(feature);
    }//--//

    //------------------//
    // WHAT DURATION (sec)
    //------------------//
    whatdur { |feature = "sfx.duration" |
        this.analyze(feature);
    }//--//


    //---------------------------------------------------//
    // FUNCTIONS FOR LIVE CODING WITH THE SOUNDS
    //---------------------------------------------------//
    // FUNCTIONS: play, sequence, sequencemachine (private), parallel, stop, solo, solo all, mute, mute all, free, free all

    //------------------//
    // PLAY
    //------------------//
    // This function plays the first sound of the class Dictionary collection play(1), otherwise it plays all
    play {
        size = synths.size;
        size.do( { |index|
            synths[index].set(\amp, maxvol);
            this.printsynth(index);
        });
    } //--//

    //------------------//
    // SEQUENCE
    //------------------//
    // This function plays sounds sequentially, one after the other
    sequence {
        "--- sequence mode".postln;
		if ( sequential == False,
			{
				"STATE 3: from parallel to sequence".postln; sequential = True;
				sequential.postln;
				"change behavior from PARALLEL to SEQUENCE".postln;
				// removing all the sounds except for the first
				 index = 0;
                synths.size.do{ |b|
                    if( b>0,
                        {
                            this.free(b);
                        }
                    );
                };
                synths[index].set(\loop, 0, \da, 2);
                this.printsynth(index);
                synths[index].onFree {
					if ( sequential == True, {
						"--- sequencemachine mode".postln;
						this.sequencemachine(index);
					});
                };
				//
		}, {
				"STATE 4: from sequence to sequence".postln; sequential.postln;
				//sequential = True;
				"keep behavior SEQUENCE".postln;
		});
    } //--//

    //------------------//
    // SEQUENCE MACHINE (PRIVATE)
    //------------------//
    // This function is private and makes sure to play sounds sequentially
    sequencemachine { |index = 0|

		if ( (index+1) < buffers.size, {index = index + 1}, {index = 0} );
		"index value in sequence machine: ".postln;
		index.postln;
        synths.add (index -> Synth.new(\synthsuf_mono_fs, [\buf, buffers[index], \numChannels, buffers[index].numChannels, \bufnum, buffers[index].bufnum, \loop, 0, \da, 2, \amp, maxvol]) );
        this.printsynth(index);
        //indexold = index;
        synths[index].onFree {
            if (sequential == True,
            {
			"inside recursion".postln;
            this.sequencemachine(index);
            } );
        };
    } //--//

    //------------------//
    // PARALLEL
    //------------------//
    // This function plays sounds in parallel, all of them looping at the same time. If it comes from sequential, it will start once the sound that is playing in the sequential state ends.
    parallel {
        "--- parallel mode".postln;

			if ( sequential == True,
			{
				"STATE 1: from sequence to parallel".postln; sequential = False;
				sequential.postln;
        "change behavior from SEQUENCE to PARALLEL".postln;
        this.parallelmachine;
		}, {
				"STATE 2: from parallel to parallel".postln; sequential.postln;
				//sequential == False;
				"keep behavior PARALLEL". postln;
		});


    }

	//------------------//
    // PARALLEL MACHINE (PRIVATE)
    //------------------//
    // This function is private and makes sure to play sounds in parallel
    parallelmachine {

        size = buffers.size;
        size.do( { |index|
            synths.add (index -> Synth.new(\synthsuf_mono_fs, [\buf, buffers[index], \numChannels, buffers[index].numChannels, \bufnum, buffers[index].bufnum, \loop, 1, \da, 0, \amp, maxvol]) );
        });
        this.printsynths;
		this.play;

    } //--//

    //------------------//
    // VOLUME
    //------------------//
    // This function changes the volume of the whole group from 0 to 1
    volume {|vol = 0.5|
        size = synths.size;
        size.do( { |index|
            synths[index].set(\amp, vol);
        });
    } //--//

    //------------------//
    // STOP
    //------------------//
    // This function stops the first sound of the class Dictionary collection play(1), otherwise it plays all
    stop {
        size = synths.size;
        size.do( { |index|
            synths[index].set(\amp, 0);
        });
    } //--//

    //------------------//
    // FADE OUT
    //------------------//
	// This function fades out all synths with a smooth fade out
	// TODO: the workflow is unclear when sounds from a group are faded out, and new sounds are added to the group
    fadeout {
		sequential = False; // to avoid inconsistencies
		synths.size.postln;
		synths.size.do( { |index|
			synths[index].set(\gate, 0);
		});
    } //--//

    //------------------//
    // SOLO
    //------------------//
    // This function..
	  solo { |targetnumsnd=0|
        synths.size.do( { |index|
            if (index == (targetnumsnd), // before: (index == (targetnumsnd-1)
                {synths[index].set(\amp, maxvol)},
                {synths[index].set(\amp, 0)}
            );
        });
    } //--//

    //------------------//
    // MUTE
    //------------------//
    // This function..
    mute { |targetnumsnd=0|
        synths[targetnumsnd].set(\amp, 0); // before: synths[targetnumsnd-1].set(\amp, 0);
    } //--//

    //------------------//
    // MUTE ALL
    //------------------//
    // This function..
	//TODO: solve it when sequential = true
    muteall { |targetnumsnd=0|
        synths.size.do( { |index|
            synths[index].set(\amp, 0);
        });
    } //--//


    //------------------//
    // FREE ALL
    //------------------//
    // This function...
    // private function
    //TODO: implement freeall: free sounds from synths, buffers, metadata
    freeall {
      synths.size.do( { |index|
        synths[index].free;
      });
    } //--//

    // private function
    free {|index|
        synths[index].free;
    }

    //---------------------------------------------------//
    // UTILS
    //---------------------------------------------------//
    // FUNCTIONS: sndexist, argstranslate, cmdperiod

    //------------------//
    // DOES A SOUND EXIST
    //------------------//
    // This function returns whether the sound is already in the metadata dictionary or not
    sndidexist { |id|
        var index;
        var mdsize = metadata.size;

        block( { |break|
            mdsize.do( { |index|
                //postln(metadata[0].id == id);
                //postln(index);
                if ( metadata[index].id == id,
                    { ^1 }
                );
            });
            {^0};
        });
    } //--//

    //------------------//
    // TRANSLATE TO FS ARGS
    //------------------//
    // This function maps from shorter arguments to the ones expected by the FreeSound quark
    argstranslate {
        //Features
        translation.add(\pitch -> ".lowlevel.pitch.mean:");
        translation.add(\dur -> ".sfx.duration:");
        translation.add(\dissonance -> ".lowlevel.dissonance.mean:");
        translation.add(\bpm -> ".rhythm.bpm:");
        //Filters
        translation.add(\key -> "tonal.key_key:");
        translation.add(\scale -> "tonal.key_scale:");
        translation.add(\conf -> ".lowlevel.pitch_instantaneous_confidence.mean:");
        translation.add(\mfcc0 -> "lowlevel.mfcc.mean[0]:");
        translation.add(\mfcc1 -> "lowlevel.mfcc.mean[1]:");
        translation.add(\mfcc4 -> "lowlevel.mfcc.mean[1]:");
        //Filter values
        translation.add(\Asharp-> "\"ASharp\"");
        translation.add(\A-> "\"A\"");
        translation.add(\B-> "\"B\"");
        translation.add(\C-> "\"C\"");
        translation.add(\D-> "\"D\"");
        translation.add(\E-> "\"E\"");
        translation.add(\F-> "\"F\"");
        translation.add(\G-> "\"G\"");
        translation.add(\major -> "\"major\"".asString);
        translation.add(\minor -> "\"minor\"".asString);
        translation.add(\hi -> "[0.8 TO 1]");
        translation.add(\lo -> "[0 TO 0.2]");
        translation.add(\bypass -> "[0 TO 1]");
        translation.add(\1720 -> "[17 TO 20]");
        translation.add(\2040 -> "[20 TO 40]");
        translation.add(\neg -> "[-1124 TO -1121]");
        //translation.add(\diss -> "lowlevel.dissonance.mean:"); // 0 -> consonant, 1-> dissonant [0.3-0.5]
        //translation.add(\voiced -> "lowlevel.spectral_entropy.mean:"); // [2-10]

    } //--//


    //------------------//
    // GET TRANSLATION
    //------------------//
    // This function translates a parameter only if it exists in the dictionary translation

    gettranslation{|key|
    		if (translation.includesKey(key)){
    			^translation[key];
    		}{
    			^key;
    		}

    	}

    //------------------//
    // CMD PERIOD
    //------------------//
    // This function is activated when stopping the code / recompiling / etc.
    cmdPeriod {
        file.close;
        currentEnvironment.clear;
    } //--//


    //---------------------------------------------------//
    // VISUALIZATION, PRINTING
    //---------------------------------------------------//
    // FUNCTIONS: scope, plotserver, printmedata, printsynths, printbuffers, printall

    //------------------//
    // SCOPE
    //------------------//
    // This function...
    //TODO: check this function, not working
    scope {
        /*window = Window.new("MIRLC scope", Rect(200, 200, 1200, 500));
        window.view.decorator = FlowLayout(window.view.bounds);
        viewoscil = Stethoscope.new(server, view:window.view);
        window.onClose = { viewoscil.free }; // don't forget this
        window.front;*/
        Stethoscope.new(server);
    } //--//

    //------------------//
    // PLOT SERVER
    //------------------//
    // This function...
    plotserver {
        server.plotTree;
    } //--//

    //------------------//
    // PRINT METADATA
    //------------------//
    // This function prints the FS metadata information for all downloaded sounds
    printmetadata {
        metadata.size.do ({ |index|
            postln("[" ++ index ++ "]: " ++ "id: " ++ metadata[index].id ++ " name: " ++ metadata[index].name ++ " by: " ++ metadata[index].username ++ " dur: " ++ metadata[index].duration);
        });
    } //--//

    //------------------//
    // PRINT BUFFERS
    //------------------//
    // This function prints the buffers information and associated FS metadata information for all downloaded sounds
    printbuffers {
        buffers.size.do ({ |index|
            postln("[" ++ index ++ "]: " ++ buffers[index] ++ "id: " ++ metadata[index].id ++ " name: " ++ metadata[index].name ++ " by: " ++ metadata[index].username);
        });
    } //--//

    //------------------//
    // PRINT SYNTHS
    //------------------//
    // This function prints the synths information and associated FS metadata information for all the active sounds
    printsynths {
        synths.size.do ({ |index|
            //postln("[" ++ index ++ "]: " ++ synths[index] ++ "id: " ++ metadata[index].id ++ " name: " ++ metadata[index].name ++ " by: " ++ metadata[index].username );
            postln("now playing..." ++ "[" ++ index ++ "]: " ++ "id: " ++ metadata[index].id ++ " name: " ++ metadata[index].name ++ " by: " ++ metadata[index].username ++ " dur: " ++ metadata[index].duration ++ $\n ++ synths[index] );
        });
    } //--//

    //------------------//
    // PRINT SYNTH
    //------------------//
    // This function prints the synth information and associated FS metadata information of the current active sound
    printsynth { |index|
        postln("now playing..." ++ "[" ++ index ++ "]: " ++ "id: " ++ metadata[index].id ++ " name: " ++ metadata[index].name ++ " by: " ++ metadata[index].username ++ " dur: " ++ metadata[index].duration ++ $\n ++ synths[index] );
    } //--//

    //------------------//
    // PRINT ALL (METADATA, BUFFERS, SYNTHS)
    //------------------//
    // This function...
    printall {
        postln("FS metadata dictionary: ");
        this.printmetadata;
        postln("buffers dictionary: ");
        this.printbuffers;
        postln("synths dictionary: ");
        this.printsynths;
    } //--//


}
