package lab2;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;



public class MyAlg {
    public static double mutationFactor = 9.0;
    public static double mutationEntityProbability = 0.1;
    public static double mutationFieldProbability = 0.0;
    public static double crossoverFactor = 5.0;
    public static double crossoverProb = 0.0;


    public static void main(String[] args) {
        int dimension = 50; // dimension of problem
        int populationSize = 100; // size of population max 100
        int generations = 100; // number of generations max 10000
        research(dimension, populationSize, generations);
//        double result = calcAverage(dimension, populationSize, generations);
//        System.out.println("Average result: " + result);
    }

    private static void research(int dimension, int populationSize, int generations) {
        double best_result = 0.0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    for (int l = 0; l < 10; l++) {
                        for (int m = 0; m < 10; m++) {
                            mutationFactor = i;
                            mutationEntityProbability = (double) j / 10;
                            mutationFieldProbability = (double) k / 10;
                            crossoverFactor = l;
                            crossoverProb = (double) m / 10;
                            double result = calcAverage(dimension, populationSize, generations);
                            if (result > best_result) {
                                best_result = result;
                                System.out.println();
                                System.out.println("MF: " + mutationFactor +
                                        " MEP: " + mutationEntityProbability +
                                        " MFP: " + mutationFieldProbability +
                                        " CF: " + crossoverFactor +
                                        " CP: " + crossoverProb);
                                System.out.println("Average result: " + best_result);
                            } else {
                                System.out.print("*");
                            }
                        }
                    }
                }
            }
        }
    }

    private static double calcAverage(int dimension, int populationSize, int generations) {
        double accum = 0.0;
        int num_of_loops = 10;
        for (int i = 0; i < num_of_loops; i++) {
            accum += calc(dimension, populationSize, generations);
        }
        accum /= num_of_loops;
        return accum;
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
//                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tBest solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
//                System.out.println("\tPop size = " + populationData.getPopulationSize());
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        algorithm.evolve(populationSize, 1, terminate);
        return ((FitnessFunction) evaluator).getBest_result();
    }
}
