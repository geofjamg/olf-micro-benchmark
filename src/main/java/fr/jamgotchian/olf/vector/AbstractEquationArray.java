package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.Quantity;

import java.util.Objects;

public abstract class AbstractEquationArray<EV extends ElementVector, V extends Enum<V> & Quantity, E extends Enum<E> & Quantity>
        implements EquationArray<V, E> {

    protected final EV elementVector;

    protected final VariableVector variableVector;

    protected int firstColumn = -1;

    protected boolean[] active;

    protected int length = 0;

    protected AbstractEquationArray(EV elementVector, VariableVector variableVector) {
        this.elementVector = Objects.requireNonNull(elementVector);
        this.variableVector = Objects.requireNonNull(variableVector);
        active = new boolean[elementVector.getSize()];
        for (int elementNum = 0; elementNum < elementVector.getSize(); elementNum++) {
            active[elementNum] = elementVector.isActive(elementNum);
            if (active[elementNum]) {
                length++;
            }
        }
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
        return length;
    }

    @Override
    public boolean isActive(int elementNum) {
        return active[elementNum];
    }

    @Override
    public void setActive(int elementNum, boolean active) {
        if (active != this.active[elementNum]) {
            this.active[elementNum] = active;
            if (active) {
                length++;
            } else {
                length--;
            }
        }
    }
}
