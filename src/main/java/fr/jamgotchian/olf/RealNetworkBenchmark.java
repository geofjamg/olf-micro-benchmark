package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.Equation;
import fr.jamgotchian.olf.vector.BusActivePowerTargetEquationArray;
import fr.jamgotchian.olf.vector.BusReactivePowerTargetEquationArray;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealNetworkBenchmark {

  //  @Benchmark
    public void objectModel(Rte6515NetworkState state, Blackhole bh) {
        List<Equation<AcVariableType, AcEquationType>> equations = state.getEquations();
        double[] values = new double[equations.size()];
        for (int i = 0; i < equations.size(); i++) {
            values[i] = equations.get(i).eval();
        }
        bh.consume(values);
    }

//    @Benchmark
    public void objectModelDer(Rte6515NetworkState state, Blackhole bh) {
        List<Equation<AcVariableType, AcEquationType>> equations = state.getEquations();
        double[] values = new double[equations.size() * 7];
        int[] index = new int[1];
        for (Equation<AcVariableType, AcEquationType> equation : equations) {
            equation.der((variable, value, matrixElementIndex) -> {
                values[index[0]++] = value;
                return index[0];
            });
        }
        bh.consume(values);
    }

    @Benchmark
    public void arrayModel(Rte6515NetworkState state, Blackhole bh) {
        state.getNetworkVector().updateState(state.getVariableVector(), state.getEquationSystem().getStateVector());
        BusActivePowerTargetEquationArray p = new BusActivePowerTargetEquationArray(state.getNetworkVector().getBusVector());
        p.setFirstColumn(0);
        BusReactivePowerTargetEquationArray q = new BusReactivePowerTargetEquationArray(state.getNetworkVector().getBusVector());
        q.setFirstColumn(p.getLength());
        double[] values = new double[state.getNetworkVector().getBusVector().getSize() * 2];
        p.eval(values);
        q.eval(values);
        bh.consume(values);
    }

//    @Benchmark
    public void arrayModelDer(Rte6515NetworkState state, Blackhole bh) {
        state.getNetworkVector().updateState(state.getVariableVector(), state.getEquationSystem().getStateVector());
        BusActivePowerTargetEquationArray p = new BusActivePowerTargetEquationArray(state.getNetworkVector().getBusVector());
        p.setFirstColumn(0);
        BusReactivePowerTargetEquationArray q = new BusReactivePowerTargetEquationArray(state.getNetworkVector().getBusVector());
        q.setFirstColumn(p.getLength());
        double[] values = new double[state.getNetworkVector().getBusVector().getSize() * 7];
        int[] index = new int[1];
        p.der((column, variable, value, matrixElementIndex) -> {
            values[index[0]++] = value;
            return index[0];
        });
        q.der((column, variable, value, matrixElementIndex) -> {
            values[index[0]++] = value;
            return index[0];
        });
        bh.consume(values);
    }
}
