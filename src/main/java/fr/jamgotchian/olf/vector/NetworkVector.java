package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AbstractClosedBranchAcFlowEquationTerm;
import com.powsybl.openloadflow.ac.equations.ClosedBranchSide1ActiveFlowEquationTerm;
import com.powsybl.openloadflow.ac.equations.ClosedBranchSide2ActiveFlowEquationTerm;
import com.powsybl.openloadflow.equations.StateVector;
import com.powsybl.openloadflow.network.LfNetwork;
import net.jafama.FastMath;

import java.util.Arrays;
import java.util.Objects;

public class NetworkVector {

    private final BusVector busVector;
    private final BranchVector branchVector;

    public NetworkVector(LfNetwork network) {
        Objects.requireNonNull(network);
        this.busVector = new BusVector(network.getBuses());
        this.branchVector = new BranchVector(network.getBranches());
    }

    public BusVector getBusVector() {
        return busVector;
    }

    public BranchVector getBranchVector() {
        return branchVector;
    }

    public void updateState(QuantityVector quantityVector, StateVector stateVector) {
        double[] state = stateVector.get();
        Arrays.fill(busVector.p, 0);
        Arrays.fill(busVector.q, 0);
        for (int branchNum = 0; branchNum < branchVector.getSize(); branchNum++) {
            if (branchVector.active[branchNum] == 1) {
                double ph1 = state[quantityVector.ph1Row[branchNum]];
                double ph2 = state[quantityVector.ph2Row[branchNum]];

                double theta1 = AbstractClosedBranchAcFlowEquationTerm.theta1(
                        branchVector.ksi[branchNum],
                        ph1,
                        branchVector.a1[branchNum],
                        ph2);
                double theta2 = AbstractClosedBranchAcFlowEquationTerm.theta2(
                        branchVector.ksi[branchNum],
                        ph1,
                        branchVector.a1[branchNum],
                        ph2);
                double sinTheta1 = FastMath.sin(theta1);
                double sinTheta2 = FastMath.sin(theta2);

                if (branchVector.bus1Num[branchNum] != -1 && branchVector.bus2Num[branchNum] != -1) {
                    double v1 = state[quantityVector.v1Row[branchNum]];
                    double v2 = state[quantityVector.v2Row[branchNum]];

                    branchVector.p1[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.p1(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            v1,
                            branchVector.r1[branchNum],
                            v2,
                            sinTheta1);
                    busVector.p[branchVector.bus1Num[branchNum]] += branchVector.p1[branchNum];

                    branchVector.p2[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.p2(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g2[branchNum],
                            v1,
                            branchVector.r1[branchNum],
                            v2,
                            sinTheta2);
                    busVector.p[branchVector.bus2Num[branchNum]] += branchVector.p2[branchNum];
                } else {
                    branchVector.p1[branchNum] = 0;
                    branchVector.p2[branchNum] = 0;
                }
            }
        }

        for (int busNum = 0; busNum < busVector.getSize(); busNum++) {
            busVector.p[busNum] *= busVector.active[busNum];
        }
    }
}
