package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;

import java.util.Objects;

public abstract class AbstractBusEquationArray implements EquationArray<AcVariableType, AcEquationType> {

    protected final BusVector busVector;

    protected final VariableVector variableVector;

    protected int firstColumn = -1;

    protected AbstractBusEquationArray(BusVector busVector, VariableVector variableVector) {
        this.busVector = Objects.requireNonNull(busVector);
        this.variableVector = Objects.requireNonNull(variableVector);
    }

    @Override
    public int getFirstColumn() {
        return firstColumn;
    }

    @Override
    public void setFirstColumn(int firstColumn) {
        this.firstColumn = firstColumn;
    }

    @Override
    public int getLength() {
        return busVector.getSize();
    }
}
