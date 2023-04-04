package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.EquationSystem;

public class BusReactivePowerTargetEquationArray extends AbstractEquationArray<BusVector, AcVariableType, AcEquationType> {

    public BusReactivePowerTargetEquationArray(EquationSystem<AcVariableType, AcEquationType> equationSystem, BusVector busVector) {
        super(equationSystem, busVector);
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
