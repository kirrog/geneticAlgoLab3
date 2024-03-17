package lab2;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCrossover extends AbstractCrossover<double[]> {
    protected MyCrossover() {
        super(1);
    }

    protected List<double[]> mate(double[] p1, double[] p2, int i, Random random) {
        ArrayList<double[]> children = new ArrayList<double[]>();
        double[] c1 = new double[p1.length];
        double[] c2 = new double[p1.length];
        int split_position_start = random.nextInt(p1.length);
        int split_position_stop = random.nextInt(p1.length);
        if (split_position_start > split_position_stop){
            int temp = split_position_start;
            split_position_start = split_position_stop;
            split_position_stop = temp;
        }
        for (int j = 0; j < p1.length; j++) {
            if (j < split_position_start ||  j > split_position_stop) {
                c1[j] = p1[j];
                c2[j] = p2[j];
            } else {
                c1[j] = p2[j];
                c2[j] = p1[j];
            }
        }
        children.add(c1);
        children.add(c2);
        return children;
    }
}
