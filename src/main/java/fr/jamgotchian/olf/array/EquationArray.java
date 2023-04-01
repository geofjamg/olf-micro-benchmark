package fr.jamgotchian.olf.array;

import com.powsybl.openloadflow.equations.Quantity;

public interface EquationArray<V extends Enum<V> & Quantity, E extends Enum<E> & Quantity> {

    E getType();

    boolean isActive(int num);

    void setActive(int num, boolean active);

    void eval(double[] values);
}
