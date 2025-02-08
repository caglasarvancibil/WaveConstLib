package Examples;

import Data.DataPaths;
import Data.LoadData;
import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.AbstractAnalyzeWavelet;
import Graphics.Plot;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MathOperators.DoubleOperators;
import SignalProcessing.DoubleSignalProcessingOperations;
import SignalProcessing.SignalProcessingOperations;
import WaveletPackage.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Example Analyzes the output of the ECGMOEAExample.
 * The time duration of analyzing one wavelet function with 90593 beat sample is 13 minutes.
 */
public class AnalyzeECGWaveletResults {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        // Initialize Wavelet Operations
        WaveletOperations<Double> waveletOperations=new WaveletOperations<Double>() {};
        waveletOperations.initializeOperators(DoubleOperators.getInstance(),DoubleMatrixOperations.getInstance());

        //Loading  Data Bases
        LoadData<Double> loadData = new LoadData<>() {};
        loadData.initialize(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        Matrix<Double> signal = loadData.loadData(DataPaths.SignalsSamples + "mitArrythmiaDB_NSR_Type.txt");
        SignalDataBase<Double> sigDB = new SignalDataBase<>();

        sigDB.setFs(360.0);
        sigDB.setSignals(signal);
        sigDB.setNumberOfInstance(signal.getRowLength());
        sigDB.setMaxlevel(7);
        sigDB.setSignalType("N");
        List<SignalDataBase<Double>> signalDataBaseList = new ArrayList<>();
        signalDataBaseList.add(sigDB);

        // Load Standard Wavelet Function by changing the string Name
        String waveletName="NSRsym5_2";
        Matrix<Double> wavecoeff=loadData.loadData(DataPaths.WaveletFunctions+waveletName+".txt");
        Wavelet<Double> wavelet=new Wavelet<>();
        wavelet.setName(waveletName);
        wavelet.setOrder(5);
        wavelet.setLoD(wavecoeff.getRow(0));
        wavelet.setHiD(wavecoeff.getRow(1));
        wavelet.setLoR(wavecoeff.getRow(2));
        wavelet.setHiR(wavecoeff.getRow(3));

        /***********Plot Wavelet Functions************/
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

        //*********************Analyze constructed wavelet function**************************//

        // Initialize Wavelet Transformations
        WaveletTransformations<Double> waveletTransformations = new WaveletTransformations<>() {};
        waveletTransformations.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());
        SignalProcessingOperations<Double> signalProcessingOperations=new SignalProcessingOperations<Double>() {};
        signalProcessingOperations.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());

        /*************Signal Sample*******************/
        SignalDataBase<Double> signalDB = signalDataBaseList.get(0);
        double fs = signalDB.getFs();
        int level = signalDB.getMaxlevel();
        Matrix<Double> t= DoubleSignalProcessingOperations.getInstance().linspace(0.0,((1/fs)*260)-1/fs,1/fs);
        Matrix<Double> tempSignals = signalDB.getSignals();
        int index=9683;

        plotGraphs=new Plot();
        plotGraphs.setData(t,tempSignals.getRow(index));
        plotGraphs.setLabels("Time (s)","Amplitude","Original Signal");
        plotGraphs.setTicUnits(0.072,0.1);
        plotGraphs.plot();

        // Wavelet Packet Spectrum of Signals
        Map<Integer, List<WPTNode<Double>>> fullWptNodes = waveletTransformations.wpt(tempSignals.getRow(index), wavelet, level, fs);
        List<WPTNode<Double>> wptNodeList=new ArrayList<>();
        // Selection of Wavelet Packet Coefficients
        wptNodeList = new ArrayList<>();
        wptNodeList.add(fullWptNodes.get(level - 1).get(0).copy());
        wptNodeList.add(fullWptNodes.get(level - 1).get(1).copy());
        for (int k = 2; k < level; k++) {
            wptNodeList.add(fullWptNodes.get(level - k).get(1).copy());
        }
        // Calculate WP Spectrum and Plot
        WPTSpectrum<Double> wptSpectrum = waveletTransformations.wpspectrum(wptNodeList, tempSignals.getRow(index).getColumnLength(), fs, (int) Math.pow(2, level+2));
        plotGraphs = new Plot();
        plotGraphs.setData(wptSpectrum.getTime(), wptSpectrum.getFrequency(), wptSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)", "Frequency (Hz)", "WP Magnitude Spectrum");
        plotGraphs.setTicUnits(0.072, 2.8125);
        plotGraphs.plot();

        /****************** time-frequency based reconstruction***********************/
        Map<Integer, List<WPTNode<Double>>> tempNodeList=new HashMap<>();

        Map<Integer, List<WPTNode<Double>>>  nodeList = waveletTransformations.wpt(tempSignals.getRow(index), wavelet,level,fs);
        wptNodeList=new ArrayList<>();
        tempNodeList= new HashMap<>();
        tempNodeList.put(5,nodeList.get(5));
        Matrix<Double> Pwave=waveletTransformations.timeFrequencyBasediwpt(new FrequencyPair(5.625,11.25),new TimeInterval(0.0,0.31),fs,tempSignals.getColumnLength(), wavelet, tempNodeList);
        nodeList = waveletTransformations.wpt(tempSignals.getRow(index), wavelet,level,fs);
        tempNodeList= new HashMap<>();
        tempNodeList.put(6,nodeList.get(6));
        Matrix<Double> Twave=waveletTransformations.timeFrequencyBasediwpt(new FrequencyPair(2.8125,5.625),new TimeInterval(0.4,0.71),fs,tempSignals.getColumnLength(), wavelet, tempNodeList);
        Matrix<Double> result=DoubleMatrixOperations.getInstance().add(Pwave,Twave);

        plotGraphs=new Plot();
        plotGraphs.setData(t,result);
        plotGraphs.setYaxisRanges(-0.2,0.9);
        plotGraphs.setLabels("Time (s)","Amplitude","P and T Wave WPT Reconstruction");
        plotGraphs.setTicUnits(0.072,0.1);
        plotGraphs.plot();

/***************************** Analyze 90593 NSR Beat  *************************************/
        AbstractAnalyzeWavelet<Double> analyzeWavelet=new AbstractAnalyzeWavelet<Double>() {};
        analyzeWavelet.initializeOperators(DoubleOperators.getInstance(),DoubleMatrixOperations.getInstance());


        double[] objVal1=analyzeWavelet.reconstructionMSE(signalDataBaseList,wavelet,new FrequencyPair(5.625,11.25),new TimeInterval(0.0,0.31),"N",new FrequencyPair(2.8125,45.0),new TimeInterval(0.0,0.31));
        double[] objVal2=analyzeWavelet.reconstructionMSE(signalDataBaseList,wavelet,new FrequencyPair(2.8125,5.625),new TimeInterval(0.4,0.71),"N",new FrequencyPair(2.8125,45.0),new TimeInterval(0.43,0.71));

        System.out.println("P-wave: mean="+ objVal1[1]+" ,std= "+  objVal1[0]);
        System.out.println("T-wave: mean="+ objVal2[1]+" ,std= "+  objVal2[0]);


    }
}
