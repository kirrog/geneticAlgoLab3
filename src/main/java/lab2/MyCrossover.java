package lab2;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCrossover extends AbstractCrossover<double[]> {
    private static double factor = 6.5;
    private static double prob = 0.95;
    private static double prob_choose = 0.5;

    protected MyCrossover() {
        super(1);
    }

    protected List<double[]> mate(double[] p1, double[] p2, int i, Random random) {
        ArrayList<double[]> children = new ArrayList<double[]>();
        double[] c1 = new double[p1.length];
        double[] c2 = new double[p2.length];

        for (int j = 0; j < p1.length; j++) {
            if (random.nextDouble() > prob) {
                c1[j] = (p1[j] + p2[j] + (((random.nextDouble() * 10.0) - 5.0) / (factor))) / (2 + (1 / factor));
            } else {
                if (random.nextDouble() > prob_choose) {
                    c1[j] = p1[j];
                } else {
                    c1[j] = p2[j];
                }
            }
            if (random.nextDouble() > prob) {
                c2[j] = (p1[j] + p2[j] + (((random.nextDouble() * 10.0) - 5.0) / (factor))) / (2 + (1 / factor));
            } else {
                if (random.nextDouble() > prob_choose) {
                    c1[j] = p1[j];
                } else {
                    c1[j] = p2[j];
                }
            }
        }
        children.add(c1);
        children.add(c2);
        return children;
    }
}
