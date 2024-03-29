package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.PiModel;
import com.powsybl.openloadflow.util.Evaluable;
import net.jafama.FastMath;

import java.util.List;

public class BranchVector implements ElementVector {

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

    final int[] status;

    final double[] p1;
    final double[] p2;
    final double[] q1;
    final double[] q2;
    final double[] i1;
    final double[] i2;

    final double[] dp1dv1;
    final double[] dp1dv2;
    final double[] dp1dph1;
    final double[] dp1dph2;
    final double[] dp1da1;
    final double[] dp1dr1;

    final double[] dq1dv1;
    final double[] dq1dv2;
    final double[] dq1dph1;
    final double[] dq1dph2;
    final double[] dq1da1;
    final double[] dq1dr1;

    final double[] dp2dv1;
    final double[] dp2dv2;
    final double[] dp2dph1;
    final double[] dp2dph2;
    final double[] dp2da1;
    final double[] dp2dr1;

    final double[] dq2dv1;
    final double[] dq2dv2;
    final double[] dq2dph1;
    final double[] dq2dph2;
    final double[] dq2da1;
    final double[] dq2dr1;

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
        status = new int[size];

        p1 = new double[size];
        p2 = new double[size];
        q1 = new double[size];
        q2 = new double[size];
        i1 = new double[size];
        i2 = new double[size];

        dp1dv1 = new double[size];
        dp1dv2 = new double[size];
        dp1dph1 = new double[size];
        dp1dph2 = new double[size];
        dp1da1 = new double[size];
        dp1dr1 = new double[size];

        dq1dv1 = new double[size];
        dq1dv2 = new double[size];
        dq1dph1 = new double[size];
        dq1dph2 = new double[size];
        dq1da1 = new double[size];
        dq1dr1 = new double[size];

        dp2dv1 = new double[size];
        dp2dv2 = new double[size];
        dp2dph1 = new double[size];
        dp2dph2 = new double[size];
        dp2da1 = new double[size];
        dp2dr1 = new double[size];

        dq2dv1 = new double[size];
        dq2dv2 = new double[size];
        dq2dph1 = new double[size];
        dq2dph2 = new double[size];
        dq2da1 = new double[size];
        dq2dr1 = new double[size];

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
            status[i] = branch.isDisabled() ? 0 : 1;
        }
    }

    @Override
    public int getSize() {
        return status.length;
    }

    @Override
    public boolean isActive(int elementNum) {
        return status[elementNum] == 1;
    }

    public Evaluable getP1(int num) {
        return () -> p1[num];
    }

    public Evaluable getQ1(int num) {
        return () -> q1[num];
    }

    public Evaluable getI1(int num) {
        return () -> i1[num];
    }

    public Evaluable getP2(int num) {
        return () -> p2[num];
    }

    public Evaluable getQ2(int num) {
        return () -> q2[num];
    }

    public Evaluable getI2(int num) {
        return () -> i2[num];
    }
}
