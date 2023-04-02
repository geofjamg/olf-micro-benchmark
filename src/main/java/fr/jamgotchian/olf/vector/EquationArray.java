package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.equations.Quantity;

public interface EquationArray<V extends Enum<V> & Quantity, E extends Enum<E> & Quantity> {

    E getType();

    int getFirstColumn();

    void setFirstColumn(int firstColumn);

    int getLength();

    boolean isActive(int elementNum);

    void setActive(int elementNum, boolean active);

    void eval(double[] values);
}
