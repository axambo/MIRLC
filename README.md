
MIRLC
===
SuperCollider extensions for using MIR techniques in live coding.

(c) 2016-2018 by Anna Xambó (<a.xambo@qmul.ac.uk>).


Introduction
----

This set of SC classes is designed to provide a high-level approach to using MIR techniques in live coding. This system has been tested with SuperCollider 3.8 and SuperCollider 3.9.


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
* Noiselets 2017 live (coming soon), January 8, 2017 at Freedonia, Barcelona, Spain. Here is the [sound credit list](sound_credits_Noiselets17.md).
* [Noiselets 2017 rehearsal](https://soundcloud.com/petermann-plays/noiselets-2017-liveset-rehearsal). Here is the [sound credit list](sound_credits_rehearsal_Noiselets17.md).

MIRLCRew Module
----

The MIRLCRew class is designed for rewiring an audio in signal as both a control signal or audio signal using MIR techniques in SuperCollider.

### Requirements

* Make sure you have either a microphone connected or audio files.

### Code Examples

* [Code demo](script_demo_MIRLCRew.md).


Version History
----

The code used in the concert of Noiselets 2017 corresponds to the following commit hash to GitHub: [https://github.com/axambo/MIRLC/commit/3d0d27a0d1098b34694d7ef29439dac7443d5167](https://github.com/axambo/MIRLC/commit/3d0d27a0d1098b34694d7ef29439dac7443d5167)


License
----

The MIT License (MIT).
