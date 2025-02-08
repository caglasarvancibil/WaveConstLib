package Examples;

import FirstGenerationWaveletConstruction.*;
import LinearAlgebra.DoubleMatrixOperations;
import MathOperators.DoubleOperators;
import WaveletPackage.Wavelet;
import WaveletPackage.WaveletConditions;
import WaveletPackage.WaveletOperations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * For the given parameters, Randomly Construction of 100000 feasible:
 * -Nearly symmetric wavelet function takes 4731 seconds (wavelet filter oder 15)
 * -Minimum phase wavelet function takes 7142 seconds (wavelet filter oder 20)
 * Default seed is 3.
 */
public class LimitsOfConstructionTests {
    public static void main(String[] args) {

        WaveletConstruction<Double> waveletConstruction=new WaveletConstruction<>() {};
        WaveletOperations<Double> waveletOperations= new WaveletOperations<>() {};

        waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        waveletConstruction.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());

        int numOfTrials=500;
        int numOfConstruction=100;
        int waveletOrder=15;
        FilterType filterType=FilterType.SYMMETRIC;

        IBasisFilterVariable ucRootVariable=new UCRootVariable();
        List<VariableBoundary> variableBoundaries=new ArrayList<>();
        int boundVal;
        if (waveletOrder%2==0){
            boundVal=waveletOrder/2;
        }else{
            boundVal=((waveletOrder-1)/2)+1;
        }
        for (int i = 0; i < boundVal; i++) {
            variableBoundaries.add(new VariableBoundary(90.0,270.0));
        }

//        IBasisFilterVariable discreteSampleVariable=new DiscreteSampleVariable();
//        List<VariableBoundary> variableBoundaries3=new ArrayList<>();
//        variableBoundaries3.add(new VariableBoundary(-1.0,1.0));
//        variableBoundaries3.add(new VariableBoundary(-1.0,1.0));
        int cntr=0;
        String genStart= LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(genStart);
        for (int i = 0; i < numOfConstruction; i++) {
            ConstructionObject constructionObject = waveletConstruction
                    .constructFeasibleWavelet(BasisVariableType.DISCRETE, ucRootVariable,
                            variableBoundaries, waveletOrder, filterType, numOfTrials);
            Wavelet wavelet=constructionObject.getWavelet();

            WaveletConditions<Double> waveletConditions=waveletOperations.waveletFeasibility(wavelet);
            if (waveletConditions.isHaveVanishingMoments()&waveletConditions.isSquareIntegrable()&waveletConditions.isCompactlySupported()){
                cntr++;
            }
        }
        System.out.println(cntr);
        String genEnd= LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(genEnd);
    }


}
