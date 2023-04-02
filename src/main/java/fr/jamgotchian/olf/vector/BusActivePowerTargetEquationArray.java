package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;

public class BusActivePowerTargetEquationArray extends AbstractBusEquationArray {

    public BusActivePowerTargetEquationArray(BusVector busVector, VariableVector variableVector) {
        super(busVector, variableVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    @Override
    public void eval(double[] values) {
        System.arraycopy(busVector.p, 0, values, firstColumn, busVector.getSize());
    }
}
