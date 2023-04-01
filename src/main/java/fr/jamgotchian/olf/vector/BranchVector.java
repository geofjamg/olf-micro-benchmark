package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.PiModel;
import net.jafama.FastMath;

import java.util.List;

public class BranchVector {

    final int[] bus1Num;
    final int[] bus2Num;

    final double[] y;
    final double[] ksi;
    final double[] cosKsi;
    final double[] sinKsi;
    final double[] g1;
    final double[] g2;
    final double[] b1;
    final double[] b2;
    final double[] r1;
    final double[] a1;

    final int[] active;

    final double[] p1;
    final double[] p2;
    final double[] q1;
    final double[] q2;

    public BranchVector(List<LfBranch> branches) {
        int size = branches.size();
        bus1Num = new int[size];
        bus2Num = new int[size];
        y = new double[size];
        ksi = new double[size];
        cosKsi = new double[size];
        sinKsi = new double[size];
        g1 = new double[size];
        g2 = new double[size];
        b1 = new double[size];
        b2 = new double[size];
        r1 = new double[size];
        a1 = new double[size];
        active = new int[size];
        p1 = new double[size];
        p2 = new double[size];
        q1 = new double[size];
        q2 = new double[size];
        for (int i = 0; i < branches.size(); i++) {
            LfBranch branch = branches.get(i);
            LfBus bus1 = branch.getBus1();
            LfBus bus2 = branch.getBus2();
            bus1Num[i] = bus1 != null ? bus1.getNum() : -1;
            bus2Num[i] = bus2 != null ? bus2.getNum() : -1;
            PiModel piModel = branch.getPiModel();
            if (piModel.getR() == 0 && piModel.getX() == 0) {
                throw new IllegalArgumentException("Non impedant branch not supported: " + branch.getId());
            }
            y[i] = piModel.getY();
            ksi[i] = piModel.getKsi();
            cosKsi[i] = FastMath.cos(ksi[i]);
            sinKsi[i] = FastMath.sin(ksi[i]);
            b1[i] = piModel.getB1();
            b2[i] = piModel.getB2();
            g1[i] = piModel.getG1();
            g2[i] = piModel.getG2();
            r1[i] = piModel.getR1();
            a1[i] = piModel.getA1();
            active[i] = branch.isDisabled() ? 0 : 1;
        }
    }

    public int getSize() {
        return active.length;
    }
}
