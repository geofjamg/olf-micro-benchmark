package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

public abstract class AbstractBusEquationArray extends AbstractEquationArray<BusVector, AcVariableType, AcEquationType> {

    protected AbstractBusEquationArray(BusVector busVector, VariableVector variableVector) {
        super(busVector, variableVector);
    }
}
