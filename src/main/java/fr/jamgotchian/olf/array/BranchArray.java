package fr.jamgotchian.olf.array;

import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.PiModel;
import net.jafama.FastMath;

import java.util.List;

public class BranchArray {

    final int length;

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

    public BranchArray(List<LfBranch> branches) {
        length = branches.size();
        bus1Num = new int[length];
        bus2Num = new int[length];
        y = new double[length];
        ksi = new double[length];
        cosKsi = new double[length];
        sinKsi = new double[length];
        g1 = new double[length];
        g2 = new double[length];
        b1 = new double[length];
        b2 = new double[length];
        r1 = new double[length];
        a1 = new double[length];
        active = new int[length];
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
            active[i] = 1;
        }
    }

    public int getLength() {
        return length;
    }
}
