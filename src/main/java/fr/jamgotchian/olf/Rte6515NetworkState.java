package fr.jamgotchian.olf;

import com.powsybl.iidm.network.Network;

public class Rte6515NetworkState extends AbstractNetworkState {

    @Override
    protected Network loadNetwork() {
        return MatpowerUtil.importMat("case6515rte");
    }
}
