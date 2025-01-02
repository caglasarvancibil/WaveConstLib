package MOEAClasses;

import Data.Parameters;
import FirstGenerationWaveletConstruction.FilterType;
import FirstGenerationWaveletConstruction.WaveletConstruction;
import LinearAlgebra.ComplexMatrix;
import LinearAlgebra.DoubleMatrixOperations;
import MathOperators.ComplexNumber;
import MathOperators.DoubleOperators;
import WaveletPackage.Wavelet;
import WaveletPackage.WaveletConditions;
import WaveletPackage.WaveletOperations;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class RootsCrossover implements Variation {

    private final double probability;
    private final WaveletConstructionProblem problem;
    private WaveletConstruction waveletConstruction;

    public RootsCrossover(double probability, WaveletConstructionProblem problem) {
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
        FilterType type=((RootsVariable)solutions[0].getVariable(0)).getWavelet().getFilterType();
        int filterOrder=((RootsVariable)solutions[0].getVariable(0)).getWavelet().getLoR().getColumnLength()/2;


        if (PRNG.nextDouble()<= probability){
            Solution[] offsprings = new Solution[]{
                    new Solution(solutions[0].getNumberOfVariables(), solutions[0].getNumberOfObjectives()),
                    new Solution(solutions[0].getNumberOfVariables(), solutions[0].getNumberOfObjectives())};
            int xoverPoint;
          // genome change
           if (filterOrder%2==0){
               xoverPoint=filterOrder/2;
               int initial=0;
               int sol1=0;
               int sol2=1;
               int end=2;
               for (int i = 0; i < xoverPoint; i++) {
                   for (int j = initial; j < end; j++) {
                       offsprings[0].setVariable(j, solutions[sol1].getVariable(j));
                       offsprings[1].setVariable(j, solutions[sol2].getVariable(j));
                   }
                   initial=initial+2;
                   end=end+2;
                   if (sol1==0){
                       sol1=1;
                       sol2=0;
                   }else{
                       sol1=0;
                       sol2=1;
                   }

               }
           }else{
               xoverPoint=(filterOrder-1)/2;
               int initial=0;
               int sol1=0;
               int sol2=1;
               int end=2;
               for (int i = 0; i < xoverPoint; i++) {
                   for (int j = initial; j < end; j++) {
                       offsprings[0].setVariable(j, solutions[sol1].getVariable(j));
                       offsprings[1].setVariable(j, solutions[sol2].getVariable(j));
                   }
                   initial=initial+2;
                   end=end+2;
                   if (sol1==0){
                       sol1=1;
                       sol2=0;
                   }else{
                       sol1=0;
                       sol2=1;
                   }

               }
               offsprings[0].setVariable(filterOrder-1, solutions[0].getVariable(filterOrder-1));
               offsprings[1].setVariable(filterOrder-1, solutions[1].getVariable(filterOrder-1));
           }

            for (int k = 0; k < offsprings.length; k++) {
                Wavelet<Double> wavelet;
                WaveletConditions<Double> waveletConditions;
                ComplexMatrix ucRootBase=new ComplexMatrix(new ComplexNumber[1][filterOrder]);
                boolean feasible=false;

                for (int j = 0; j < offsprings[k] .getNumberOfVariables(); j++) {
                    ucRootBase.setIndex (0,j,((RootsVariable) offsprings[k].getVariable(j)).getUcRootVariable().copy().getVariable());
                }

                wavelet=waveletConstruction.constructWavelet(ucRootBase,filterOrder,type);

                if (wavelet.getLoR()!=null){
                    waveletConditions = waveletOperations.waveletFeasibility(wavelet);
                    feasible=(waveletConditions.isHaveVanishingMoments() & waveletConditions.isSquareIntegrable());
                }

                if (!feasible){
                    offsprings[k]=problem.newSolution();
                }

            }
            return offsprings;
        }
        else
        {
            return solutions;
        }

    }
}
