package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.ac.equations.AcEquationType;

public class BusActivePowerTargetEquationArray extends AbstractBusEquationArray {

    public BusActivePowerTargetEquationArray(BusVector busVector, QuantityVector quantityVector) {
        super(busVector, quantityVector);
    }

    @Override
    public AcEquationType getType() {
        return AcEquationType.BUS_TARGET_P;
    }

    @Override
    public void eval(double[] values) {
        for (int busNum = 0; busNum < busVector.getSize(); busNum++) {
            values[quantityVector.pColumn[busNum]] = busVector.p[busNum];
        }
    }
}
