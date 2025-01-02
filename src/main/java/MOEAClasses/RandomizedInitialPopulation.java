package MOEAClasses;


import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;


public class RandomizedInitialPopulation implements Initialization {

    protected final Problem problem;


    public RandomizedInitialPopulation(Problem problem) {
        this.problem = problem;
    }

    @Override
    public Solution[] initialize(int populationSize) {

        Solution[] initialPopulation=new Solution[populationSize];

        for (int i = 0; i < populationSize; i++) {
            Solution solution= problem.newSolution();
            initialPopulation[i] = solution;
        }

        return initialPopulation;
    }

}
