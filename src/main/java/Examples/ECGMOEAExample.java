package Examples;

import Data.DataPaths;
import Data.LoadData;
import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.BasisVariableType;
import FirstGenerationWaveletConstruction.FilterType;
import Graphics.Plot;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MOEAClasses.NSGAIIRun;
import MOEAClasses.NSRWaveletConstructionProblem;
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
 * Please check the NSRWaveletConstructionProblem class:
 * To change the objective function and parameters.
 * To change the maximum trial number for the construct feasible wavelets
 * After run finishes, the added "AnalyzeECGWaveletResults" example provides the illustration of constructed wavelet function and signal analysis.
 *
 * WARNINGS:
 * 1) To construct wavelet functions with Polynomial and Discrete Variable,
 * number of variables must set as 2.
 * Because only two pieces supported for Polynomials
 * and two discrete samples supported for Discrete Variable basis.
 * 2) To construct wavelet functions with UCRoots, the number of variable and filter order value must be same.
 */
public class ECGMOEAExample {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        //Loading  Data Bases
        LoadData<Double> loadData = new LoadData<>() {};
        loadData.initialize(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        Matrix<Double> signals = loadData.loadData(DataPaths.SignalsSamples + "mitArrythmiaDB_NSR_Type_114.txt");
        SignalDataBase<Double> sigDB = new SignalDataBase<>();

        sigDB.setFs(360.0);
        sigDB.setSignals(signals);
        sigDB.setNumberOfInstance(signals.getRowLength());
        sigDB.setMaxlevel(7);
        sigDB.setSignalType("N");
        List<SignalDataBase<Double>> signalDataBaseList = new ArrayList<>();
        signalDataBaseList.add(sigDB);

        // Assign Wavelet Construction Problem Parameters
        int filterOrder = 5;
        FilterType type = FilterType.SYMMETRIC;
        BasisVariableType basisVariableType = BasisVariableType.UCROOT;

        // Assign MOEA Parameters
        int numberOfVariables = 5;
        int numberOfObjectives = 2;
        int popSize = 500;
        int numberOfGenerations = 6;
        double crossProbobality = 0.8;
        NSRWaveletConstructionProblem problem=new NSRWaveletConstructionProblem(numberOfVariables,numberOfObjectives,signalDataBaseList,filterOrder, type,basisVariableType);


        Spea2Run spea2Run = new Spea2Run();
        List<Wavelet<Double>> waveletList = spea2Run.Run(numberOfVariables,
                numberOfObjectives,
                crossProbobality,
                popSize,
                numberOfGenerations,
                problem,
                signalDataBaseList,
                filterOrder,
                type,
                basisVariableType);

//          NSGAIIRun nsgaiiRun=new NSGAIIRun();
//        List<Wavelet<Double>> waveletList=nsgaiiRun.Run(numberOfVariables,
//                                                        numberOfObjectives,
//                                                        crossProbobality,
//                                                        popSize,
//                                                        numberOfGenerations,
//                                                        problem,
//                                                        signalDataBaseList,
//                                                        filterOrder,
//                                                        type,
//                                                        basisVariableType);

        //**********************Analyze the Results**********************//
        SaveConstructedWaveletsAsTxt save = new SaveConstructedWaveletsAsTxt();

        Wavelet<Double> wavelet = new Wavelet<>();

        for (int i = 0; i < waveletList.size(); i++) {
            wavelet = waveletList.get(i);
            wavelet.setName("NSRSYM5_" + i);
            System.out.println(wavelet);

            //Save the Wavelet Function filters as a txt file uncomment the line 104
            //save.saveFilterCoefficients(wavelet);

            // Wavelet Function Graphs
            WaveletOperations<Double> waveletOperations = new WaveletOperations<>() {};
            waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                    DoubleMatrixOperations.getInstance());
            WaveletGraph<Double> waveletGraph = waveletOperations.waveletGraphs(wavelet, 4);
            Plot plotGraphs = new Plot();
            plotGraphs.createSubPlot(1, 4);
            plotGraphs.setData(waveletGraph.getTime(), waveletGraph.getPhi());
            plotGraphs.setLabels("Time(s)", "Amplitude", "Scaling Function");
            plotGraphs.setTicUnits(2, 0.1);
            plotGraphs.setPosition(0);
            plotGraphs.setData(waveletGraph.getTime(), DoubleMatrixOperations.getInstance().multiplyScalar(1.0, waveletGraph.getPsi()));
            plotGraphs.setLabels("Time(s)", "Amplitude", "Wavelet Function");
            plotGraphs.setTicUnits(2, 0.1);
            plotGraphs.setPosition(1);
            plotGraphs.setData(waveletGraph.getFrequency(), waveletGraph.getPhiFreqSpec());
            plotGraphs.setLabels("Frequency (Hz)", "Amplitude", "Frequency Spectrum of Scaling Function ");
            plotGraphs.setTicUnits(2, 0.5);
            plotGraphs.setPosition(2);
            plotGraphs.setData(waveletGraph.getFrequency(), waveletGraph.getPsiFreqSpec());
            plotGraphs.setLabels("Frequency (Hz)", "Amplitude", "Frequency Spectrum of Wavelet Function");
            plotGraphs.setTicUnits(2, 0.5);
            plotGraphs.setPosition(3);
            plotGraphs.PlotSignal();
        }

    }
}
