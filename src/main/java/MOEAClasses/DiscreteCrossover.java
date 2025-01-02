package MOEAClasses;

import Data.Parameters;
import FirstGenerationWaveletConstruction.FilterType;
import FirstGenerationWaveletConstruction.WaveletConstruction;
import LinearAlgebra.DoubleMatrixOperations;
import LinearAlgebra.Matrix;
import MathOperators.DoubleOperators;
import WaveletPackage.Wavelet;
import WaveletPackage.WaveletConditions;
import WaveletPackage.WaveletOperations;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class DiscreteCrossover implements Variation {

    private final double probability;
    private final WaveletConstructionProblem problem;
    private WaveletConstruction waveletConstruction;

    public DiscreteCrossover(double probability, WaveletConstructionProblem problem) {
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
        FilterType type=((DiscreteVariable)solutions[0].getVariable(0)).getWavelet().getFilterType();
        int filterOrder=((DiscreteVariable)solutions[0].getVariable(0)).getWavelet().getLoR().getColumnLength()/2;
        if (PRNG.nextDouble()<= probability) {
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
                Matrix<Double> base=DoubleMatrixOperations.getInstance().zeros(1,2);
                boolean feasible=false;

                for (int j = 0; j < results[k] .getNumberOfVariables(); j++) {
                    base.setIndex(0,j,((DiscreteVariable) results[k].getVariable(j)).getDiscreteSampleVariable().copy().getVariable());
                }

                wavelet=waveletConstruction.constructWavelet(base,filterOrder,type);

                if (wavelet.getLoR()!=null){
                    waveletConditions = waveletOperations.waveletFeasibility(wavelet);
                    feasible=(waveletConditions.isHaveVanishingMoments() & waveletConditions.isSquareIntegrable());
                }

                if (!feasible){
                    results[k]=problem.newSolution();
                }

            }

            return results;
        }else{
            return solutions;
        }

    }
}
