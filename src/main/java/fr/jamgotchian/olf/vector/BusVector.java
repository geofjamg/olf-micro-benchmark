package fr.jamgotchian.olf.vector;

import com.powsybl.openloadflow.network.LfBus;

import java.util.List;

public class BusVector implements ElementVector {

    final int[] status;

    final double[] p;
    final double[] q;

    public BusVector(List<LfBus> buses) {
        status = new int[buses.size()];
        p = new double[buses.size()];
        q = new double[buses.size()];
        for (int i = 0; i < buses.size(); i++) {
            LfBus bus = buses.get(i);
            status[i] = bus.isDisabled() ? 0 : 1;
        }
    }

    @Override
    public int getSize() {
        return status.length;
    }

    @Override
    public boolean isActive(int elementNum) {
        return status[elementNum] == 1;
    }
}
