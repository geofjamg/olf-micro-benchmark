package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

import java.util.Objects;

public abstract class AbstractBusEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    protected final BusVector busVector;

    protected final QuantityVector quantityVector;

    protected AbstractBusEquationArray(BusVector busVector, QuantityVector quantityVector) {
        this.busVector = Objects.requireNonNull(busVector);
        this.quantityVector = Objects.requireNonNull(quantityVector);
    }
}
