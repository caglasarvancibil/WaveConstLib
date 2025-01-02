package MOEAClasses;

import Data.Parameters;
import FirstGenerationWaveletConstruction.*;
import LinearAlgebra.DoubleMatrixOperations;
import MathOperators.DoubleOperators;
import WaveletPackage.Wavelet;
import WaveletPackage.WaveletConditions;
import WaveletPackage.WaveletOperations;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import java.util.ArrayList;
import java.util.List;

public class PolynomCrossover implements Variation {
    private final double probability;
    private final WaveletConstructionProblem problem;
    private WaveletConstruction waveletConstruction;

    public PolynomCrossover(double probability, WaveletConstructionProblem problem) {
        this.probability = probability;
        this.problem = problem;
        waveletConstruction=new WaveletConstruction<>() {};
        waveletConstruction.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        PRNG.setSeed(Parameters.getInstance().getRANDOM().nextLong());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Solution[] evolve(Solution[] solutions) {
        WaveletOperations<Double> waveletOperations= new WaveletOperations<>() {};
        waveletOperations.initializeOperators(DoubleOperators.getInstance(),
                DoubleMatrixOperations.getInstance());
        FilterType type=((PolynomialVariable)solutions[0].getVariable(0)).getWavelet().getFilterType();
        int filterOrder=((PolynomialVariable)solutions[0].getVariable(0)).getWavelet().getLoR().getColumnLength()/2;


        if (PRNG.nextDouble()<= probability){
            Solution[] results = new Solution[]{
                    new Solution(solutions[0].getNumberOfVariables(), solutions[0].getNumberOfObjectives()),
                    new Solution(solutions[0].getNumberOfVariables(), solutions[0].getNumberOfObjectives())};

            results[0].setVariable(0, solutions[0].getVariable(0));
            results[0].setVariable(1, solutions[1].getVariable(1));
            results[1].setVariable(0, solutions[1].getVariable(0));
            results[1].setVariable(1, solutions[0].getVariable(1));


        for (int k = 0; k < results.length; k++) {
            Wavelet<Double> wavelet;
            WaveletConditions<Double> waveletConditions;
            List<PiecewisePolynomialVariable> polynomialVariables=new ArrayList<>();
            boolean feasible=false;

            for (int j = 0; j < results[k] .getNumberOfVariables(); j++) {
                polynomialVariables.add(j, (PiecewisePolynomialVariable) ((PolynomialVariable)results[k].getVariable(j)).getPiecewisePolynomialVariable().copy());
                }

               wavelet=waveletConstruction.constructWavelet(polynomialVariables,filterOrder,type);

                if (wavelet.getLoR()!=null){
                    waveletConditions = waveletOperations.waveletFeasibility(wavelet);
                    feasible=(waveletConditions.isHaveVanishingMoments() & waveletConditions.isSquareIntegrable());
                }

                if (!feasible){
                    results[k]=problem.newSolution();
                }

        }
        return results;
        }

        return solutions;
    }
}
