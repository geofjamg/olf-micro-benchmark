package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.equations.Variable;

public class VariableArray {

    final int[] vRow;
    final int[] phRow;

    final int[] v1Row;
    final int[] v2Row;
    final int[] ph1Row;
    final int[] ph2Row;

    public VariableArray(int busCount, BranchArray branchArray,
                         EquationSystem<AcVariableType, AcEquationType> equationSystem) {
        vRow = new int[busCount];
        phRow = new int[busCount];
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
        v1Row = new int[branchArray.length];
        v2Row = new int[branchArray.length];
        ph1Row = new int[branchArray.length];
        ph2Row = new int[branchArray.length];
        for (int branchNum = 0; branchNum < branchArray.length; branchNum++) {
            v1Row[branchNum] = vRow[branchArray.bus1Num[branchNum]];
            v2Row[branchNum] = vRow[branchArray.bus2Num[branchNum]];
            ph1Row[branchNum] = phRow[branchArray.bus1Num[branchNum]];
            ph2Row[branchNum] = phRow[branchArray.bus2Num[branchNum]];
        }
    }
}
