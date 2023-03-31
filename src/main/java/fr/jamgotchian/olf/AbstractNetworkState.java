package fr.jamgotchian.olf;

import com.powsybl.iidm.network.Network;
import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.ac.equations.ClosedBranchSide1ActiveFlowEquationTerm;
import com.powsybl.openloadflow.ac.equations.ClosedBranchSide2ActiveFlowEquationTerm;
import com.powsybl.openloadflow.ac.nr.NewtonRaphson;
import com.powsybl.openloadflow.equations.Equation;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.network.LfBranch;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.LfNetwork;
import com.powsybl.openloadflow.network.MostMeshedSlackBusSelector;
import com.powsybl.openloadflow.network.impl.LfNetworkLoaderImpl;
import com.powsybl.openloadflow.network.util.PreviousValueVoltageInitializer;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Thread)
public abstract class AbstractNetworkState {

    private Network network;

    private LfNetwork lfNetwork;

    private List<Equation<AcVariableType, AcEquationType>> equations;

    private BusActivePowerTargetEquationArray equationArray;

    @Setup(Level.Trial)
    public void doSetup() {
        network = loadNetwork();
        lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new MostMeshedSlackBusSelector()).get(0);
        equations = new ArrayList<>();
        EquationSystem<AcVariableType, AcEquationType> equationSystem = new EquationSystem<>();
        for (LfBus bus : lfNetwork.getBuses()) {
            equations.add(equationSystem.createEquation(bus.getNum(), AcEquationType.BUS_TARGET_P));
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
            }
        }
        NewtonRaphson.initStateVector(lfNetwork, equationSystem, new PreviousValueVoltageInitializer());

        BranchArray branchArray = new BranchArray(lfNetwork.getBranches());
        VariableArray variableArray = new VariableArray(lfNetwork.getBuses().size(), equationSystem);
        equationArray = new BusActivePowerTargetEquationArray(lfNetwork.getBuses().size(), branchArray, variableArray, equationSystem.getStateVector());
    }

    protected abstract Network loadNetwork();

    public LfNetwork getLfNetwork() {
        return lfNetwork;
    }

    public List<Equation<AcVariableType, AcEquationType>> getEquations() {
        return equations;
    }

    public BusActivePowerTargetEquationArray getEquationArray() {
        return equationArray;
    }
}
