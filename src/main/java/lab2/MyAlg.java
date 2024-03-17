package lab2;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MyAlg {

    public static void main(String[] args) {
        int dimension = 2; // dimension of problem
        int populationSize = 10; // size of population
        int generations = 10; // number of generations
        int num_of_loops = 100000;

        double accum = 0.0;

        for (int i = 0; i < num_of_loops; i++) {
            accum += calc(dimension, populationSize, generations);
        }
        accum /= num_of_loops;

        System.out.println("**********************************************************");
        System.out.println("Average result: " + accum);
    }

    private static double calc(int dimension, int populationSize, int generations) {
        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new FitnessFunction(dimension); // Fitness function

        EvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, populationSize, false, random);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
                System.out.println("\tBest solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
                System.out.println("\tPop size = " + populationData.getPopulationSize());
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        algorithm.evolve(populationSize, 1, terminate);
        return ((FitnessFunction) evaluator).getBest_result();
    }
}
