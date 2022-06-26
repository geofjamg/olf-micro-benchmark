```
Benchmark                                                                                                        Mode  Cnt     Score      Error  Units
EquationTermBenchmark.objectModelShuffled                                                                        avgt    5  3269,062 ±   15,702  us/op
EquationTermBenchmark.objectModelSortedByBranch                                                                  avgt    5  1612,496 ±  325,020  us/op
EquationTermBenchmark.objectModelSortedByBranchAndStoreResults                                                   avgt    5  2434,113 ± 1003,407  us/op
EquationTermBenchmark.staticMethodSortedByBranch                                                                 avgt    5   592,683 ±   38,069  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePower                             avgt    5   224,712 ±    1,085  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResult               avgt    5   209,735 ±    0,974  us/op
EquationTermBenchmark.staticMethodSortedByBranchWithCurrentCalculatedFromActivePowerAndStoreResultOnlyOneCosSin  avgt    5   220,861 ±    1,090  us/op
EquationTermBenchmark.staticMethodSortedByTermType                                                               avgt    5   697,439 ±   15,817  us/op
```
