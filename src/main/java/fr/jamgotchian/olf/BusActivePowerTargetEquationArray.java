package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.*;
import com.powsybl.openloadflow.equations.StateVector;
import net.jafama.FastMath;

import java.util.Arrays;

public class BusActivePowerTargetEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    private final BranchArray branchArray;
    private final VariableArray variableArray;
    private final StateVector stateVector;

    private final int[] active;

    public BusActivePowerTargetEquationArray(int busCount, BranchArray branchArray, VariableArray variableArray,
                                             StateVector stateVector) {
        this.branchArray = branchArray;
        this.variableArray = variableArray;
        this.stateVector = stateVector;
        active = new int[busCount];
        Arrays.fill(active, 1);
    }

    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    public boolean isActive(int num) {
        return active[num] == 0;
    }

    public void setActive(int num, boolean active) {
        this.active[num] = active ? 1 : 0;
    }

    void eval(double[] values) {
        double[] state = stateVector.get();
        for (int branchNum = 0; branchNum < branchArray.getLength(); branchNum++) {
            double ph1 = state[variableArray.phRow[branchArray.bus1Num[branchNum]]];
            double ph2 = state[variableArray.phRow[branchArray.bus2Num[branchNum]]];

            double theta1 = AbstractClosedBranchAcFlowEquationTerm.theta1(
                    branchArray.ksi[branchNum],
                    ph1,
                    branchArray.a1[branchNum],
                    ph2);
            double theta2 = AbstractClosedBranchAcFlowEquationTerm.theta2(
                    branchArray.ksi[branchNum],
                    ph1,
                    branchArray.a1[branchNum],
                    ph2);
            double sinTheta1 = FastMath.sin(theta1);
            double sinTheta2 = FastMath.sin(theta2);

            if (branchArray.bus1Num[branchNum] != -1 && branchArray.bus2Num[branchNum] != -1) {
                double v1 = state[variableArray.vRow[branchArray.bus1Num[branchNum]]];
                double v2 = state[variableArray.vRow[branchArray.bus2Num[branchNum]]];

                double p1 = ClosedBranchSide1ActiveFlowEquationTerm.p1(
                        branchArray.y[branchNum],
                        branchArray.sinKsi[branchNum],
                        branchArray.g1[branchNum],
                        v1,
                        branchArray.r1[branchNum],
                        v2,
                        sinTheta1);
                values[branchArray.bus1Num[branchNum]] += p1;

                double p2 = ClosedBranchSide2ActiveFlowEquationTerm.p2(
                        branchArray.y[branchNum],
                        branchArray.sinKsi[branchNum],
                        branchArray.g2[branchNum],
                        v1,
                        branchArray.r1[branchNum],
                        v2,
                        sinTheta2);
                values[branchArray.bus2Num[branchNum]] += p2;
            }
        }

        for (int busNum = 0; busNum < values.length; busNum++) {
            values[busNum] *= active[busNum];
        }
    }
}
