package lab2;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.List;
import java.util.Random;

public class MyMutation implements EvolutionaryOperator<double[]> {
    private static double factor = 4.0;

    public List<double[]> apply(List<double[]> population, Random random) {
        for (int i = 0; i < population.size(); i++) {
            if (random.nextDouble() > 0.5) {
                double[] entity = population.get(i);
                for (int j = 0; j < entity.length; j++) {
                    if (random.nextDouble() > 0.3) {
                        entity[j] = (entity[j] + (((random.nextDouble() * 10.0) - 5.0) / (factor))) / (1 + (1 / factor));
                    }
                }
            }
        }
        return population;
    }
}
