
MIRLC
===
SuperCollider extensions for using MIR techniques in live coding.

(c) 2016-2020 by Anna Xambó (<anna.xambo@dmu.ac.uk>).


Introduction
----

This set of SC classes is designed to provide a high-level approach to using MIR techniques in live coding. This system has been tested with SuperCollider 3.8, 3.9 and 3.10.


Application Start
----

Drag the MIRLC files (`MIRLCRep.sc`, `MIRLCRew.sc`) to the Extensions folder of SuperCollider (suggested to create a subfolder with the same name): `/Users/{username}/Library/Application Support/SuperCollider/Extensions` (in Mac)

Either recompile the class library (`Language>Recompile Class Library`) or restart SuperCollider.

MIRLCRep Module
----

The MIRLCRep class is designed for repurposing audio samples from Freesound.org using and expanding the Freesound quark for SuperCollider.

### Requirements

* Make sure you have an Internet connection.
* Make sure you have a [Freesound.org](http://freesound.org) account.
* Make sure to obtain an [API key](https://freesound.org/help/developers/).
* Install [Freesound quark](https://github.com/g-roma/Freesound.sc), which is a SuperCollider client for accessing the Freesound API and operate with sounds from Freesound.org.
* In order to connect with Freesound.org, the type of authentication used in MIRLC is Token. Make sure to introduce your API key in the class `MIRLCRep.sc` and recompile the class library.

### Additions to the Freesound quark

* Asynchronous management of multiple sounds by a single query.
* User-friendlier queries by content, similarity, tag, filter, and sound id.
* A new user-friendly query by random.
* A new architecture of groups of sounds with user-friendly functions for playing them in sequence and in parallel, which are managed asynchronously.
* A new set of functions to control both individual sounds and group of sounds (e.g., play, stop, mute single sounds, solo single sounds).
* Retrieval of sounds avoiding repetition in queries by content and similarity.
* Retrieval of sounds avoiding inexistent results in random queries.
* A customizable text file that prints the sounds used by title and username.

### Code Examples

* [Code demo](script_demo_MIRLCRep.md).
* [Code demo Noiselets (old version of the code)](script_demo_Noiselets17.md).

### Live Performance Examples

* [Video demo](https://vimeo.com/249968326).
* [Live coding with crowdsourced sounds & a drum machine @ International Conference of Live Coding 2019](http://annaxambo.me/music/solo-performances/live-coding-iclc-2019/). International Conference on Live Coding 2019, Closing Concert at the Public School of Music and Dance María Dolores Pradera, Madrid, Spain. Here is the [script](script_performance_ICLC19.scd) and the [sound credit list](sound_credits_ICLC19.md).
* [Live coding @ The RAW and The COOKED, Inter/sections 2018](https://annaxambo.me/music/solo-performances/live-coding-the-raw-2018/). The Raw, Inter/sections 2018. Café 1001. London, UK. September 28, 2018. Here is the [script](script_performance_TheRAW18.scd) and the [sound credit list](sound_credits_TheRAW18.md).
* [Live coding @ Noiselets 2017](https:///music/solo-performances/live-coding-noiselets-2017/). Noiselets – a noise music microfestival. Freedonia, Barcelona, Spain. January 8, 2017. Here is the [script](script_performance_Noiselets17.scd) and the [sound credit list](sound_credits_Noiselets17.md).
* [Noiselets 2017 rehearsal](https://soundcloud.com/petermann-plays/noiselets-2017-liveset-rehearsal). Here is the [sound credit list](sound_credits_rehearsal_Noiselets17.md).

### Music Album Examples

* Anna Xambó. [H2RI](http://www.panyrosasdiscos.net/pyr247-anna-xambo-h2ri/) (2018). Chicago, IL, USA: pan y rosas.

MIRLCRew Module
----

The MIRLCRew class is designed for rewiring an audio in signal as both a control signal or audio signal using MIR techniques in SuperCollider.

### Requirements

* Make sure you have either a microphone connected or an audio file.
* Input audio files work in mono.

### Code Examples

* [Code demo](script_demo_MIRLCRew.md).

### Live Performance Examples

* [Video demo](https://vimeo.com/249997271).
* [Beckon @ NIME 2018](http://annaxambo.me/music/group-performances/beckon-nime-2018/). Moss Arts Center: Anne and Ellen Fife Theatre, Blacksburg, VA (USA). June 4, 2018. Here is the [sound credit list](https://github.com/axambo/beacon/blob/master/NIME18-18.06/sound_credits_NIME18.md).
* [Beacon @ TEI 2018](http://annaxambo.me/music/group-performances/beacon-tei-2018/). Kulturhuset. Stockholm, Sweden. March 20, 2018. Here is the [sound credit list](https://github.com/axambo/beacon/blob/master/TEI18-18.03/sound_credits_TEI18.md).
* [Beacon @ NIME 2017](http://annaxambo.me/music/group-performances/beacon-nime-2017/). NIME 2017. Stengade. Copenhagen, Denmark. May 16, 2017.  Here is the [sound credit list](https://github.com/axambo/beacon/blob/master/RSF17-17.02/sound_credits_RSF17.md).
* [Beacon @ Root Signals Festival 2017](http://annaxambo.me/music/group-performances/beacon-root-signals-festival-2017/). Georgia Southern University. Statesboro, Georgia, United States. February 11, 2017. Here is the [sound credit list](https://github.com/axambo/beacon/blob/master/RSF17-17.02/sound_credits_RSF17.md).

### Music Album Examples

* Anna Weisling and Anna Xambó. [Beacon [EP]](https://carpal-tunnel.bandcamp.com/album/beacon) (2019). Barcelona: Carpal Tunnel.


MIRLCRex Module
----

The MIRLCRex class is designed for supporting the remix of multiple audio streams using MIR techniques in SuperCollider.

### Requirements

* Create a path to the audio streams. The code is based on four audio streams.
* Input audio files work in mono.

### Code Examples

* [Code demo](script_demo_MIRLCRex.md).

### Live Performance Examples

* [Video demo](https://vimeo.com/249997569).


License
----

The MIT License (MIT).
