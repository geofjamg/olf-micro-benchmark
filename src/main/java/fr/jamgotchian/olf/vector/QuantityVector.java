package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.*;

import java.util.Objects;

public class QuantityVector implements EquationSystemIndexListener<AcVariableType, AcEquationType> {

    private final NetworkVector networkVector;

    private final EquationSystem<AcVariableType, AcEquationType> equationSystem;

    final int[] vRow;
    final int[] phRow;

    final int[] v1Row;
    final int[] v2Row;
    final int[] ph1Row;
    final int[] ph2Row;

    final int[] pColumn;
    final int[] qColumn;

    public QuantityVector(NetworkVector networkVector,
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
        pColumn = new int[busCount];
        qColumn = new int[busCount];
        updateVariables();
        updateEquations();
        equationSystem.getIndex().addListener(this);
    }

    private void updateVariables() {
        for (Variable<AcVariableType> v : equationSystem.getIndex().getSortedVariablesToFind()) {
            switch (v.getType()) {
                case BUS_V:
                    vRow[v.getElementNum()] = v.getRow();
                    break;

                case BUS_PHI:
                    phRow[v.getElementNum()] = v.getRow();
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

    private void updateEquations() {
        for (Equation<AcVariableType, AcEquationType> equation : equationSystem.getIndex().getSortedEquationsToSolve()) {
            switch (equation.getType()) {
                case BUS_TARGET_P:
                    pColumn[equation.getElementNum()] = equation.getColumn();
                    break;
                case BUS_TARGET_Q:
                    qColumn[equation.getElementNum()] = equation.getColumn();
                    break;
            }
        }
    }

    @Override
    public void onVariableChange(Variable<AcVariableType> variable, ChangeType changeType) {
        updateVariables();
    }

    @Override
    public void onEquationChange(Equation<AcVariableType, AcEquationType> equation, ChangeType changeType) {
        updateEquations();
    }

    @Override
    public void onEquationTermChange(EquationTerm<AcVariableType, AcEquationType> equationTerm) {
        // northing to do
    }
}
