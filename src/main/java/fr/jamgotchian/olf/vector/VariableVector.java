package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.*;

import java.util.Arrays;
import java.util.Objects;

public class VariableVector implements EquationSystemIndexListener<AcVariableType, AcEquationType> {

    private final NetworkVector networkVector;

    private final EquationSystem<AcVariableType, AcEquationType> equationSystem;

    final int[] vRow;
    final int[] phRow;

    final int[] v1Row;
    final int[] v2Row;
    final int[] ph1Row;
    final int[] ph2Row;

    final int[] a1Row;
    final int[] r1Row;

    public VariableVector(NetworkVector networkVector,
                          EquationSystem<AcVariableType, AcEquationType> equationSystem) {
        this.networkVector = Objects.requireNonNull(networkVector);
        this.equationSystem = Objects.requireNonNull(equationSystem);
        int busCount = networkVector.getBusVector().getSize();
        int branchCount = networkVector.getBranchVector().getSize();
        vRow = new int[busCount];
        phRow = new int[busCount];
        v1Row = new int[branchCount];
        v2Row = new int[branchCount];
        ph1Row = new int[branchCount];
        ph2Row = new int[branchCount];
        a1Row = new int[branchCount];
        r1Row = new int[branchCount];
        updateVariables();
        equationSystem.getIndex().addListener(this);
    }

    private void updateVariables() {
        Arrays.fill(a1Row, -1);
        Arrays.fill(r1Row, -1);

        for (Variable<AcVariableType> v : equationSystem.getIndex().getSortedVariablesToFind()) {
            switch (v.getType()) {
                case BUS_V:
                    vRow[v.getElementNum()] = v.getRow();
                    break;

                case BUS_PHI:
                    phRow[v.getElementNum()] = v.getRow();
                    break;

                case BRANCH_ALPHA1:
                    a1Row[v.getElementNum()] = v.getRow();
                    break;

                case BRANCH_RHO1:
                    r1Row[v.getElementNum()] = v.getRow();
                    break;

                default:
                    break;
            }
        }
        var branchVector = networkVector.getBranchVector();
        for (int branchNum = 0; branchNum < branchVector.getSize(); branchNum++) {
            v1Row[branchNum] = vRow[branchVector.bus1Num[branchNum]];
            v2Row[branchNum] = vRow[branchVector.bus2Num[branchNum]];
            ph1Row[branchNum] = phRow[branchVector.bus1Num[branchNum]];
            ph2Row[branchNum] = phRow[branchVector.bus2Num[branchNum]];
        }
    }

    @Override
    public void onVariableChange(Variable<AcVariableType> variable, ChangeType changeType) {
        updateVariables();
    }

    @Override
    public void onEquationChange(Equation<AcVariableType, AcEquationType> equation, ChangeType changeType) {
        // nothing to do
    }

    @Override
    public void onEquationTermChange(EquationTerm<AcVariableType, AcEquationType> equationTerm) {
        // nothing to do
    }
}
