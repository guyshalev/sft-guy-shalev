# The Project Structure #

The project consists of 18 classes. They can be divided into five groups:
  * Main and debug classes.
  * WavFile and Graph classes.
  * Period Search libraries.
  * FT and SFT libraries.
  * Output classes.

# Main and debug classes #
(2)
The central function of our project is learnWavFile (wav file --> wavFileInfo), and it is the only function the user of our project needs to know. its code and documentation are in the class **WavHandler**. This class contains some other main functions, such as WavFileInfoToWavFile which does the opposite of learnWavFile.
**Test Utility** is a classes with a main function that can be used for debugging and demonstration of usage of the project.

# WavFile and graph classes #
(3)
This group contains three classes -
**WavFile** & **WavFileException**, used for opening/reading/writing/closing a .wav file.
**GraphingData** is a SUPER-helpful class that enabled us to SEE the wav file beautifully, and add colorful dots in strategic places. It has been one of our main tools in development and debugging. We got it from the Internet and modified it to fit our needs.

# Period search libraries #
(3)
During the project, we've learnt searching for periodicity is far from trivial, and we deeply researched this area. The research resulted in  **PatternMatching** and **PeriodSearch**, classes that contain the _TWO_ independent algorithms (PATTERNS & PEAKS, matching) we developed for finding periodicity in a wav file segment. Both work on simple files, and PeriodSearch, our second algorithm (chronologically) also does well with more complicated files. **PeriodExperiment** is not a library, but a class used for debugging and drawing, to measure the success of period searching.

# FT and SFT libraries #
(7)
**SFTFuncition** is an interface, and its only (surviving) implementation is **ArraySFTFunction**.
As in the period search part, user has _TWO_ options - Using the SFT algorithm from **SFTAlgorithm** or a deterministic version from the **FTLibrary** class. both classes use the **Complex** class, and the SFT option also uses the **Inteval** object, and functions from **SFTUtils**.

# Output classes #
(3)
This group contains three classes - WavFileInfo, ChannelInfo, PeriodicityInfo.

**WavFileInfo** is the type of object that is returned to the user when he calls the main function of the project, learnWavFile. it contains some data, including a list of **ChannelInfo** objects, each representing a channel of the WAV file, and containing a list of **PeriodicityInfo** objects. The core of each PeriodicityInfo is the cyclic group, represented by a map from the heavy characters to their coefficients.