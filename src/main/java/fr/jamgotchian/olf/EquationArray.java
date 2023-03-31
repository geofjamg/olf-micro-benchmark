package fr.jamgotchian.olf;

import com.powsybl.openloadflow.equations.Quantity;

public interface EquationArray<V extends Enum<V> & Quantity, E extends Enum<E> & Quantity> {

    E getType();
}
