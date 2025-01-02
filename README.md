# Introduction

Wavelet construction library presents signal processing methodologies and numerical analysis of wavelet theory with its conditions. Therefore, the library contains methods that enable the extraction of meaningful information from an application-specific signal and its analysis by applying wavelet transform algorithms.

>[!IMPORTANT]
>* No additional methods has been developed for signal graphs and visuals. To visualize the graphs JfreeChart Library has been used and stated as dependency.
>* No additional methods have been produced for eigenvector decomposition operations. For this, the commons-math3 library has been used and stated as dependency.
>* The library only presents first-generation orthogonal wavelet function construction.
>* The applied signals can be loaded via “txt” files.

>[!WARNING]
>Construction can be provided between the filter order/degree 2 and 20.

>[!CAUTION]
>Roots that satisfy all wavelet conditions may not always be found in the search spaces.


# Getting Started

1. Create new Java project.
2. Create two folders by giving the SignalSamples and WaveletFunctions name under the project's resources folder.
3. Put the txt files under the corresponding folders.
4. Open the project settings. Go to the libraries section. Click the add library. Select Java. Choose WaveConstLib.jar file.

## Examples

There are five examples under the main branch to illustrate the usage of the library. These are:

1. [Matrices and Linear Algebra](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/MatricesAndLinearAlgebraExample.java)
2. [Signal Processing](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/SignalProcessingExample.java)
3. [Wavelet](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/WaveletExample.java)
4. [First-Generation Wavelet Construction](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/FirstGenerationWaveletConstructionExample.java)
5. [Signal Specific Wavelet Construction with MOEA Framework](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/MOEAExample.java)

As an example of [First-Generation Wavelet Construction](https://github.com/caglasarvancibil/WaveConstLib/blob/main/src/main/java/Examples/FirstGenerationWaveletConstructionExample.java)

To obtain the DB4 wavelet function coefficients, apply the Haar basis vector of [1,1]. While the Haar basis vector is sent as the first parameter of the constructWavelet method, the filter degree is 4 and the minimum phase is assigned as the filter type. The constructed wavelet function is sent to the waveletFeasibility method under the WaveletOperations class for feasibility evaluation. And this method performs the rule analysis of the constructed wavelet function numerically and returns. The evaluation outputs can be printed to the console with the toSummary() and getFeasibility() methods.

This example also includes the randomly construction of feasible wavelet functions. Construct feasible wavelet functions as randomly, it applies constructFeasibleWavelet() method under the abstract WaveletConstruction class.

For more information check the example package.

# Documentation and Download

Check and download the documentation and release from [WaveConstLibJavaDoc and Release](https://github.com/caglasarvancibil/WaveConstLib/releases/tag/WaveConstLibv1.0)

# How To Contribute

You can create a new branch and push your suggestions. And also you can propose or discuss under the issues tab.

# Library Dependencies

* commons-math3 version 3.6
* jfreechart version 1.5.3



