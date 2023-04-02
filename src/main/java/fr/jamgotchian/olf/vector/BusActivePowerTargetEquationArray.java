package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

public class BusActivePowerTargetEquationArray extends AbstractEquationArray<BusVector, AcVariableType, AcEquationType> {

    public BusActivePowerTargetEquationArray(BusVector busVector, VariableVector variableVector) {
        super(busVector, variableVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    @Override
    protected double[] getAttribute() {
        return elementVector.p;
    }
}
