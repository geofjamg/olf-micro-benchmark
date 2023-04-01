package fr.jamgotchian.olf;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.Equation;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RealNetworkBenchmark {

   // @Benchmark
    public void objectModel(Rte6515NetworkState state) {
        List<Equation<AcVariableType, AcEquationType>> equations = state.getEquations();
        double[] values = new double[equations.size()];
        for (int i = 0; i < equations.size(); i++) {
            values[i] = equations.get(i).eval();
        }
    }

    @Benchmark
    public void arrayModel(Rte6515NetworkState state) {
        BusActivePowerTargetEquationArray equationArray = state.getEquationArray();
        double[] values = new double[state.getLfNetwork().getBuses().size()];
        equationArray.eval(values);
    }
}
