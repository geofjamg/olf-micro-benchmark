package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.*;
import com.powsybl.openloadflow.equations.StateVector;
import com.powsybl.openloadflow.network.LfNetwork;
import net.jafama.DoubleWrapper;
import net.jafama.FastMath;

import java.util.Arrays;
import java.util.Objects;

public class NetworkVector {

    private static final double SQRT3 = FastMath.sqrt(3);

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

    public void updateState(VariableVector variableVector, StateVector stateVector) {
        double[] state = stateVector.get();
        Arrays.fill(busVector.p, 0);
        Arrays.fill(busVector.q, 0);
        var w = new DoubleWrapper();
        for (int branchNum = 0; branchNum < branchVector.getSize(); branchNum++) {
            if (branchVector.status[branchNum] == 1) {
                double ph1 = state[variableVector.ph1Row[branchNum]];
                double ph2 = state[variableVector.ph2Row[branchNum]];
                double a1 = variableVector.a1Row[branchNum] != -1 ? state[variableVector.a1Row[branchNum]]
                                                                  : branchVector.a1[branchNum];

                double theta1 = AbstractClosedBranchAcFlowEquationTerm.theta1(
                        branchVector.ksi[branchNum],
                        ph1,
                        a1,
                        ph2);
                double theta2 = AbstractClosedBranchAcFlowEquationTerm.theta2(
                        branchVector.ksi[branchNum],
                        ph1,
                        a1,
                        ph2);
                double sinTheta1 = FastMath.sinAndCos(theta1, w);
                double cosTheta1 = w.value;
                double sinTheta2 = FastMath.sinAndCos(theta2, w);
                double cosTheta2 = w.value;

                if (branchVector.bus1Num[branchNum] != -1 && branchVector.bus2Num[branchNum] != -1) {
                    double v1 = state[variableVector.v1Row[branchNum]];
                    double v2 = state[variableVector.v2Row[branchNum]];
                    double r1 = variableVector.r1Row[branchNum] != -1 ? state[variableVector.r1Row[branchNum]]
                                                                      : branchVector.r1[branchNum];

                    // p1

                    branchVector.p1[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.p1(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta1);
                    busVector.p[branchVector.bus1Num[branchNum]] += branchVector.p1[branchNum];

                    branchVector.dp1dv1[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.dp1dv1(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta1);
                    branchVector.dp1dv2[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.dp1dv2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            sinTheta1);
                    branchVector.dp1dph1[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.dp1dph1(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta1);
                    branchVector.dp1dph2[branchNum] = ClosedBranchSide1ActiveFlowEquationTerm.dp1dph2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta1);

                    // q1

                    branchVector.q1[branchNum] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.b1[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta1);
                    busVector.q[branchVector.bus1Num[branchNum]] += branchVector.q1[branchNum];

                    branchVector.dq1dv1[branchNum] = ClosedBranchSide1ReactiveFlowEquationTerm.dq1dv1(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.b1[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta1);
                    branchVector.dq1dv2[branchNum] = ClosedBranchSide1ReactiveFlowEquationTerm.dq1dv2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            cosTheta1);
                    branchVector.dq1dph1[branchNum] = ClosedBranchSide1ReactiveFlowEquationTerm.dq1dph1(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta1);
                    branchVector.dq1dph2[branchNum] = ClosedBranchSide1ReactiveFlowEquationTerm.dq1dph2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta1);

                    // i1

                    branchVector.i1[branchNum] = FastMath.hypot(branchVector.p1[branchNum], branchVector.q1[branchNum]) / (v1 * SQRT3 / 1000);

                    // p2

                    branchVector.p2[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.p2(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g2[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta2);
                    busVector.p[branchVector.bus2Num[branchNum]] += branchVector.p2[branchNum];

                    branchVector.dp2dv1[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.dp2dv1(
                            branchVector.y[branchNum],
                            r1,
                            v2,
                            sinTheta2);
                    branchVector.dp2dv2[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.dp2dv2(
                            branchVector.y[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g2[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta2);
                    branchVector.dp2dph1[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.dp2dph1(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta2);
                    branchVector.dp2dph2[branchNum] = ClosedBranchSide2ActiveFlowEquationTerm.dp2dph2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta2);

                    // q2

                    branchVector.q2[branchNum] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.b2[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta2);
                    busVector.q[branchVector.bus2Num[branchNum]] += branchVector.q2[branchNum];

                    branchVector.dq2dv1[branchNum] = ClosedBranchSide2ReactiveFlowEquationTerm.dq2dv1(
                            branchVector.y[branchNum],
                            r1,
                            v2,
                            cosTheta2);
                    branchVector.dq2dv2[branchNum] = ClosedBranchSide2ReactiveFlowEquationTerm.dq2dv2(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.b2[branchNum],
                            v1,
                            r1,
                            v2,
                            cosTheta2);
                    branchVector.dq2dph1[branchNum] = ClosedBranchSide2ReactiveFlowEquationTerm.dq2dph1(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta2);
                    branchVector.dq2dph2[branchNum] = ClosedBranchSide2ReactiveFlowEquationTerm.dq2dph2(
                            branchVector.y[branchNum],
                            v1,
                            r1,
                            v2,
                            sinTheta2);

                    // i2

                    branchVector.i2[branchNum] = FastMath.hypot(branchVector.p2[branchNum], branchVector.q2[branchNum]) / (v2 * SQRT3 / 1000);
                } else if (branchVector.bus1Num[branchNum] != -1) {
                    branchVector.p2[branchNum] = 0;
                    branchVector.q2[branchNum] = 0;
                    branchVector.i2[branchNum] = 0;

                    double v1 = state[variableVector.v1Row[branchNum]];
                    double r1 = variableVector.r1Row[branchNum] != -1 ? state[variableVector.r1Row[branchNum]]
                                                                      : branchVector.r1[branchNum];

                    branchVector.p1[branchNum] = OpenBranchSide2ActiveFlowEquationTerm.p1(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            branchVector.g2[branchNum],
                            branchVector.b2[branchNum],
                            v1,
                            r1);
                    busVector.p[branchVector.bus1Num[branchNum]] += branchVector.p1[branchNum];

                    branchVector.q1[branchNum] = OpenBranchSide2ReactiveFlowEquationTerm.q1(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.b1[branchNum],
                            branchVector.g2[branchNum],
                            branchVector.b2[branchNum],
                            v1,
                            r1);
                    busVector.q[branchVector.bus1Num[branchNum]] += branchVector.q1[branchNum];

                    branchVector.i1[branchNum] = FastMath.hypot(branchVector.p1[branchNum], branchVector.q1[branchNum]) / (v1 * SQRT3 / 1000);
                } else if (branchVector.bus2Num[branchNum] != -1) {
                    branchVector.p1[branchNum] = 0;
                    branchVector.q1[branchNum] = 0;
                    branchVector.i1[branchNum] = 0;

                    double v2 = state[variableVector.v2Row[branchNum]];

                    branchVector.p2[branchNum] = OpenBranchSide1ActiveFlowEquationTerm.p2(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            branchVector.b1[branchNum],
                            branchVector.g2[branchNum],
                            v2);
                    busVector.p[branchVector.bus2Num[branchNum]] += branchVector.p2[branchNum];

                    branchVector.q2[branchNum] = OpenBranchSide1ReactiveFlowEquationTerm.q2(
                            branchVector.y[branchNum],
                            branchVector.cosKsi[branchNum],
                            branchVector.sinKsi[branchNum],
                            branchVector.g1[branchNum],
                            branchVector.b1[branchNum],
                            branchVector.b2[branchNum],
                            v2);
                    busVector.q[branchVector.bus2Num[branchNum]] += branchVector.q2[branchNum];

                    branchVector.i2[branchNum] = FastMath.hypot(branchVector.p2[branchNum], branchVector.q2[branchNum]) / (v2 * SQRT3 / 1000);
                } else {
                    branchVector.p1[branchNum] = 0;
                    branchVector.p2[branchNum] = 0;
                    branchVector.i1[branchNum] = 0;
                    branchVector.i2[branchNum] = 0;
                }
            }
        }

        for (int busNum = 0; busNum < busVector.getSize(); busNum++) {
            busVector.p[busNum] *= busVector.status[busNum];
            busVector.q[busNum] *= busVector.status[busNum];
        }
    }
}
