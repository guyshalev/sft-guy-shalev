# Overview #

The core of our project is the implementation of the SFT algorithm for learning a function f:Zn->C, namely a function from a cyclic group into C, and applying it in the special case where the function is given as a WAV File.

# Details #
The SFT algorithm, which is fully described here is a probabilistic algorithm for finding significant Fourier coefficients in the Fourier transform of a function f:Zn->C.

A WAV file, arguably the simplest format for storing audio data, is organized as an ordered set of frames, each can be described by a real number between -1 and 1. Hence, every WAV file can be viewed as a function f, from the set of natural numbers to the real numbers, wherein f(i)=the value of the i'th frame in the file.