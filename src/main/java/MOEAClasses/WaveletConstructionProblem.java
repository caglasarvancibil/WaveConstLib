package MOEAClasses;


import Data.SignalDataBase;
import FirstGenerationWaveletConstruction.*;

import LinearAlgebra.DoubleMatrixOperations;
import MathOperators.DoubleOperators;
import WaveletPackage.*;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class WaveletConstructionProblem extends AbstractProblem {
    List<SignalDataBase<Double>> signalDataBaseList;
    List<FrequencyPair> frequencyPairList;
    List<TimeInterval> timeIntervalList;
    AbstractAnalyzeWavelet<Double> analyzeWavelet;
    WaveletConstruction waveletConstruction;
    WaveletOperations<Double> waveletOperations;
    int filterOrder;
    FilterType type;
    PiecewisePolynomialVariable piecewisePolynomialVariable;
    UCRootVariable ucRootVariable;
    DiscreteSampleVariable discreteSampleVariable;
    BasisVariableType basisVariableType;
    public WaveletConstructionProblem(int numberOfVariables, int numberOfObjectives, List<SignalDataBase<Double>> signalDataBaseList,int filterOrder,FilterType filterType,BasisVariableType basisVariableType) {

        super(numberOfVariables, numberOfObjectives);
        this.signalDataBaseList=signalDataBaseList;
        this.filterOrder=filterOrder;
        this.type=filterType;
        this.basisVariableType=basisVariableType;

        //Analyze constructed wavelet function
        this.analyzeWavelet=new AbstractAnalyzeWavelet<>() {};
        this.analyzeWavelet.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());

        //Scale Dependent Power Parameters
        frequencyPairList=new ArrayList<>();
        frequencyPairList.add(new FrequencyPair(8.0,16.0));
        timeIntervalList=new ArrayList<>();
        timeIntervalList.add(new TimeInterval(0.65,0.87));

       waveletConstruction=new WaveletConstruction<>() {};
       waveletConstruction.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());
       waveletOperations= new WaveletOperations<>() {};
       waveletOperations.initializeOperators(DoubleOperators.getInstance(), DoubleMatrixOperations.getInstance());


    }

    @Override
    public void evaluate(Solution solution) {

       Wavelet<Double> wavelet=new Wavelet<>();
        if (basisVariableType==BasisVariableType.POLYNOMIAL) {
            wavelet=((PolynomialVariable) solution.getVariable(0)).getWavelet();
        } else if (basisVariableType==BasisVariableType.UCROOT) {
             wavelet=((RootsVariable) solution.getVariable(0)).getWavelet();
        }else if (basisVariableType==BasisVariableType.DISCRETE) {
            wavelet=((DiscreteVariable) solution.getVariable(0)).getWavelet();

        }



        double [] scaledepent=analyzeWavelet.scaleDependent(signalDataBaseList,wavelet,frequencyPairList,timeIntervalList);
        double value= analyzeWavelet.scaleDependentCommonFrequency(signalDataBaseList,wavelet,new FrequencyPair(8.0,16.0),new TimeInterval(0.65,0.87),"O");

        DecimalFormat df=new DecimalFormat("##");
        DecimalFormat df2=new DecimalFormat("#.#");

        // Apply penalty to nonfeasible wavelets objective values
        WaveletConditions<Double> waveletConditions= waveletOperations.waveletFeasibility(wavelet);
        if (!(waveletConditions.isCompactlySupported()&waveletConditions.isSquareIntegrable()&waveletConditions.isHaveVanishingMoments())){
            solution.setObjective(0,Double.parseDouble(df.format(scaledepent[0]).replace(',','.')));
            solution.setObjective(1, Double.parseDouble(df2.format(value).replace(',','.')));

        }else {
            solution.setObjective(0, Double.parseDouble(df.format(-1 * scaledepent[0]).replace(',', '.')));
            solution.setObjective(1, Double.parseDouble(df2.format(value).replace(',', '.')));
         }
    }

    @Override
    public Solution newSolution() {

        Solution solution= new Solution(numberOfVariables,numberOfObjectives);
        List<VariableBoundary> variableBoundaries;
        ConstructionObject constructionObject;

        if (basisVariableType==BasisVariableType.POLYNOMIAL) {
            //Piecewise Polynomial Variable Parameters
            variableBoundaries= new ArrayList<>();
            variableBoundaries.add(new VariableBoundary(0.1, 0.8));
            variableBoundaries.add(new VariableBoundary(-0.8, 0.1));
            constructionObject = waveletConstruction.constructFeasibleWavelet(BasisVariableType.POLYNOMIAL, piecewisePolynomialVariable,
                    variableBoundaries, filterOrder, type, 100);

            for (int j = 0; j < solution.getNumberOfVariables(); j++) {
                solution.setVariable(j, new PolynomialVariable((PiecewisePolynomialVariable) constructionObject.getBasisFilterVariableList().get(j)));
            }
            ((PolynomialVariable) solution.getVariable(0)).setWavelet(constructionObject.getWavelet());

        } else if (basisVariableType==BasisVariableType.UCROOT) {
            //UC ROOTS Variable Parameters
            variableBoundaries=new ArrayList<>();
            if (filterOrder%2==0){
                for (int i = 0; i < (filterOrder/2)-1; i++) {
                    variableBoundaries.add(new VariableBoundary(90.0,270.0));
                }
                variableBoundaries.add(new VariableBoundary(179.9,180.0));
            }else {

                for (int i = 0; i < (filterOrder-1)/2; i++) {
                    variableBoundaries.add(new VariableBoundary(90.0,270.0));
                }
                variableBoundaries.add(new VariableBoundary(179.9,180.0));

            }
            constructionObject= waveletConstruction.constructFeasibleWavelet(BasisVariableType.UCROOT, ucRootVariable,
                    variableBoundaries, filterOrder,type,100);

            for (int j = 0; j < solution.getNumberOfVariables(); j++) {
                solution.setVariable(j,new RootsVariable((UCRootVariable) constructionObject.getBasisFilterVariableList().get(j)));
            }
            ((RootsVariable)solution.getVariable(0)).setWavelet(constructionObject.getWavelet());


        } else if (basisVariableType==BasisVariableType.DISCRETE) {
            //Discrete Sample Variable Parameters
            variableBoundaries= new ArrayList<>();
            variableBoundaries.add(new VariableBoundary(-1.0, 1.0));
            variableBoundaries.add(new VariableBoundary(-1.0, 1.0));
            constructionObject= waveletConstruction.constructFeasibleWavelet(BasisVariableType.DISCRETE, discreteSampleVariable,
                            variableBoundaries, filterOrder,type,100);

            for (int j = 0; j < solution.getNumberOfVariables(); j++) {
                solution.setVariable(j,new DiscreteVariable((DiscreteSampleVariable) constructionObject.getBasisFilterVariableList().get(j)));
            }
            ((DiscreteVariable)solution.getVariable(0)).setWavelet(constructionObject.getWavelet());


        }

        return solution;
    }
}
