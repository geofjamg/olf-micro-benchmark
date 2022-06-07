package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.*;
import net.jafama.FastMath;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class EquationTermBenchmark {

    @Benchmark
    public void objectModelSortedByBranch(EquationTermState state) {
        for (var term : state.getTerms()) {
            term.eval();
        }
    }

    @Benchmark
    public void objectModelShuffled(EquationTermState state) {
        for (var term : state.getShuffledTerms()) {
            term.eval();
        }
    }

    @Benchmark
    public void staticMethodSortedByBranch(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        for (int num = 0; num < state.getBranchCount(); num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    @Benchmark
    public void staticMethodSortedByTermType(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        int branchCount = state.getBranchCount();
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    @Benchmark
    public void staticMethodSortedByBranchWithCurrentCalculatedFromActivePower(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        for (int num = 0; num < state.getBranchCount(); num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            double p1 = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            double q1 = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            double p2 = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            double q2 = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            double i1 = FastMath.hypot(p1, q1) / (v[bus1Num] * FastMath.sqrt(3) / 1000);
            double i2 = FastMath.hypot(p2, q2) / (v[bus2Num] * FastMath.sqrt(3) / 1000);
        }
    }
}
