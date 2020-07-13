```
// instantiation
a = MIRLCRex.new 

// original audio streams
a.original // the pure audio streams and visualization

// effects

a.mix // automatic mix based on the amplitudes of the streams

a.equalize // automatic equalization based on the spectral centroids of the streams

a.spatialize // automatic spatialization (panning) of the streams with transition

a.spatialize(0) // automatic reset of spatialization (panning) of the streams with transition

a.spatialize(1) // automatic spatialization (panning) of the streams with transition

// finish
cmd + period

```
