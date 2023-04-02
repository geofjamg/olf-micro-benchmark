package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;

public class BusReactivePowerTargetEquationArray extends AbstractBusEquationArray {

    public BusReactivePowerTargetEquationArray(BusVector busVector, VariableVector variableVector) {
        super(busVector, variableVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_Q;
    }

    @Override
    public void eval(double[] values) {
        System.arraycopy(busVector.q, 0, values, firstColumn, busVector.getSize());
    }
}
