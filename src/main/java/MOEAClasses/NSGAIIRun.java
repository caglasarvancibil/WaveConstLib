package MOEAClasses;


import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.BasisVariableType;
import FirstGenerationWaveletConstruction.FilterType;
import WaveletPackage.Wavelet;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.TournamentSelection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class NSGAIIRun {
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
        TournamentSelection tournamentSelection=new TournamentSelection(2);
        NondominatedSortingPopulation nondominatedSortingPopulation=new NondominatedSortingPopulation();
        NSGAII nsgaii=new NSGAII(problem,popSize,nondominatedSortingPopulation,null,tournamentSelection,crossover,initPOP);

        for (int i = 0; i < numberOfGenerations; i++) {
            System.out.println("Iteration :" + i);
            String genStart= LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(genStart);
            nsgaii.run(popSize);
            nsgaii.getResult().display();
            String genEnd=LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            System.out.println(genEnd);

        }
        List<Wavelet<Double>> waveletList=new ArrayList<>();

        if (basisVariableType==BasisVariableType.POLYNOMIAL){
            for (int i = 0; i < nsgaii.getResult().size(); i++) {
                PolynomialVariable var = (PolynomialVariable) nsgaii.getResult().get(i).getVariable(0);
                waveletList.add(var.getWavelet());
            }

        } else if (basisVariableType==BasisVariableType.UCROOT) {
            for (int i = 0; i < nsgaii.getResult().size(); i++) {
                RootsVariable var = (RootsVariable) nsgaii.getResult().get(i).getVariable(0);
                waveletList.add(var.getWavelet());
            }
        } else if (basisVariableType==BasisVariableType.DISCRETE) {
            for (int i = 0; i < nsgaii.getResult().size(); i++) {
                DiscreteVariable var = (DiscreteVariable) nsgaii.getResult().get(i).getVariable(0);
                waveletList.add(var.getWavelet());
            }
        }
        return waveletList;
    }
}
