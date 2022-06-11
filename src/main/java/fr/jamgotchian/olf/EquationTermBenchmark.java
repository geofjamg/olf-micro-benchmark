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
        var terms = state.getTerms();
        double[] values = new double[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            values[i] = terms.get(i).eval();
        }
    }

    @Benchmark
    public void objectModelShuffled(EquationTermState state) {
        var terms = state.getShuffledTerms();
        double[] values = new double[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            values[i] = terms.get(i).eval();
        }
    }

    @Benchmark
    public void staticMethodSortedByBranch(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] cosKsi = state.getCosKsi();
        double[] sinKsi = state.getSinKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        int branchCount = state.getBranchCount();
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], sinKsi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], cosKsi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            i1[num] = ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num],sinKsi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], cosKsi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            i2[num] = ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    @Benchmark
    public void staticMethodSortedByTermType(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] cosKsi = state.getCosKsi();
        double[] sinKsi = state.getSinKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        int branchCount = state.getBranchCount();
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], sinKsi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], cosKsi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            i1[num] = ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num], sinKsi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], cosKsi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            i2[num] = ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    @Benchmark
    public void staticMethodSortedByBranchWithCurrentCalculatedFromActivePower(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
        double[] ksi = state.getKsi();
        double[] cosKsi = state.getCosKsi();
        double[] sinKsi = state.getSinKsi();
        double[] g1 = state.getG1();
        double[] g2 = state.getG2();
        double[] b1 = state.getB1();
        double[] b2 = state.getB2();
        double[] r1 = state.getR1();
        double[] a1 = state.getA1();
        double[] v = state.getV();
        double[] ph = state.getPh();
        int branchCount = state.getBranchCount();
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], ksi[num], sinKsi[num], g1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], ksi[num], cosKsi[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], ksi[num], sinKsi[num], g2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], ksi[num], cosKsi[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            i1[num] = FastMath.hypot(p1[num], q1[num]) / (v[bus1Num] * FastMath.sqrt(3) / 1000);
            i2[num] = FastMath.hypot(p2[num], q2[num]) / (v[bus2Num] * FastMath.sqrt(3) / 1000);
        }
    }
}
