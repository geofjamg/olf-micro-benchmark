package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.*;
import net.jafama.FastMath;

public class BusActivePowerTargetEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    private final BranchArray branchArray;
    private final StateArray stateArray;

    public BusActivePowerTargetEquationArray(BranchArray branchArray, StateArray stateArray) {
        this.branchArray = branchArray;
        this.stateArray = stateArray;
    }

    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    void eval(double[] values) {
        for (int branchNum = 0; branchNum < branchArray.getLength(); branchNum++) {

            double theta1 = AbstractClosedBranchAcFlowEquationTerm.theta1(
                    branchArray.ksi[branchNum],
                    stateArray.ph[branchArray.bus1Num[branchNum]],
                    branchArray.a1[branchNum],
                    stateArray.ph[branchArray.bus2Num[branchNum]]);
            double theta2 = AbstractClosedBranchAcFlowEquationTerm.theta2(
                    branchArray.ksi[branchNum],
                    stateArray.ph[branchArray.bus1Num[branchNum]],
                    branchArray.a1[branchNum],
                    stateArray.ph[branchArray.bus2Num[branchNum]]);
            double sinTheta1 = FastMath.sin(theta1);
            double sinTheta2 = FastMath.sin(theta2);

            if (branchArray.bus1Num[branchNum] != -1 && branchArray.bus2Num[branchNum] != -1) {
                double p1 = ClosedBranchSide1ActiveFlowEquationTerm.p1(
                        branchArray.y[branchNum],
                        branchArray.sinKsi[branchNum],
                        branchArray.g1[branchNum],
                        stateArray.v[branchArray.bus1Num[branchNum]],
                        branchArray.r1[branchNum],
                        stateArray.v[branchArray.bus2Num[branchNum]],
                        sinTheta1);
                values[branchArray.bus1Num[branchNum]] += p1;

                double p2 = ClosedBranchSide2ActiveFlowEquationTerm.p2(
                        branchArray.y[branchNum],
                        branchArray.sinKsi[branchNum],
                        branchArray.g2[branchNum],
                        stateArray.v[branchArray.bus1Num[branchNum]],
                        branchArray.r1[branchNum],
                        stateArray.v[branchArray.bus2Num[branchNum]],
                        sinTheta2);
                values[branchArray.bus2Num[branchNum]] += p2;
            }
        }
    }
}
