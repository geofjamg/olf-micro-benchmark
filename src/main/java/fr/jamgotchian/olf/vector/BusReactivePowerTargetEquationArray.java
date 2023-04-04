package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

public class BusReactivePowerTargetEquationArray extends AbstractEquationArray<BusVector, AcVariableType, AcEquationType> {

    public BusReactivePowerTargetEquationArray(BusVector busVector) {
        super(busVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_Q;
    }

    @Override
    protected double[] getAttribute() {
        return elementVector.q;
    }
}
