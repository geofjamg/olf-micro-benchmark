package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

import java.util.Objects;

public class BusReactivePowerTargetEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    private final BusVector busVector;

    private final QuantityVector quantityVector;

    public BusReactivePowerTargetEquationArray(BusVector busVector, QuantityVector quantityVector) {
        this.busVector = Objects.requireNonNull(busVector);
        this.quantityVector = Objects.requireNonNull(quantityVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_Q;
    }

    @Override
    public void eval(double[] values) {
        for (int busNum = 0; busNum < busVector.getSize(); busNum++) {
            values[quantityVector.qColumn[busNum]] = busVector.q[busNum];
        }
    }
}
