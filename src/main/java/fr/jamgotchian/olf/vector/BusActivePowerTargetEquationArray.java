package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

import java.util.Objects;

public class BusActivePowerTargetEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    private final BusVector busVector;

    private final QuantityVector quantityVector;

    public BusActivePowerTargetEquationArray(BusVector busVector, QuantityVector quantityVector) {
        this.busVector = Objects.requireNonNull(busVector);
        this.quantityVector = Objects.requireNonNull(quantityVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    @Override
    public boolean isActive(int num) {
        return busVector.active[num] == 0;
    }

    @Override
    public void setActive(int num, boolean active) {
        busVector.active[num] = active ? 1 : 0;
    }

    @Override
    public void eval(double[] values) {
        for (int busNum = 0; busNum < busVector.getSize(); busNum++) {
            values[quantityVector.pColumn[busNum]] = busVector.p[busNum];
            values[quantityVector.qColumn[busNum]] = busVector.q[busNum];
        }
    }
}
