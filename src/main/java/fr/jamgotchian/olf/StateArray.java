package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.equations.StateVector;
import com.powsybl.openloadflow.equations.Variable;

public class StateArray {

    final double[] v;
    final double[] ph;

    public StateArray(int busCount, EquationSystem<AcVariableType, AcEquationType> equationSystem) {
        v = new double[busCount];
        ph = new double[busCount];
        StateVector stateVector = equationSystem.getStateVector();
        for (Variable<AcVariableType> var : equationSystem.getIndex().getSortedVariablesToFind()) {
            switch (var.getType()) {
                case BUS_V:
                    v[var.getElementNum()] = stateVector.get(var.getRow());
                    break;

                case BUS_PHI:
                    ph[var.getElementNum()] = stateVector.get(var.getRow());
                    break;
            }
        }
    }
}
