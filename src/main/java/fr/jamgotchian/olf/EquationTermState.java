package fr.jamgotchian.olf;

import com.powsybl.commons.PowsyblException;
import com.powsybl.openloadflow.ac.equations.*;
import com.powsybl.openloadflow.equations.EquationTerm;
import com.powsybl.openloadflow.equations.StateVector;
import com.powsybl.openloadflow.equations.VariableSet;
import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.PiModel;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@State(Scope.Thread)
public class EquationTermState {

    public static class RuntimeExceptionAnswer implements Answer<Object> {

        public Object answer(InvocationOnMock invocation) {
            throw new PowsyblException(invocation.getMethod().getName() + " is not stubbed");
        }
    }

    private static final RuntimeExceptionAnswer ANSWER = new RuntimeExceptionAnswer();

    private final int branchCount = 10000;
    private final int busCount = 5000;

    private static final double R = 5.872576933488291E-4;
    private static final double X = 0.007711911135433123;
    private static final double Y = 129.29521139058275;
    private static final double KSI = 0.07600275710144264;
    private static final double G_1 = 0.08448324029284184;
    private static final double G_2 = 0.06483244848429284;
    private static final double B_1 = 0.13324220618233085;
    private static final double B_2 = 0.18320177723653615;
    private static final double V_1 = 1.0714396912858781;
    private static final double PH_1 = 0.1613653508202422;
    private static final double R_1 = 0.95455;
    private static final double A_1 = 0.324294234;

    private void generate(double[] values, double baseValue) {
        for (int i = 0; i < values.length; i++) {
            values[i] = baseValue + (random.nextDouble() / 20 * baseValue);
        }
    }

    private final Random random = new Random();

    private final List<EquationTerm<AcVariableType, AcEquationType>> terms = new ArrayList<>();

    private final List<EquationTerm<AcVariableType, AcEquationType>> shuffledTerms = new ArrayList<>();

    private final int[] branchBus1Num = new int[branchCount];

    private final int[] branchBus2Num = new int[branchCount];

    private final double[] r = new double[branchCount];
    private final double[] x = new double[branchCount];
    private final double[] y = new double[branchCount];
    private final double[] ksi = new double[branchCount];
    private final double[] g1 = new double[branchCount];
    private final double[] g2 = new double[branchCount];
    private final double[] b1 = new double[branchCount];
    private final double[] b2 = new double[branchCount];
    private final double[] r1 = new double[branchCount];
    private final double[] a1 = new double[branchCount];

    private final double[] v = new double[busCount];
    private final double[] ph = new double[busCount];

    @Setup(Level.Trial)
    public void doSetup() {
        generate(r, R);
        generate(x, X);
        generate(y, Y);
        generate(ksi, KSI);
        generate(g1, G_1);
        generate(g2, G_2);
        generate(b1, B_1);
        generate(b2, B_2);
        generate(v, V_1);
        generate(ph, PH_1);
        generate(r1, R_1);
        generate(a1, A_1);

        VariableSet<AcVariableType> variableSet = new VariableSet<>();

        List<LfBus> buses = new ArrayList<>();
        for (int i = 0; i < busCount; i++) {
            var bus = Mockito.mock(LfBus.class, ANSWER);
            buses.add(bus);
            Mockito.doReturn(i).when(bus).getNum();

            variableSet.getVariable(i, AcVariableType.BUS_V);
            variableSet.getVariable(i, AcVariableType.BUS_PHI);
        }

        List<LfBranch> branches = new ArrayList<>();
        for (int i = 0; i < branchCount; i++) {
            var branch = Mockito.mock(LfBranch.class, ANSWER);
            branches.add(branch);
            Mockito.doReturn(i).when(branch).getNum();
            PiModel piModel = Mockito.mock(PiModel.class, ANSWER);
            Mockito.doReturn(piModel).when(branch).getPiModel();
            Mockito.doReturn(r[i]).when(piModel).getR();
            Mockito.doReturn(x[i]).when(piModel).getX();
            Mockito.doReturn(y[i]).when(piModel).getY();
            Mockito.doReturn(g1[i]).when(piModel).getG1();
            Mockito.doReturn(g2[i]).when(piModel).getG2();
            Mockito.doReturn(b1[i]).when(piModel).getB1();
            Mockito.doReturn(b2[i]).when(piModel).getB2();
            Mockito.doReturn(ksi[i]).when(piModel).getKsi();
            Mockito.doReturn(r1[i]).when(piModel).getR1();

            variableSet.getVariable(i, AcVariableType.BRANCH_RHO1);
            variableSet.getVariable(i, AcVariableType.BRANCH_ALPHA1);

            int bus1Num = i % busCount;
            int bus2Num = (int) Math.floor(random.nextDouble() * busCount);
            Mockito.doReturn(buses.get(bus1Num)).when(branch).getBus1();
            Mockito.doReturn(buses.get(bus2Num)).when(branch).getBus2();
        }

        int row = 0;
        double[] values = new double[variableSet.getVariables().size()];
        for (var vb : variableSet.getVariables()) {
            vb.setRow(row);
            switch (vb.getType()) {
                case BUS_V:
                    values[row] = v[vb.getElementNum()];
                    break;
                case BUS_PHI:
                    values[row] = ph[vb.getElementNum()];
                    break;
                case BRANCH_RHO1:
                    values[row] = r1[vb.getElementNum()];
                    break;
                case BRANCH_ALPHA1:
                    values[row] = a1[vb.getElementNum()];
                    break;
                default:
                    throw new AssertionError();
            }
            row++;
        }

        var sv = new StateVector(values);

        for (LfBranch branch : branches) {
            terms.add(new ClosedBranchSide1ActiveFlowEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
            terms.add(new ClosedBranchSide1ReactiveFlowEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
            terms.add(new ClosedBranchSide1CurrentMagnitudeEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
            terms.add(new ClosedBranchSide2ActiveFlowEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
            terms.add(new ClosedBranchSide2ReactiveFlowEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
            terms.add(new ClosedBranchSide2CurrentMagnitudeEquationTerm(branch, branch.getBus1(), branch.getBus2(), variableSet, true, true));
        }
        for (var term : terms) {
            term.setStateVector(sv);
        }

        shuffledTerms.addAll(terms);
        Collections.shuffle(shuffledTerms);

        for (int num = 0; num < branchCount; num++) {
            LfBranch branch = branches.get(num);
            branchBus1Num[num] = branch.getBus1().getNum();
            branchBus2Num[num] = branch.getBus2().getNum();
        }
    }

    public int getBranchCount() {
        return branchCount;
    }

    public int getBusCount() {
        return busCount;
    }

    public List<EquationTerm<AcVariableType, AcEquationType>> getTerms() {
        return terms;
    }

    public List<EquationTerm<AcVariableType, AcEquationType>> getShuffledTerms() {
        return shuffledTerms;
    }

    public int[] getBranchBus1Num() {
        return branchBus1Num;
    }

    public int[] getBranchBus2Num() {
        return branchBus2Num;
    }

    public double[] getY() {
        return y;
    }

    public double[] getKsi() {
        return ksi;
    }

    public double[] getG1() {
        return g1;
    }

    public double[] getG2() {
        return g2;
    }

    public double[] getB1() {
        return b1;
    }

    public double[] getB2() {
        return b2;
    }

    public double[] getR1() {
        return r1;
    }

    public double[] getA1() {
        return a1;
    }

    public double[] getV() {
        return v;
    }

    public double[] getPh() {
        return ph;
    }
}
