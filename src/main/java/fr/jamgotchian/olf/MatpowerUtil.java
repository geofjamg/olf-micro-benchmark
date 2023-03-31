package fr.jamgotchian.olf;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.network.Importer;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;

public final class MatpowerUtil {

    private MatpowerUtil() {
    }

    public static Network importMat(String name) {
        return Importer.find("MATPOWER")
                .importData(new ResourceDataSource(name, new ResourceSet("/data", name + ".mat")),
                        new NetworkFactoryImpl(),
                        null);
    }
}
