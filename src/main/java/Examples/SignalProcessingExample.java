package Examples;

import Data.DataPaths;
import Data.LoadData;
import Graphics.Plot;
import LinearAlgebra.*;
import MathOperators.DoubleOperators;
import SignalProcessing.DoubleConvolution;
import SignalProcessing.DoubleSignalProcessingOperations;
import SignalProcessing.SymbolicConvolution;
import Symbolic.ParseStringsOperations;

import java.io.FileNotFoundException;

/**
 * Consist of the example usage of the Signal Processing Package.
 * @see LoadData
 * @see SignalProcessing.SignalProcessingOperations
 * @see DoubleSignalProcessingOperations
 * @see SignalProcessing.Convolution
 */
public class SignalProcessingExample {
    public static void main(String[] args) throws FileNotFoundException {

        // fft and ifft analysis of a signal noisySineWave
        LoadData<Double> loadData=new LoadData<>() {};
        loadData.initialize(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        Matrix<Double> signalSample=loadData.loadData(DataPaths.SignalsSamples+"twoSinusoids.txt");


        int fftPow=1;
        double val=signalSample.getColumnLength();
        while(val>1.0){
            val=val/2;
            fftPow++;

        }
        ComplexMatrix signalFFT= DoubleSignalProcessingOperations.getInstance().fft(signalSample,(int)Math.pow(2,fftPow));
        Matrix<Double> signalFreqSpec= DoubleMatrixOperations.getInstance().magnitude(signalFFT);
        Matrix<Double> freq=DoubleMatrixOperations.getInstance().zeros(1,signalFreqSpec.getColumnLength());
        Matrix<Double> t=DoubleMatrixOperations.getInstance().zeros(1,signalSample.getColumnLength());
        Matrix<Double> reconstructedSignal=DoubleSignalProcessingOperations.getInstance().ifft(signalFFT,2001);
        Matrix<Double> t2=DoubleSignalProcessingOperations.getInstance().linspace(0.0,2000.0,1.0);
        int m=signalFreqSpec.getColumnLength();
        double fs=500;

        for (int i = 0; i < signalSample.getColumnLength(); i++) {
            t.setIndex(0,i,i*(1/fs));
        }
        for (int i = 0; i < m/2; i++) {
            freq.setIndex(0,i,(fs/m)*i);
        }

        Plot plotGraphs=new Plot();
        plotGraphs.setData(t,signalSample);
        plotGraphs.setLabels("Time (s)","Amplitude","Original Signal x(t)");
        plotGraphs.setTicUnits(0.5,0.5);
        plotGraphs.plot();

        plotGraphs.setData(freq,signalFreqSpec);
        plotGraphs.setLabels("Frequency (Hz)","|FFT(X)|","Single-Sided Frequency Magnitude Spectrum");
        plotGraphs.setTicUnits(16,100);
        plotGraphs.plot();

        plotGraphs.setData(t2,reconstructedSignal);
        plotGraphs.setLabels("Sample Points","Amplitude","Result of IFFT");
        plotGraphs.setTicUnits(500,0.5);
        plotGraphs.plot();

        //convolution example
        // Symbolic Convolution
        System.out.println("Symbolic convolution Example =");
        Matrix<Double> coeficientMatrix=new DoubleMatrix(new Double[][]{{1.0,2.0,1.0}});
        Matrix<String> variables= StringMatrixOperations.getInstance().symbolicVariables("r",4);
        Matrix<String> stringCoefMatrix=new StringMatrix(StringMatrixOperations.getInstance().convertString(coeficientMatrix).getMatrix());
        Matrix<String> symbConvOut=SymbolicConvolution.getInstance().conv(variables,stringCoefMatrix);
        for (int i = 0; i < symbConvOut.getColumnLength(); i++) {
            String smartliteral = ParseStringsOperations.smartLiteral(ParseStringsOperations.parseStringValue(symbConvOut.getIndex(0, i)));
            symbConvOut.setIndex(0, i, smartliteral);
        }

        System.out.print("variables = "+variables);
        System.out.print("coefficients = "+stringCoefMatrix);
        System.out.print("Symbolic Convolution Result =");
        symbConvOut.transpose().print();
        System.out.println();

        //Double Convolution Example 1
        Matrix<Double> u1=new DoubleMatrix(new Double[][]{{1.0},{2.0},{1.0}});
        Matrix<Double> v1=new DoubleMatrix(new Double[][]{{1.0},{2.0},{1.0}});
        Matrix<Double> convResult=DoubleConvolution.getInstance().conv(u1.transpose(),v1.transpose());
        System.out.println("u1 = "+u1.transpose());
        System.out.println("v1 = "+v1.transpose());
        System.out.println("Convolution Result = "+convResult);

        //Double Convolution Example 2
        Matrix<Double> base=new DoubleMatrix(new Double[][]{{1.0,1.0}});
        Matrix<Double> result=DoubleConvolution.getInstance().conv(base,base);//2
        for (int i = 0; i < 4; i++) {
            result=DoubleConvolution.getInstance().conv(result,base);//3
        }
        result=DoubleConvolution.getInstance().conv(result,base);//3
        result=DoubleConvolution.getInstance().conv(result,base);//4
        result=DoubleConvolution.getInstance().conv(result,base);//5
        result=DoubleConvolution.getInstance().conv(result,base);//6






    }
}
