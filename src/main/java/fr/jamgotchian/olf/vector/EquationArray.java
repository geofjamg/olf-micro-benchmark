package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.equations.Quantity;

public interface EquationArray<V extends Enum<V> & Quantity, E extends Enum<E> & Quantity> {

    E getType();

    void eval(double[] values);
}
