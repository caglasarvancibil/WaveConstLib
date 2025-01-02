package Examples;

import Data.DataPaths;
import Data.LoadData;
import Graphics.Plot;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MathOperators.DoubleOperators;
import SignalProcessing.DoubleSignalProcessingOperations;
import WaveletPackage.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consist of the example usage of the LoadData, WaveletOperations and  WaveletTransformations.
 * @see WaveletOperations
 * @see WaveletTransformations
 * @see WPTSpectrum
 * @see Wavelet
 * @see LoadData
 */
public class WaveletExample {
    public static void main(String[] args) throws FileNotFoundException {
        //Loading Signal Data
        LoadData<Double> loadData=new LoadData<>() {};
        loadData.initialize(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        Matrix<Double> ecgSample=loadData.loadData(DataPaths.SignalsSamples+"ECGBeatSample.txt");
        Matrix<Double> sineSample=loadData.loadData(DataPaths.SignalsSamples+"twoSinusoids.txt");
        Matrix<Double> chirpSample=loadData.loadData(DataPaths.SignalsSamples+"chirpSignal.txt");
        int level=7;
        double fsECG=250.0;
        double fsSine=500.0;
        double fsChirp=1000.0;

        // Load Standard Wavelet Function SYM6 as wavelet
        Matrix<Double> wavecoeff=loadData.loadData(DataPaths.WaveletFunctions+"sym6.txt");
        Wavelet<Double> wavelet=new Wavelet<>();
        wavelet.setName("sym6");
        wavelet.setOrder(6);
        wavelet.setLoD(wavecoeff.getRow(0));
        wavelet.setHiD(wavecoeff.getRow(1));
        wavelet.setLoR(wavecoeff.getRow(2));
        wavelet.setHiR(wavecoeff.getRow(3));

        // Initialize Wavelet Operations
        WaveletOperations<Double> waveletOperations= new WaveletOperations<>() {};
        waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());

        // Initialize Wavelet Transformations
        WaveletTransformations<Double> waveletTransformations=new WaveletTransformations<>() {};
        waveletTransformations.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());

        //Initialize Plot
        Plot plotGraphs=new Plot();

        // Wavelet graph
        WaveletGraph<Double> waveletGraph=waveletOperations.waveletGraphs(wavelet,4);
        System.out.println(waveletGraph.toString());

        //***********ECG Signal***************//
        // DWT and IDWT TO the ECG signal
        DWTObject<Double> dwtObject=waveletTransformations.dwt(ecgSample,wavelet,level);
        Matrix<Double> reconstructedDWT=waveletTransformations.idwt(dwtObject,wavelet,level);

        // Wavelet denoise
        Matrix<Double> denoisedSignal=waveletOperations.wdenoise(ecgSample,wavelet,4);

        Matrix<Double> tecg=DoubleMatrixOperations.getInstance().zeros(1,ecgSample.getColumnLength());
        for (int i = 0; i < ecgSample.getColumnLength(); i++) {
            tecg.setIndex(0,i,i*(1/fsECG));
        }
        plotGraphs=new Plot();
        plotGraphs.setData(tecg,ecgSample);
        plotGraphs.setLabels("Time (s)","Amplitude","Original ECG Signal");
        plotGraphs.setTicUnits(0.2,0.1);
        plotGraphs.plot();

        plotGraphs=new Plot();
        plotGraphs.setData(tecg,reconstructedDWT);
        plotGraphs.setLabels("Time (s)","Amplitude","IDWT Reconstructed");
        plotGraphs.setTicUnits(0.2,0.1);
        plotGraphs.plot();

        plotGraphs=new Plot();
        plotGraphs.setData(tecg,denoisedSignal);
        plotGraphs.setLabels("Time (s)","Amplitude","Wavelet Denoised Signal at Level 4");
        plotGraphs.setTicUnits(0.2,0.1);
        plotGraphs.plot();
        // Wavelet Packet Spectrum of ECG  Signal
        Map<Integer, List<WPTNode<Double>>> ecgWPCoef = waveletTransformations.wpt(ecgSample, wavelet,level,fsECG);
        Map<Integer, List<WPTNode<Double>>> ecgNodeList=new HashMap<>();

        // Selection of Wavelet Packet Coefficients
        List<WPTNode<Double>> ecgSelectedWPCoef=new ArrayList<>();
        for (int i = 0; i < ecgWPCoef.get(6).size(); i++) {
            ecgSelectedWPCoef.add(i,ecgWPCoef.get(6).get(i).copy());
        }


        WPTSpectrum<Double> ecgWPSpectrum=waveletTransformations.wpspectrum(ecgSelectedWPCoef,ecgSample.getColumnLength(),fsECG,256);

        plotGraphs=new Plot();
        plotGraphs.setData(ecgWPSpectrum.getTime(),ecgWPSpectrum.getFrequency(),ecgWPSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum Level 6");
        plotGraphs.setTicUnits(0.1,8.0);
        plotGraphs.plot();

        ecgSelectedWPCoef=new ArrayList<>();
        ecgSelectedWPCoef.add(ecgWPCoef.get(level-1).get(0).copy());
        ecgSelectedWPCoef.add(ecgWPCoef.get(level-1).get(1).copy());
        ecgNodeList.put(level-1,ecgSelectedWPCoef);
        for (int i = 2; i < level ; i++) {
            ecgSelectedWPCoef.add(ecgWPCoef.get(level-i).get(1).copy());

        }
        ecgWPSpectrum=waveletTransformations.wpspectrum(ecgSelectedWPCoef,ecgSample.getColumnLength(),fsECG,512);
        plotGraphs=new Plot();
        plotGraphs.setData(ecgWPSpectrum.getTime(),ecgWPSpectrum.getFrequency(),ecgWPSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum Different Levels");
        plotGraphs.setTicUnits(0.1,8.0);
        plotGraphs.plot();

        //***********Sine Wave***************//

       // WPT and IWPT to Sine Signal
        Map<Integer, List<WPTNode<Double>>> nodeList = waveletTransformations.wpt(sineSample, wavelet,level,fsSine);
        Map<Integer, List<WPTNode<Double>>> tempNodeList=new HashMap<>();

        // Selection of Wavelet Packet Coefficients
        List<WPTNode<Double>> wpCoefList=new ArrayList<>();
        for (int i = 0; i < nodeList.get(6).size(); i++) {
            wpCoefList.add(i,nodeList.get(6).get(i).copy());
        }
        tempNodeList.put(6,wpCoefList);

        // Time Based WPT Reconstruction
        Matrix<Double> sineTimeBasedIWPT=waveletTransformations.timeBasediwpt(new TimeInterval(2.0,3.9),fsSine, sineSample.getColumnLength(), wavelet, tempNodeList);
        Matrix<Double> tSine= DoubleSignalProcessingOperations.getInstance().linspace(0.0,4.0,1/fsSine);
        plotGraphs=new Plot();
        plotGraphs.setData(tSine,sineTimeBasedIWPT);
        plotGraphs.setLabels("Time (s)","Amplitude","2.0-4.0 sec. WPT Reconstruction");
        plotGraphs.setTicUnits(0.2,0.1);
        plotGraphs.plot();

        // WP Spectrum

        WPTSpectrum<Double> wptSpectrum=waveletTransformations.wpspectrum(nodeList.get(6),sineSample.getColumnLength(),fsSine,128);
        plotGraphs=new Plot();
        plotGraphs.setData(wptSpectrum.getTime(),wptSpectrum.getFrequency(),wptSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum");
        plotGraphs.setTicUnits(0.1,8.0);
        plotGraphs.plot();

        // Frequency Based WPT Reconstruction
        //slection of WP coefficients
        tempNodeList= new HashMap<>();
        wpCoefList=new ArrayList<>();
        wpCoefList.add(nodeList.get(level-1).get(0).copy());
        wpCoefList.add(nodeList.get(level-1).get(1).copy());
        tempNodeList.put(level-1,wpCoefList);
        for (int i = 2; i < level ; i++) {
            wpCoefList=new ArrayList<>();
            wpCoefList.add(nodeList.get(level-i).get(1).copy());
            tempNodeList.put(level-i,wpCoefList);

        }
        Matrix<Double> sineFrequencyBasedIWPT=waveletTransformations.frequencyBasediwpt(new FrequencyPair(0.0,32.0),fsSine, wavelet, tempNodeList);
        plotGraphs=new Plot();
        plotGraphs.setData(tSine,sineFrequencyBasedIWPT);
        plotGraphs.setLabels("Time (s)","Amplitude","0-32 Hz WPT Reconstruction");
        plotGraphs.setTicUnits(0.2,0.1);
        plotGraphs.plot();

        //***********Chirp Signal***************//
       //Chirp Signal
        Matrix<Double> tChirp= DoubleSignalProcessingOperations.getInstance().linspace(0.0,2.0,1/fsChirp);

        plotGraphs=new Plot();
        plotGraphs.setData(tChirp,chirpSample);
        plotGraphs.setLabels("Time (s)","Amplitude","Chirp Signal");
        plotGraphs.setTicUnits(0.2,0.5);
        plotGraphs.plot();

        nodeList = waveletTransformations.wpt(chirpSample, wavelet,level,fsChirp);
        tempNodeList= new HashMap<>();
        wpCoefList=new ArrayList<>();
        wpCoefList.add(nodeList.get(level-1).get(0).copy());
        wpCoefList.add(nodeList.get(level-1).get(1).copy());
        tempNodeList.put(level-1,wpCoefList);
        for (int i = 2; i < level ; i++) {
            wpCoefList=new ArrayList<>();
            wpCoefList.add(nodeList.get(level-i).get(1).copy());
            tempNodeList.put(level-i,wpCoefList);

        }


       //0-250 Hz 0.0-0.6 s
        Matrix<Double> chirpTimeFrequencyBasedIWPT1=waveletTransformations.timeFrequencyBasediwpt(new FrequencyPair(0.0,250.0),new TimeInterval(0.0,0.6),fsChirp,chirpSample.getColumnLength(), wavelet, tempNodeList);
        plotGraphs=new Plot();
        plotGraphs.setData(tChirp,chirpTimeFrequencyBasedIWPT1);
        plotGraphs.setLabels("Time (s)","Amplitude","0-250 Hz,0-0.6 s WPT Reconstruction");
        plotGraphs.setTicUnits(0.2,0.5);
        plotGraphs.plot();

        tempNodeList= new HashMap<>();
        wpCoefList=new ArrayList<>();
        wpCoefList.add(nodeList.get(level-1).get(0).copy());
        wpCoefList.add(nodeList.get(level-1).get(1).copy());
        tempNodeList.put(level-1,wpCoefList);
        for (int i = 2; i < level ; i++) {
            wpCoefList=new ArrayList<>();
            wpCoefList.add(nodeList.get(level-i).get(1).copy());
            tempNodeList.put(level-i,wpCoefList);

        }
        //300-500 Hz 0.8-1.4 s
        Matrix<Double> chirpTimeFrequencyBasedIWPT2=waveletTransformations.timeFrequencyBasediwpt(new FrequencyPair(300.0,500.0),new TimeInterval(0.8,1.4),fsChirp,chirpSample.getColumnLength(), wavelet, tempNodeList);
        plotGraphs=new Plot();
        plotGraphs.setData(tChirp,chirpTimeFrequencyBasedIWPT2);
        plotGraphs.setLabels("Time (s)","Amplitude","300-500 Hz,0.8-1.4 s WPT Reconstruction");
        plotGraphs.setTicUnits(0.2,0.5);
        plotGraphs.plot();

        // Wavelet Packet Spectrum of chirp  Signal
        Map<Integer, List<WPTNode<Double>>> chirpWPCoef = waveletTransformations.wpt(chirpSample, wavelet,level,fsChirp);
        Map<Integer, List<WPTNode<Double>>> chirpNodeList=new HashMap<>();

        // Selection of Wavelet Packet Coefficients
        List<WPTNode<Double>> chirpSelectedWPCoef=new ArrayList<>();
        for (int i = 0; i < chirpWPCoef.get(6).size(); i++) {
            chirpSelectedWPCoef.add(i,chirpWPCoef.get(6).get(i).copy());
        }


        WPTSpectrum<Double> chirpWPSpectrum=waveletTransformations.wpspectrum(chirpSelectedWPCoef,chirpSample.getColumnLength(),fsChirp,128);
        plotGraphs=new Plot();
        plotGraphs.setData(chirpWPSpectrum.getTime(),chirpWPSpectrum.getFrequency(),chirpWPSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum");
        plotGraphs.setTicUnits(0.5,50.0);
        plotGraphs.plot();

        chirpSelectedWPCoef=new ArrayList<>();
        chirpSelectedWPCoef.add(chirpWPCoef.get(level-1).get(0).copy());
        chirpSelectedWPCoef.add(chirpWPCoef.get(level-1).get(1).copy());
        //ecgNodeList.put(level-1,chirpSelectedWPCoef);
        for (int i = 2; i < level ; i++) {
            chirpSelectedWPCoef.add(chirpWPCoef.get(level-i).get(1).copy());

        }
        chirpWPSpectrum=waveletTransformations.wpspectrum(chirpSelectedWPCoef,chirpSample.getColumnLength(),fsChirp,128);
        plotGraphs=new Plot();
        plotGraphs.setData(chirpWPSpectrum.getTime(),chirpWPSpectrum.getFrequency(),chirpWPSpectrum.getSpectrum());
        plotGraphs.setLabels("Time (s)","Frequency (Hz)","WP Magnitude Spectrum");
        plotGraphs.setTicUnits(0.5,50.0);
        plotGraphs.plot();


    }
}
