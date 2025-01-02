package Examples;

import Data.DataPaths;
import Data.LoadData;
import Data.Parameters;
import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.AbstractAnalyzeWavelet;
import FirstGenerationWaveletConstruction.BasisVariableType;
import FirstGenerationWaveletConstruction.FilterType;
import Graphics.Plot;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MOEAClasses.NSGAIIRun;
import MOEAClasses.SaveConstructedWaveletsAsTxt;
import MOEAClasses.Spea2Run;
import MathOperators.DoubleOperators;
import WaveletPackage.*;


import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * You can run NSGAII algorithm by uncommenting the code lines and commenting the SPEAII code lines.
 * You can change the construction parameters.
 * Please check the WaveletConstructionProblem class:
 * To change the objective function and parameters.
 * To change the maximum trial number for the construct feasible wavelets
 * After run finishes, the added analysis code provides the illustration of constructed wavelet function and signal analysis.
 *
 * WARNINGS:
 * 1) To construct wavelet functions with Polynomial and Discrete Variable,
 * number of variables must set as 2.
 * Because only two pieces supported for Polynomials
 * and two discrete samples supported for Discrete Variable basis.
 * 2) To construct wavelet functions with UCRoots, the number of variable and filter order value must be same.
 */
public class MOEAExample {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        //Assign seed value. If you don't use this assignment default seed value is taken as 3.
        //Parameters.getInstance().setSEED(5);

        //Loading  Data Bases
        LoadData<Double> loadData=new LoadData<>() {};
        loadData.initialize(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        Matrix<Double> signal=loadData.loadData(DataPaths.SignalsSamples+"normalTypeSineSamples.txt");
        Matrix<Double> otherType=loadData.loadData(DataPaths.SignalsSamples+"otherTypeSineSamples.txt");
        SignalDataBase<Double> sigDB=new SignalDataBase<>();
        SignalDataBase<Double> otherSigDB=new SignalDataBase<>();
        sigDB.setFs(500.0);
        otherSigDB.setFs(1000.0);
        sigDB.setSignals(signal);
        otherSigDB.setSignals(otherType);
        sigDB.setNumberOfInstance(signal.getRowLength());
        otherSigDB.setNumberOfInstance(otherType.getRowLength());
        sigDB.setMaxlevel(8);
        otherSigDB.setMaxlevel(9);
        sigDB.setSignalType("N");
        otherSigDB.setSignalType("O");
        List<SignalDataBase<Double>> signalDataBaseList=new ArrayList<>();
        signalDataBaseList.add(sigDB);
        signalDataBaseList.add(otherSigDB);

       // Assign Wavelet Construction Problem Parameters
        int filterOrder=5;
        FilterType type=FilterType.SYMMETRIC;
        BasisVariableType basisVariableType=BasisVariableType.UCROOT;

        // Assign MOEA Parameters
        int numberOfVariables=5;
        int numberOfObjectives=2;
        int popSize=500;
        int numberOfGenerations=6;
        double crossProbobality=0.8;


        Spea2Run spea2Run=new Spea2Run();
        List<Wavelet<Double>> waveletList=spea2Run.Run(numberOfVariables,
                                                        numberOfObjectives,
                                                        crossProbobality,
                                                        popSize,
                                                        numberOfGenerations,
                                                        signalDataBaseList,
                                                        filterOrder,
                                                        type,
                                                        basisVariableType);
//        NSGAIIRun nsgaiiRun=new NSGAIIRun();
//        List<Wavelet<Double>> waveletList=nsgaiiRun.Run(numberOfVariables,
//                                                        numberOfObjectives,
//                                                        crossProbobality,
//                                                        popSize,
//                                                        numberOfGenerations,
//                                                        signalDataBaseList,
//                                                        filterOrder,
//                                                        type,
//                                                        basisVariableType);

//**********************Analyze the Results**********************//
        SaveConstructedWaveletsAsTxt save=new SaveConstructedWaveletsAsTxt();

        Wavelet<Double> wavelet=new Wavelet<>();
        for (int i = 0; i < waveletList.size(); i++) {
            wavelet=waveletList.get(i);
            wavelet.setName("ucRootSYM5_"+i);
            System.out.println(wavelet);

            //Save the Wavelet Function filters as a txt file uncomment the line 112
           // save.saveFilterCoefficients(wavelet);

            // Wavelet Function Graphs
            WaveletOperations<Double> waveletOperations= new WaveletOperations<>() {};
            waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                    DoubleMatrixOperations.getInstance());
            WaveletGraph<Double> waveletGraph=waveletOperations.waveletGraphs(wavelet,4);
            Plot plotGraphs=new Plot();
            plotGraphs.createSubPlot(1,4);
            plotGraphs.setData(waveletGraph.getTime(),waveletGraph.getPhi());
            plotGraphs.setLabels("Time(s)","Amplitude","Scaling Function");
            plotGraphs.setTicUnits(2,0.1);
            plotGraphs.setPosition(0);
            plotGraphs.setData(waveletGraph.getTime(),DoubleMatrixOperations.getInstance().multiplyScalar(1.0,waveletGraph.getPsi()));
            plotGraphs.setLabels("Time(s)","Amplitude","Wavelet Function");
            plotGraphs.setTicUnits(2,0.1);
            plotGraphs.setPosition(1);
            plotGraphs.setData(waveletGraph.getFrequency(),waveletGraph.getPhiFreqSpec());
            plotGraphs.setLabels("Frequency (Hz)","Amplitude","Frequency Spectrum of Scaling Function ");
            plotGraphs.setTicUnits(2,0.5);
            plotGraphs.setPosition(2);
            plotGraphs.setData(waveletGraph.getFrequency(),waveletGraph.getPsiFreqSpec());
            plotGraphs.setLabels("Frequency (Hz)","Amplitude","Frequency Spectrum of Wavelet Function");
            plotGraphs.setTicUnits(2,0.5);
            plotGraphs.setPosition(3);
            plotGraphs.PlotSignal();
        }

        wavelet=waveletList.get(1);

        //*********************Analyze constructed wavelet function**************************//
        // Initialize Wavelet Transformations
        WaveletTransformations<Double> waveletTransformations=new WaveletTransformations<>() {};
        waveletTransformations.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());

        //Initialize Analyze Wavelets
        AbstractAnalyzeWavelet<Double> analyzeWavelet=new AbstractAnalyzeWavelet<>() {};
        analyzeWavelet.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());

        //Scale Dependent Power Parameters
        List frequencyPairList=new ArrayList<>();
        frequencyPairList.add(new FrequencyPair(8.0,16.0));
        List timeIntervalList=new ArrayList<>();
        timeIntervalList.add(new TimeInterval(0.65,0.87));

        WPTSpectrum<Double> wptSpectrum;
        Map<Integer, List<WPTNode<Double>>> fullWptNodes;
        List<WPTNode<Double>> wptNodeList;

        for (int i = 0; i < signalDataBaseList.size(); i++) {
            Matrix<Double>  values=analyzeWavelet.calculateScaleDependentCoefficients(signalDataBaseList.get(i),wavelet,frequencyPairList,timeIntervalList);
            values.print();
            SignalDataBase<Double> signalDB=signalDataBaseList.get(i);
            double fs=signalDB.getFs();
            int level=signalDB.getMaxlevel();
            int numOfInstances=signalDB.getNumberOfInstance();
            Matrix<Double> tempSignals=signalDB.getSignals();
            for (int j = 0; j < numOfInstances; j++) {

                // Wavelet Packet Spectrum of Signals
                fullWptNodes = waveletTransformations.wpt(tempSignals.getRow(j), wavelet,level,fs);

                // Selection of Wavelet Packet Coefficients
                wptNodeList=new ArrayList<>();
                wptNodeList.add(fullWptNodes.get(level-1).get(0).copy());
                wptNodeList.add(fullWptNodes.get(level-1).get(1).copy());
                for (int k = 2; k <level ; k++) {
                    wptNodeList.add(fullWptNodes.get(level-k).get(1).copy());
                }

               // Calculate WP Spectrum and Plot
                wptSpectrum=waveletTransformations.wpspectrum(wptNodeList,tempSignals.getColumnLength(),fs,(int)Math.pow(2,level-1));
                Plot plotGraphs=new Plot();
                plotGraphs.setData(wptSpectrum.getTime(),wptSpectrum.getFrequency(),wptSpectrum.getSpectrum());
                plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum");
                plotGraphs.setTicUnits(0.5,32);
                plotGraphs.plot();

            }
        }


    }
}