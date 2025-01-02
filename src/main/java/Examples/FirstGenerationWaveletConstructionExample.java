package Examples;

import Data.DataPaths;
import Data.LoadData;
import Data.Parameters;
import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.*;
import Graphics.Plot;
import LinearAlgebra.DoubleMatrix;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MathOperators.DoubleOperators;
import WaveletPackage.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class FirstGenerationWaveletConstructionExample {
    public static void main(String[] args) throws FileNotFoundException {


        WaveletConstruction<Double> waveletConstruction=new WaveletConstruction<>() {};
        WaveletOperations<Double> waveletOperations= new WaveletOperations<>() {};

        waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        waveletConstruction.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());


        //**************Wavelet CONSTRUCTION of DB4 Example*******************//
        Matrix<Double> base=new DoubleMatrix(new Double[][]{{1.0,1.0}});
        Wavelet<Double> wavelet=waveletConstruction.constructWavelet(base,4, FilterType.MINPHASE);

        System.out.println(wavelet);
        System.out.println( waveletOperations.waveletFeasibility(wavelet).toSummary());
        System.out.println( waveletOperations.waveletFeasibility(wavelet).getFeasibility());

        //Default seed is 3. In here seed value changed as 5.
        Parameters.getInstance().setSEED(5);
        //***************Wavelet construction of Randomly Produced Bases and Analysis with Signals*********//
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

        //*************Construct Wavelet Function Parameters**************//
        IBasisFilterVariable piecewisePolynomialVariable=new PiecewisePolynomialVariable();
        List<VariableBoundary> variableBoundaries=new ArrayList<>();
        variableBoundaries.add(new VariableBoundary(0.1,0.8));
        variableBoundaries.add(new VariableBoundary(-0.8,0.1));


        IBasisFilterVariable ucRootVariable=new UCRootVariable();
        List<VariableBoundary> variableBoundaries2=new ArrayList<>();
        variableBoundaries2.add(new VariableBoundary(90.0,270.0));
        variableBoundaries2.add(new VariableBoundary(90.0,270.0));

        IBasisFilterVariable discreteSampleVariable=new DiscreteSampleVariable();
        List<VariableBoundary> variableBoundaries3=new ArrayList<>();
        variableBoundaries3.add(new VariableBoundary(-1.0,1.0));
        variableBoundaries3.add(new VariableBoundary(-1.0,1.0));

        //********************Analyze Constructed Wavelet Function Parameters*****************//
        AbstractAnalyzeWavelet<Double> analyzeWavelet=new AbstractAnalyzeWavelet<>() {};
        analyzeWavelet.initializeOperators(DoubleOperators.getInstance(),DoubleMatrixOperations.getInstance());

        //Common Frequency Ranges
        List<FrequencyPair> frequencyPairList=new ArrayList<>();
        frequencyPairList.add(new FrequencyPair(4.0,8.0));
        frequencyPairList.add(new FrequencyPair(8.0,16.0));
        frequencyPairList.add(new FrequencyPair(16.0,32.0));

        //Scale Dependent Power Parameters
        List<FrequencyPair> frequencyPairList2=new ArrayList<>();
        frequencyPairList2.add(new FrequencyPair(8.0,16.0));//maksimize
        frequencyPairList2.add(new FrequencyPair(16.0,32.0));//minimize
        List<TimeInterval> timeIntervalList=new ArrayList<>();
        timeIntervalList.add(new TimeInterval(0.5,1.0));
        timeIntervalList.add(new TimeInterval(0.5,1.0));

        //******************** UCRootsVariable Filter Order=4 **********************//
        for (int i = 0; i < 5; i++) {
            ConstructionObject constructionObject= waveletConstruction
                    .constructFeasibleWavelet(BasisVariableType.UCROOT, ucRootVariable,
                            variableBoundaries2, 4,FilterType.MINPHASE,100);
            wavelet= constructionObject.getWavelet();

            System.out.println(wavelet);
            System.out.println( waveletOperations.waveletFeasibility(wavelet).toSummary());
            System.out.println( waveletOperations.waveletFeasibility(wavelet).getFeasibility());

            double [] mse=analyzeWavelet.reconstructionMSE(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"N",new FrequencyPair(2.0,64.0));
            double [] commonfreq=analyzeWavelet.commonFreqRanges(signalDataBaseList,wavelet,frequencyPairList,"N");
            double [] scaledepent=analyzeWavelet.scaleDependent(signalDataBaseList,wavelet,frequencyPairList2,timeIntervalList);
            double a=analyzeWavelet.scaleDependentCommonFrequency(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"O");
            System.out.println("std common freq= "+a);

            System.out.println("STD: "+mse[0]);
            System.out.println("Mean: "+mse[1]);
            System.out.println(frequencyPairList.get(0).toString()+"= "+commonfreq[0]);
            System.out.println(frequencyPairList.get(1).toString()+"= "+commonfreq[1]);
            System.out.println(frequencyPairList.get(2).toString()+"= "+commonfreq[2]);
            System.out.println(frequencyPairList2.get(0).toString()+" & "+timeIntervalList.get(0).toString()+"= "+scaledepent[0]);
            System.out.println(frequencyPairList2.get(1).toString()+" & "+timeIntervalList.get(1).toString()+"= "+scaledepent[1]);

            // Wavelet Function Graphs
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
        //********************PiecewisePolynomialVariable Filter Order=3 **********************//

        for (int i = 0; i < 5; i++) {
            ConstructionObject constructionObject=waveletConstruction
                    .constructFeasibleWavelet(BasisVariableType.POLYNOMIAL,piecewisePolynomialVariable,
                            variableBoundaries,3,FilterType.MINPHASE,100);


            wavelet= constructionObject.getWavelet();

            double [] mse=analyzeWavelet.reconstructionMSE(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"N",new FrequencyPair(2.0,64.0));
            double [] commonfreq=analyzeWavelet.commonFreqRanges(signalDataBaseList,wavelet,frequencyPairList,"N");
            double [] scaledepent=analyzeWavelet.scaleDependent(signalDataBaseList,wavelet,frequencyPairList2,timeIntervalList);
            double a=analyzeWavelet.scaleDependentCommonFrequency(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"O");

            System.out.println("STD of Common Freq= "+a);
            System.out.println("STD: "+mse[0]);
            System.out.println("Mean: "+mse[1]);
            System.out.println(frequencyPairList.get(0).toString()+"= "+commonfreq[0]);
            System.out.println(frequencyPairList.get(1).toString()+"= "+commonfreq[1]);
            System.out.println(frequencyPairList.get(2).toString()+"= "+commonfreq[2]);
            System.out.println(frequencyPairList2.get(0).toString()+" & "+timeIntervalList.get(0).toString()+"= "+scaledepent[0]);
            System.out.println(frequencyPairList2.get(1).toString()+" & "+timeIntervalList.get(1).toString()+"= "+scaledepent[1]);

            // Wavelet Function Graphs
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

        //******************** DiscreteSampleVariable Filter Order=5 **********************//

        for (int i = 0; i < 5; i++) {
            ConstructionObject constructionObject=waveletConstruction
                    .constructFeasibleWavelet(BasisVariableType.DISCRETE,discreteSampleVariable,
                            variableBoundaries3,5,FilterType.SYMMETRIC,100);


            wavelet= constructionObject.getWavelet();

            double [] mse=analyzeWavelet.reconstructionMSE(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"N",new FrequencyPair(2.0,64.0));
            double [] commonfreq=analyzeWavelet.commonFreqRanges(signalDataBaseList,wavelet,frequencyPairList,"N");
            double [] scaledepent=analyzeWavelet.scaleDependent(signalDataBaseList,wavelet,frequencyPairList2,timeIntervalList);
            double a=analyzeWavelet.scaleDependentCommonFrequency(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.5,1.0),"O");

            System.out.println("STD of Common Freq= "+a);
            System.out.println("STD: "+mse[0]);
            System.out.println("Mean: "+mse[1]);
            System.out.println(frequencyPairList.get(0).toString()+"= "+commonfreq[0]);
            System.out.println(frequencyPairList.get(1).toString()+"= "+commonfreq[1]);
            System.out.println(frequencyPairList.get(2).toString()+"= "+commonfreq[2]);
            System.out.println(frequencyPairList2.get(0).toString()+" & "+timeIntervalList.get(0).toString()+"= "+scaledepent[0]);
            System.out.println(frequencyPairList2.get(1).toString()+" & "+timeIntervalList.get(1).toString()+"= "+scaledepent[1]);

            // Wavelet Function Graphs
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


    }

}
