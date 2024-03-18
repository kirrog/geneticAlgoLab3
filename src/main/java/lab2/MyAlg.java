package lab2;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

class CalculationServiceSupplier implements Supplier<ResultInstance> {
    private final double mutationFactor;
    private final double mutationEntityProbability;
    private final double mutationFieldProbability;
    private final double crossoverFactor;
    private final double crossoverProb;
    private final int dimension;
    private final int populationSize;
    private final int generations;

    public CalculationServiceSupplier(double mutationFactor,
                                      double mutationEntityProbability,
                                      double mutationFieldProbability,
                                      double crossoverFactor,
                                      double crossoverProb,
                                      int dimension,
                                      int populationSize,
                                      int generations) {
        this.mutationFactor = mutationFactor;
        this.mutationEntityProbability = mutationEntityProbability;
        this.mutationFieldProbability = mutationFieldProbability;
        this.crossoverFactor = crossoverFactor;
        this.crossoverProb = crossoverProb;
        this.dimension = dimension;
        this.populationSize = populationSize;
        this.generations = generations;
    }

    @Override
    public ResultInstance get() {
        MyAlg.mutationFactor = this.mutationFactor;
        MyAlg.mutationEntityProbability = this.mutationEntityProbability;
        MyAlg.mutationFieldProbability = this.mutationFieldProbability;
        MyAlg.crossoverFactor = this.crossoverFactor;
        MyAlg.crossoverProb = this.crossoverProb;
        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(this.dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessFunction evaluator = new FitnessFunction(this.dimension); // Fitness function

        EvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, this.populationSize, false, random);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
//                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
//                System.out.println("\tBest solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
//                System.out.println("\tPop size = " + populationData.getPopulationSize());
            }
        });

        TerminationCondition terminate = new GenerationCount(this.generations);
        algorithm.evolve(this.populationSize, 1, terminate);
        return new ResultInstance(this.mutationFactor,
                this.mutationEntityProbability,
                this.mutationEntityProbability,
                this.crossoverFactor,
                this.crossoverProb,
                evaluator.getBest_result());
    }

}

class ResultInstance {
    double mutationFactor;
    double mutationEntityProbability;
    double mutationFieldProbability;
    double crossoverFactor;
    double crossoverProb;
    double result;

    public ResultInstance(double mutationFactor,
                          double mutationEntityProbability,
                          double mutationFieldProbability,
                          double crossoverFactor,
                          double crossoverProb,
                          double result) {
        this.mutationFactor = mutationFactor;
        this.mutationEntityProbability = mutationEntityProbability;
        this.mutationFieldProbability = mutationFieldProbability;
        this.crossoverFactor = crossoverFactor;
        this.crossoverProb = crossoverProb;
        this.result = result;
    }
}

public class MyAlg {
    public static double mutationFactor = 4.0;
    public static double mutationEntityProbability = 0.5;
    public static double mutationFieldProbability = 0.5;
    public static double crossoverFactor = 7.0;
    public static double crossoverProb = 0.9;


    public static void main(String[] args) {
        int dimension = 50; // dimension of problem
        int populationSize = 100; // size of population max 100
        int generations = 10000; // number of generations max 10000
//        research(dimension, populationSize, generations);
        double result = calcAverage(dimension, populationSize, generations);
        System.out.println("Average result: " + result);
    }

    private static void research(int dimension, int populationSize, int generations) {
        ExecutorService cachedThreadPool = Executors.newScheduledThreadPool(20);
        List<CompletableFuture<ResultInstance>> allTasks = new ArrayList<>();
        final double[] best_result = {0.0};
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
                            CompletableFuture<ResultInstance> additionTask = CompletableFuture.supplyAsync(new CalculationServiceSupplier(
                                    i,
                                    (double) j / 10,
                                    (double) k / 10, l,
                                    (double) m / 10,
                                    dimension,
                                    populationSize,
                                    generations
                            ), cachedThreadPool);
                            allTasks.add(additionTask);
                        }
                    }
                }
            }
        }
        for (CompletableFuture<ResultInstance> task : allTasks) {
            task.whenComplete((result, exception)
                    ->
            {
                if (exception == null) {

                    if (result.result > best_result[0]) {
                        best_result[0] = result.result;
                        System.out.println();
                        System.out.println("MF: " + result.mutationFactor +
                                " MEP: " + result.mutationEntityProbability +
                                " MFP: " + result.mutationFieldProbability +
                                " CF: " + result.crossoverFactor +
                                " CP: " + result.crossoverProb);
                        System.out.println("Average result: " + best_result[0]);
                    } else {
                        System.out.print("*");
                    }
                } else {
                    task.completeExceptionally(exception);
                    System.out.println(exception.getMessage());
                }
            });
        }
        cachedThreadPool.shutdown();
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
