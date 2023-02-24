```
Benchmark                                                                                                        Mode  Cnt     Score    Error  Units
EquationTermBenchmark.objectModelShuffled                                                                        avgt    5  3251,524 ± 89,873  us/op
EquationTermBenchmark.objectModelSortedByBranch                                                                  avgt    5  1551,126 ± 19,797  us/op
EquationTermBenchmark.objectModelSortedByBranchAndStoreResults                                                   avgt    5  2101,806 ± 58,153  us/op
EquationTermBenchmark.staticMethodSortedByBranch                                                                 avgt    5   581,066 ±  1,681  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePower                             avgt    5   200,953 ±  0,479  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResult               avgt    5   188,918 ±  2,321  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResultOnlyOneCosSin  avgt    5   205,125 ±  0,087  us/op
EquationTermBenchmark.staticMethodSortedByTermType                                                               avgt    5   694,702 ±  2,119  us/op
```
