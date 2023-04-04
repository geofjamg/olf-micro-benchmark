package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.equations.Quantity;
import com.powsybl.openloadflow.equations.Variable;

public interface EquationArray<V extends Enum<V> & Quantity, E extends Enum<E> & Quantity> {

    E getType();

    int getFirstColumn();

    void setFirstColumn(int firstColumn);

    int getLength();

    boolean isActive(int elementNum);

    void setActive(int elementNum, boolean active);

    void eval(double[] values);


    interface DerHandler<V extends Enum<V> & Quantity> {

        int onDer(int column, Variable<V> variable, double value, int matrixElementIndex);
    }

    void der(DerHandler<V> handler);
}
