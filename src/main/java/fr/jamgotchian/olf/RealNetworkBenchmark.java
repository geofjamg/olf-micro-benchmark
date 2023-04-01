package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.Equation;
import fr.jamgotchian.olf.vector.BusActivePowerTargetEquationArray;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealNetworkBenchmark {

    @Benchmark
    public void objectModel(Rte6515NetworkState state, Blackhole bh) {
        List<Equation<AcVariableType, AcEquationType>> equations = state.getEquations();
        double[] values = new double[equations.size()];
        for (int i = 0; i < equations.size(); i++) {
            values[i] = equations.get(i).eval();
        }
        bh.consume(values);
    }

    @Benchmark
    public void arrayModel(Rte6515NetworkState state, Blackhole bh) {
        state.getNetworkVector().updateState(state.getQuantityVector(), state.getEquationSystem().getStateVector());
        BusActivePowerTargetEquationArray equationArray = state.getEquationArray();
        double[] values = new double[state.getEquationSystem().getIndex().getSortedEquationsToSolve().size()];
        equationArray.eval(values);
        bh.consume(values);
    }
}
