package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.*;
import net.jafama.DoubleWrapper;
import net.jafama.FastMath;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.powsybl.openloadflow.network.PiModel.A2;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class EquationTermBenchmark {

    private static final double SQRT_3 = FastMath.sqrt(3);

    //@Benchmark
    public void objectModelSortedByBranch(EquationTermState state) {
        var terms = state.getTerms();
        double[] values = new double[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            values[i] = terms.get(i).eval();
        }
    }

    //@Benchmark
    public void objectModelSortedByBranchAndStoreResults(EquationTermState state) {
        double[] p = new double[state.getBusCount()];
        double[] q = new double[state.getBusCount()];
        double[] i = new double[state.getBusCount()];
        for (int num = 0; num < state.getBusCount(); num++) {
            for (var term : state.getBusP().get(num)) {
                p[num] += term.eval();
            }
            for (var term : state.getBusQ().get(num)) {
                q[num] += term.eval();
            }
            for (var term : state.getBusI().get(num)) {
                i[num] += term.eval();
            }
        }
    }

    //@Benchmark
    public void objectModelShuffled(EquationTermState state) {
        var terms = state.getShuffledTerms();
        double[] values = new double[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            values[i] = terms.get(i).eval();
        }
    }

    private static void calculateCosSinTheta(int branchCount, double[] ksi, int[] branchBus1Num, int[] branchBus2Num, double[] ph,
                                             double[] a1, double[] sinTheta1, double[] cosTheta1, double[] sinTheta2, double[] cosTheta2) {
        var w = new DoubleWrapper();
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            double theta1 = AbstractClosedBranchAcFlowEquationTerm.theta1(ksi[num], ph[bus1Num], a1[num], ph[bus2Num]);
            double theta2 = AbstractClosedBranchAcFlowEquationTerm.theta2(ksi[num], ph[bus1Num], a1[num], ph[bus2Num]);
            sinTheta1[num] = FastMath.sinAndCos(theta1, w);
            cosTheta1[num] = w.value;
            sinTheta2[num] = FastMath.sinAndCos(theta2, w);
            cosTheta2[num] = w.value;
        }
    }

    //@Benchmark
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
        double[] sinTheta1 = new double[state.getBranchCount()];
        double[] cosTheta1 = new double[state.getBranchCount()];
        double[] sinTheta2 = new double[state.getBranchCount()];
        double[] cosTheta2 = new double[state.getBranchCount()];
        calculateCosSinTheta(branchCount, ksi, branchBus1Num, branchBus2Num, ph, a1, sinTheta1, cosTheta1, sinTheta2, cosTheta2);
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], sinKsi[num], g1[num], v[bus1Num], r1[num], v[bus2Num], sinTheta1[num]);
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], cosKsi[num], b1[num], v[bus1Num], r1[num], v[bus2Num], cosTheta1[num]);
            i1[num] = ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], sinKsi[num], g2[num], v[bus1Num], r1[num], v[bus2Num], sinTheta2[num]);
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], cosKsi[num], b2[num], v[bus1Num], r1[num], v[bus2Num], cosTheta2[num]);
            i2[num] = ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    //@Benchmark
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
        double[] sinTheta1 = new double[state.getBranchCount()];
        double[] cosTheta1 = new double[state.getBranchCount()];
        double[] sinTheta2 = new double[state.getBranchCount()];
        double[] cosTheta2 = new double[state.getBranchCount()];
        calculateCosSinTheta(branchCount, ksi, branchBus1Num, branchBus2Num, ph, a1, sinTheta1, cosTheta1, sinTheta2, cosTheta2);
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], sinKsi[num], g1[num], v[bus1Num], r1[num], v[bus2Num], sinTheta1[num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], cosKsi[num], b1[num], v[bus1Num], r1[num], v[bus2Num], cosTheta1[num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            i1[num] = ClosedBranchSide1CurrentMagnitudeEquationTerm.i1(y[num], ksi[num], g1[num], b1[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], sinKsi[num], g2[num], v[bus1Num], r1[num], v[bus2Num], sinTheta2[num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], cosKsi[num], b2[num], v[bus1Num], r1[num], v[bus2Num], cosTheta2[num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            i2[num] = ClosedBranchSide2CurrentMagnitudeEquationTerm.i2(y[num], ksi[num], g2[num], b2[num], v[bus1Num], ph[bus1Num], r1[num], a1[num], v[bus2Num], ph[bus2Num]);
        }
    }

    //@Benchmark
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
        double[] sinTheta1 = new double[state.getBranchCount()];
        double[] cosTheta1 = new double[state.getBranchCount()];
        double[] sinTheta2 = new double[state.getBranchCount()];
        double[] cosTheta2 = new double[state.getBranchCount()];
        calculateCosSinTheta(branchCount, ksi, branchBus1Num, branchBus2Num, ph, a1, sinTheta1, cosTheta1, sinTheta2, cosTheta2);
        double[] p1 = new double[branchCount];
        double[] q1 = new double[branchCount];
        double[] i1 = new double[branchCount];
        double[] p2 = new double[branchCount];
        double[] q2 = new double[branchCount];
        double[] i2 = new double[branchCount];
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            p1[num] = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], sinKsi[num], g1[num], v[bus1Num], r1[num], v[bus2Num], sinTheta1[num]);
            q1[num] = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], cosKsi[num], b1[num], v[bus1Num], r1[num], v[bus2Num], cosTheta1[num]);
            p2[num] = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], sinKsi[num], g2[num], v[bus1Num], r1[num], v[bus2Num], sinTheta2[num]);
            q2[num] = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], cosKsi[num], b2[num], v[bus1Num], r1[num], v[bus2Num], cosTheta2[num]);
            i1[num] = FastMath.hypot(p1[num], q1[num]) / (v[bus1Num] * FastMath.sqrt(3) / 1000);
            i2[num] = FastMath.hypot(p2[num], q2[num]) / (v[bus2Num] * FastMath.sqrt(3) / 1000);
        }
    }

    //@Benchmark
    public void staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResult(EquationTermState state) {
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
        double[] p = new double[state.getBusCount()];
        double[] q = new double[state.getBusCount()];
        double[] i = new double[state.getBusCount()];
        double[] sinTheta1 = new double[state.getBranchCount()];
        double[] cosTheta1 = new double[state.getBranchCount()];
        double[] sinTheta2 = new double[state.getBranchCount()];
        double[] cosTheta2 = new double[state.getBranchCount()];
        calculateCosSinTheta(branchCount, ksi, branchBus1Num, branchBus2Num, ph, a1, sinTheta1, cosTheta1, sinTheta2, cosTheta2);
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            double p1 = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], sinKsi[num], g1[num], v[bus1Num], r1[num], v[bus2Num], sinTheta1[num]);
            double q1 = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], cosKsi[num], b1[num], v[bus1Num], r1[num], v[bus2Num], cosTheta1[num]);
            double p2 = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], sinKsi[num], g2[num], v[bus1Num], r1[num], v[bus2Num], sinTheta2[num]);
            double q2 = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], cosKsi[num], b2[num], v[bus1Num], r1[num], v[bus2Num], cosTheta2[num]);
            double i1 = FastMath.hypot(p1, q1) / (v[bus1Num] * SQRT_3 / 1000);
            double i2 = FastMath.hypot(p2, q2) / (v[bus2Num] * SQRT_3 / 1000);
            p[bus1Num] += p1;
            q[bus1Num] += q1;
            i[bus1Num] += i1;
            p[bus2Num] += p2;
            q[bus2Num] += q2;
            i[bus2Num] += i2;
        }
    }

    // cos(a+b) = cos a cos b - sin a sin b
    private static double cosAplusB(double cosA, double cosB, double sinA, double sinB) {
        return cosA * cosB - sinA * sinB;
    }

    // sin(a+b) = sin a cos b + cos a sin b
    private static double sinAplusB(double cosA, double cosB, double sinA, double sinB) {
        return sinA * cosB + cosA * sinB;
    }

    private static double theta0(double ph1, double a1, double ph2) {
        return a1 - A2 + ph1 - ph2;
    }

    //@Benchmark
    public void staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResultOnlyOneCosSin(EquationTermState state) {
        int[] branchBus1Num = state.getBranchBus1Num();
        int[] branchBus2Num = state.getBranchBus2Num();
        double[] y = state.getY();
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
        double[] p = new double[state.getBusCount()];
        double[] q = new double[state.getBusCount()];
        double[] i = new double[state.getBusCount()];
        double[] sinTheta0 = new double[state.getBranchCount()];
        double[] cosTheta0 = new double[state.getBranchCount()];
        double[] sinTheta1 = new double[state.getBranchCount()];
        double[] cosTheta1 = new double[state.getBranchCount()];
        double[] sinTheta2 = new double[state.getBranchCount()];
        double[] cosTheta2 = new double[state.getBranchCount()];
        var w = new DoubleWrapper();
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            double theta0 = theta0(ph[bus1Num], a1[num], ph[bus2Num]);
            sinTheta0[num] = FastMath.sinAndCos(theta0, w);
            cosTheta0[num] = w.value;
            // theta1 = ksi - theta0
            // theta2 = ksi + theta0
            cosTheta1[num] = cosAplusB(cosKsi[num], cosTheta0[num], sinKsi[num], -sinTheta0[num]);
            sinTheta1[num] = sinAplusB(cosKsi[num], cosTheta0[num], sinKsi[num], -sinTheta0[num]);
            cosTheta2[num] = cosAplusB(cosKsi[num], cosTheta0[num], sinKsi[num], sinTheta0[num]);
            sinTheta2[num] = sinAplusB(cosKsi[num], cosTheta0[num], sinKsi[num], sinTheta0[num]);
        }
        for (int num = 0; num < branchCount; num++) {
            int bus1Num = branchBus1Num[num];
            int bus2Num = branchBus2Num[num];
            double p1 = ClosedBranchSide1ActiveFlowEquationTerm.p1(y[num], sinKsi[num], g1[num], v[bus1Num], r1[num], v[bus2Num], sinTheta1[num]);
            double q1 = ClosedBranchSide1ReactiveFlowEquationTerm.q1(y[num], cosKsi[num], b1[num], v[bus1Num], r1[num], v[bus2Num], cosTheta1[num]);
            double p2 = ClosedBranchSide2ActiveFlowEquationTerm.p2(y[num], sinKsi[num], g2[num], v[bus1Num], r1[num], v[bus2Num], sinTheta2[num]);
            double q2 = ClosedBranchSide2ReactiveFlowEquationTerm.q2(y[num], cosKsi[num], b2[num], v[bus1Num], r1[num], v[bus2Num], cosTheta2[num]);
            double i1 = FastMath.hypot(p1, q1) / (v[bus1Num] * SQRT_3 / 1000);
            double i2 = FastMath.hypot(p2, q2) / (v[bus2Num] * SQRT_3 / 1000);
            p[bus1Num] += p1;
            q[bus1Num] += q1;
            i[bus1Num] += i1;
            p[bus2Num] += p2;
            q[bus2Num] += q2;
            i[bus2Num] += i2;
        }
    }
}
