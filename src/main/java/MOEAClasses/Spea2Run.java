package MOEAClasses;

import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.BasisVariableType;
import FirstGenerationWaveletConstruction.FilterType;
import WaveletPackage.Wavelet;
import org.moeaframework.algorithm.SPEA2;
import org.moeaframework.core.Variation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Spea2Run {
    public  List<Wavelet<Double>>  Run(int numberOfVariables,
                     int numberOfObjectives,
                     double crossProbobality,
                     int popSize,
                     int numberOfGenerations,
                     List<SignalDataBase<Double>> signalDataBaseList,
                     int filterOrder,
                     FilterType filterType,
                     BasisVariableType basisVariableType){


        WaveletConstructionProblem problem=new WaveletConstructionProblem(numberOfVariables,numberOfObjectives,signalDataBaseList,filterOrder, filterType,basisVariableType);
        RandomizedInitialPopulation initPOP = new RandomizedInitialPopulation(problem);
        Variation crossover=null;
        if (basisVariableType==BasisVariableType.POLYNOMIAL){
            crossover=new PolynomCrossover(crossProbobality,problem);
        } else if (basisVariableType==BasisVariableType.UCROOT) {
            crossover=new RootsCrossover(crossProbobality,problem);
        } else if (basisVariableType==BasisVariableType.DISCRETE) {
            crossover=new DiscreteCrossover(crossProbobality,problem);
        }
        SPEA2 spea2= new SPEA2(problem,popSize,initPOP,crossover,popSize,1);

        //Run SPEA2
        for (int i = 0; i < numberOfGenerations; i++) {
            System.out.println("Iteration :" + i);
            String genStart= LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(genStart);
            spea2.run(popSize);
            spea2.getResult().display();
            String genEnd=LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(genEnd);

        }
        List<Wavelet<Double>> waveletList=new ArrayList<>();

        if (basisVariableType==BasisVariableType.POLYNOMIAL){
            for (int i = 0; i < spea2.getResult().size(); i++) {
                PolynomialVariable var = (PolynomialVariable) spea2.getResult().get(i).getVariable(0);
                waveletList.add(var.getWavelet());
            }

        } else if (basisVariableType==BasisVariableType.UCROOT) {
           for (int i = 0; i < spea2.getResult().size(); i++) {
               RootsVariable var = (RootsVariable) spea2.getResult().get(i).getVariable(0);
               waveletList.add(var.getWavelet());
            }
        }else if (basisVariableType==BasisVariableType.DISCRETE) {
            for (int i = 0; i < spea2.getResult().size(); i++) {
                DiscreteVariable var = (DiscreteVariable) spea2.getResult().get(i).getVariable(0);
                waveletList.add(var.getWavelet());
            }
        }
        return waveletList;

    }
}
