package fr.jamgotchian.olf.array;

import com.powsybl.openloadflow.network.LfBus;

import java.util.List;

public class BusVector {

    final int[] active;

    final double[] p;
    final double[] q;

    public BusVector(List<LfBus> buses) {
        active = new int[buses.size()];
        p = new double[buses.size()];
        q = new double[buses.size()];
        for (int i = 0; i < buses.size(); i++) {
            LfBus bus = buses.get(i);
            active[i] = bus.isDisabled() ? 0 : 1;
        }
    }

    public int getSize() {
        return active.length;
    }
}
