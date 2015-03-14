# Introduction #

Our project is designed to take a wav file input and create a useful "summary" of it in the shape of a WavFileInfo object. After reducing the problem to a single segment, we divide the process into two main independent phases - periodicity search, and the search the heavy characters. In the second phase, the theoretical methods that accomplish it were known in advance (meaning, we did not invent the SFT algorithm ourselves). Most of our work was implementation - it wasn't simple at all, but still not much creativity was needed. We also added a deterministic Fourier transform option, for a user who wants to work with a specific amount of characters. The first part, searching for periodicity in a segment appeared to be the real heart of the project. As shown in the gallery, different files may look completely different, and inventing a flexible, reliable and efficient algorithm was a great challenge.


# Why two? #
We decided to attack the problem from two completely different directions. Each of us had a different idea of how it should be done, so we decided to try both ways, mostly for (curiosity) research purposes. So now we had two options and we needed to decide which one to use, in addition to the two options of the FT. We decided to let the user have the option of trying all 4 possible combinations of periodicity search and FT. Due to the simple, orderly design of our project, it is very easy to add a new algorithm for one of the phases.

# **The Algorithms** #

Both algorithms take as input a buffer of doubles (the segment) and return a sub-buffer of the segment (the period).

# The Patterns Algorithm #

The code of the algorithm is in the class PatternMatching.
The central function is: public static double[.md](.md) SegmentToPeriodUsingPatterns(double[.md](.md) buffer).
At first, the algorithm tries to find a pattern which best "represents" the input segment. It does so by giving grades to different subsegments according to the number of their appearances in the whole segment, and to how accurately they repeat. After choosing the right subsegment, the algorithm chooses the place in the segment where the period is represented in the most common way, and that is the period which is returned by the function.


# The Peaks Algorithm #
The code of the algorithm is in the class PeriodSearch.
The central function is: public static double[.md](.md) SegmentToPeriodUsingPeaks(double[.md](.md) buffer).
The main idea of the algorithm is to work only with the significant extreme points of the segment (or: function). this way, instead of working with 4410 doubles (for example) we work only with about 40.
This allows us to perform many actions very efficiently: The algorithm works at the amazing efficiency of 1-1.5 seconds per 1 minute of music! which is negligible compared to the SFT or deterministic FT.
The algorithm works in four rounds, if needed. A "round" of the algorithm is described here:
in O(n) time, the algorithm finds the significant extreme points (minima or maxima, and more or less, depending on the round). The algorithm guesses the "leap" of the period - the number of extreme points in a period (seen clearly in photos). Grading every leap-option, and carefully handling edge cases, the algorithm decides if there is a leap that is good enough. If so, it announces the period length as the median of all differences between extreme points with _leap_ extreme points between them. If not, another round is called.
The grading system is the core of the algorithm - possible leaps are 1 to 8, meaning we have 8 grades. For example, 3 is graded this way. we take all pairs of points spaced 3 points from each other, and compute the variance from 1 of their ratio (or 1/ratio, whichever is >1). If we our guess of the leap was good, the variance will be small, because of the period. If we guessed wrong, there is no reason for the variance to be small.