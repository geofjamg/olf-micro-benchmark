package fr.jamgotchian.olf;

import com.powsybl.iidm.network.Network;
import com.powsybl.openloadflow.ac.equations.*;
import com.powsybl.openloadflow.ac.nr.NewtonRaphson;
import com.powsybl.openloadflow.equations.Equation;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.LfNetwork;
import com.powsybl.openloadflow.network.MostMeshedSlackBusSelector;
import com.powsybl.openloadflow.network.impl.LfNetworkLoaderImpl;
import com.powsybl.openloadflow.network.util.PreviousValueVoltageInitializer;
import fr.jamgotchian.olf.vector.BusActivePowerTargetEquationArray;
import fr.jamgotchian.olf.vector.NetworkVector;
import fr.jamgotchian.olf.vector.VariableVector;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Thread)
public abstract class AbstractNetworkState {

    private LfNetwork lfNetwork;

    private List<Equation<AcVariableType, AcEquationType>> equations;

    private EquationSystem<AcVariableType, AcEquationType> equationSystem;

    private NetworkVector networkVector;

    private VariableVector variableVector;

    private BusActivePowerTargetEquationArray equationArray;

    @Setup(Level.Trial)
    public void doSetup() {
        Network network = loadNetwork();
        lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new MostMeshedSlackBusSelector()).get(0);
        equations = new ArrayList<>();
        equationSystem = new EquationSystem<>();
        for (LfBus bus : lfNetwork.getBuses()) {
            equations.add(equationSystem.createEquation(bus.getNum(), AcEquationType.BUS_TARGET_P));
            equations.add(equationSystem.createEquation(bus.getNum(), AcEquationType.BUS_TARGET_Q));
        }
        for (LfBranch branch : lfNetwork.getBranches()) {
            LfBus bus1 = branch.getBus1();
            LfBus bus2 = branch.getBus2();
            if (bus1 != null && bus2 != null) {
                equationSystem.getEquation(bus1.getNum(), AcEquationType.BUS_TARGET_P)
                        .orElseThrow()
                        .addTerm(new ClosedBranchSide1ActiveFlowEquationTerm(branch, bus1, bus2, equationSystem.getVariableSet(), false, false));
                equationSystem.getEquation(bus2.getNum(), AcEquationType.BUS_TARGET_P)
                        .orElseThrow()
                        .addTerm(new ClosedBranchSide2ActiveFlowEquationTerm(branch, bus1, bus2, equationSystem.getVariableSet(), false, false));
                equationSystem.getEquation(bus1.getNum(), AcEquationType.BUS_TARGET_Q)
                        .orElseThrow()
                        .addTerm(new ClosedBranchSide1ReactiveFlowEquationTerm(branch, bus1, bus2, equationSystem.getVariableSet(), false, false));
                equationSystem.getEquation(bus2.getNum(), AcEquationType.BUS_TARGET_Q)
                        .orElseThrow()
                        .addTerm(new ClosedBranchSide2ReactiveFlowEquationTerm(branch, bus1, bus2, equationSystem.getVariableSet(), false, false));
            } else if (bus1 != null) {
                equationSystem.getEquation(bus1.getNum(), AcEquationType.BUS_TARGET_P)
                        .orElseThrow()
                        .addTerm(new OpenBranchSide1ActiveFlowEquationTerm(branch, bus1, equationSystem.getVariableSet(), false, false));
                equationSystem.getEquation(bus1.getNum(), AcEquationType.BUS_TARGET_Q)
                        .orElseThrow()
                        .addTerm(new OpenBranchSide1ReactiveFlowEquationTerm(branch, bus1, equationSystem.getVariableSet(), false, false));
            } else if (bus2 != null) {
                equationSystem.getEquation(bus2.getNum(), AcEquationType.BUS_TARGET_P)
                        .orElseThrow()
                        .addTerm(new OpenBranchSide2ActiveFlowEquationTerm(branch, bus2, equationSystem.getVariableSet(), false, false));
                equationSystem.getEquation(bus2.getNum(), AcEquationType.BUS_TARGET_Q)
                        .orElseThrow()
                        .addTerm(new OpenBranchSide2ReactiveFlowEquationTerm(branch, bus2, equationSystem.getVariableSet(), false, false));
            }
        }
        NewtonRaphson.initStateVector(lfNetwork, equationSystem, new PreviousValueVoltageInitializer());

        networkVector = new NetworkVector(lfNetwork);
        variableVector = new VariableVector(networkVector, equationSystem);
    }

    protected abstract Network loadNetwork();

    public LfNetwork getLfNetwork() {
        return lfNetwork;
    }

    public List<Equation<AcVariableType, AcEquationType>> getEquations() {
        return equations;
    }

    public EquationSystem<AcVariableType, AcEquationType> getEquationSystem() {
        return equationSystem;
    }

    public NetworkVector getNetworkVector() {
        return networkVector;
    }

    public VariableVector getQuantityVector() {
        return variableVector;
    }
}
