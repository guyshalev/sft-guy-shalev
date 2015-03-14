# Introduction #

The galleries consist of many pictures and audio files, that are sometimes difficult to understand without explanation, so we decided to add this guided tour through the gallery. We hope that after the tour, you'll have a better understanding of our working process and the final product.


# **Photo Gallery** #

All photos in the gallery are screenshots of graphs created by our "drawing class", Graphing-Data.
The gallery is divided into three parts -

  * **A** - Files we opened before starting the project, to set goals.
  * **B** - The development step of the PEAKS algorithm and its behaviour on various files.
  * **C** - Comparison between input and output segments, after running the PEAKS algorithm with one of the FT functions.

# A #
In the first part of the gallery, we can see 6 photos. (in this part, red dots should be ignored).
Three of them (A, I, U) represent segments of different sizes, taken from the audio files matching their names. The fourth photo, Chord, represents two (harmonic) piano notes played simultaneously. The other two are segments from a song with a few instruments playing together.


From these graphs we've concluded that periodicity _can_ be found in audio files, though it might be complicated, for several reasons:
  * The recurring period is not identical.
  * The amplitude might change throughout the file (as in A).
  * In some files, the period is very unclear due to many instruments.
Therefore, we decided to set our goals to work with "real-world" input, but still simpler than multi-instrumental music.

# B #
In the second (and main) part of the gallery, we see the results of the PEAKS algorithm for finding periodicity. Red dots mark significant extreme points (explained in algorithm), and blue dots are spread every N frames, N being the output length of the periodicity search algorithm.
  * **IMax** - from the file I. From this file, we learnt it might be better to take into account only _positive_ maximum points.
  * **UPeriod** - from the file U. The first success of the algorithm! leap size is clearly 2 - there are 2 red dots in every period.
  * **Beatles** - from a Beatles song. The first success of the algorithm on a more serious file. it is clear that finding periodicity here is less trivial, but the algorithm handles it well.
  * **PeriodSearchSuccess** (1 & 2) - in these two files, alongside the graph you can see some written output of the algorithm. The scores of the leaps are great - 3 and 5 (matching) score **much** lower (which is good) than all other leaps (except for 6 = 3\*2, naturally). In each photo, the difs are quite similar to each other as expected, and choosing the median gives the great result in the graph.
  * **DifficultSegmentSuccess** - This segment is much more complicated than the last ones. It is even hard to visually find periodicity in the photo. But looking closer, it is possible to see the algorithm found the periodicity, and not by chance - the correct leap (8) was chosen because it scored much better than the other leaps.
  * **BadSegment** (& **ProblematicSegment**) - This segment is bad. we assumed that the segment is periodic (expecting small changes, yet still periodic), and unfortunately this one is not. therefore, our algorithm returns -1 on this type of segment, admitting it did not find periodicity. Of course, this does not happen very often.

# C #
After creating a periodicity-info object (using either SFT or deterministic FT), we used the fourier polynomial within to recreate the segment. The results are represented by these four screenshots:
  * **HeyJudeFT** - a segment from the song Hey Jude (down), recreated (up) using the 20 heaviest characters. it is a very close approximation, from which it is easy to understand the song, but it lacks the small "interferences" that make the sound beautiful, rather than robotic and monotonic.
  * **A1Output** - when the sound is simpler (vowel or clear note), the use of only a few characters is enough to create an almost perfect replica. It is clear that the function here is _Fourier concentrated_.
    * **OrinSong** - much alike A1, it is difficult to identify which is the input and which is the replica. Clue - in the output, the blue dots are spaced perfectly, by definition.
    * **FT** - The most interesting photo in the gallery. a segment (up) taken from a real song, recreated using the 100 (middle) and then 20 (down) heaviest characters. the difference in quality is indisputable - but the user will generally prefer the second option, because it is simpler and has more than enough information to understand and identify the music.



# **Audio Gallery** #
Files in the audio gallery are divided into pairs - input files (collected from other teams and YouTube), and the wav file output from the sequence WavFileInfoToWavFile( learnWavFile(file) ).
The gallery is divided into three parts:
  * **Vowel Test** - Simpler audio files, consisting of a single, ongoing speaking tone.
  * **Instrument Test** - Audio file containing complicated instrumental music.
  * **Full Song Test** -  Audio file containing complicated singing (with consonants) and sometimes instruments.

**NOTE** - Some of the output files, mostly the last ones, are far from perfect replicas. This is the place to remind ourselves that the goal of the project is _not_ to restore audio files, but to create a useful, workable output for the user in a reasonable amount of time, and that the important issue is that the music is recognizable. Another "problem" with the output - it has a few small jumps every second. This is because we worked with segments (usually 10 per second). Even though it doesn't sound nice, it has no effect on the quality of the output regarding the users.
That being said, we can continue our tour.

# Vowel Test #
As seen in the Photo Gallery, our algorithm handles well vowels - the files usually consist of many good segments and both vowel-type and height of tone can be easily recognized and recovered from the output.

# Instrument Test #
Unlike the monotonous vowels, we have here many changes of tone, causing some segments to be "Bad Segments" (see B in photo gallery) resulting in a tone jump here and there. Yet, all files can be recognized.
  * **OrinSong** - A song from one of the other teams. sound is simple, and is restored almost perfectly (by both FT and SFT).
  * **PianoNote** - A short note. segment length (inputed by user) is longer, therefore fewer jumps. note can be easily recovered using the size of the group, N.
  * **ShortViolin** - A difficult music part (to play and to analyze). Yet, ignoring segment-jumps and interference, notes can be recovered.
  * **RequiemPiano** - A _very_ difficult music part. Two and sometimes three "voices", and many chords. This challenge is a bit beyond our algorithm, but we tried it anyway just to see what happened. using smaller segments, the output is recognizable, and you can hear both left and right hands playing. But still, not really a version to listen to on your IPod :)

# Full Song Test #
This final test includes two loved Beatles songs. Both have singing and instruments in them, which makes life very difficult for our algorithm.
It is obvious that in the parts that consist only of singing (or background music) the output is much clearer - for example, listen to the two halves of HeyJudeOutWithSFT - From the first one can learn the words and notes, and from the second you can barely understand a thing (even though you can still hear some of the song). In our opinion, this is the most complicated type of audio this kind of algorithm can handle.

If you have any questions about the tour we would gladly answer. We hope you enjoyed the tour!
Guy & Mark.