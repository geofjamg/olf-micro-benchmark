package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.equations.Variable;

public class VariableArray {

    final int[] vRow;
    final int[] phRow;

    public VariableArray(int busCount, EquationSystem<AcVariableType, AcEquationType> equationSystem) {
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
    }
}
