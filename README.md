# SAT Solver

## User Guide
### Prerequisites 
Our project was built with Gradle. Hence, please first have Gradle installed.

Once Gradle is installed, run the following commands in the project root directory to build the project jar.

```
gradle clean
gradle fatJar
```

Alternatively, if you have this project setup in your IDE, you can click on the `clean` and `fatJar` tasks in your Gradle toolbar as well. For IntelliJ, the specific steps are:
1. In the top navigation bar, click on View > Tool Windows > Gradle. The Gradle toolbar should pop out on your left.
2. Click on Tasks > build > clean
3. Click on Tasks > other > fatJar

### Running the SAT Solver
In the project root directory, run this:
```
java -jar build/libs/sat-solver-all-1.0.jar <filename> <pick_branching_type> <conflict_analsyer_type>

# e.g.
# java -jar build/libs/sat-solver-all-1.0.jar input/sat/sat_input1.cnf seq single_uip

# e.g. with optional parameters (format: key=value)
# java -jar build/libs/sat-solver-all-1.0.jar input/sat/sat_input1.cnf seq single_uip bump=1 
```
* `filename`: A CNF file following the DIMACS format
* `pick_branching_type`: "seq", "random", "two_clause", "vsids", "chaff", "minisat". Please see [Branch Pickers](#branch-pickers) for more explanation.
* `conflict_analyser_type`: "single_uip", "no_uip", "direct", or "roots". Please see [Conflict Analysers](#conflict-analysers) for more explanation.
* (Optional) `bump`: any integer that is at least 1. Used for `vsids` pick branching. Corresponds to the additive factor applied to the variable weights every time a conflict happens. 
* (Optional) `decay_factor`: any double from 0 to 1. Used for `vsids` pick branching. Corresponds to the multiplicative factor applied to the variable weights.
* (Optional) `decay_interval`: any integer that is at least 1. Used for `vsids` pick branching. Corresponds to the number of conflicts before a decay is applied to the variable weights.

#### Branch Pickers
We have various implementations of how the solver picks the next variable for assignment. 
* `seq`: Pick sequentially by numerical order of variable IDs.
* `random`: Pick a random variable from the list of unassigned variables.
* `two_clause`: From all clauses with only two unassigned variables, pick the variable which occurs the most. If there are no such clauses, default to using `random` branch picker. If there are more than one most occurring variable, pick any with equal probability.
* `vsids`: Implementation follows the description in [this paper](https://arxiv.org/pdf/1506.08905.pdf). This option allows you to set your `bump`, `decay_factor` and `decay_interval` through optional parameters.
* `chaff`: Implementation adapted from [this paper](https://arxiv.org/pdf/1506.08905.pdf) and [slide 71 in this deck](http://fmv.jku.at/biere/talks/Biere-VTSA12-talk.pdf). Equivalent to `vsids` with set `bump=1`, `decay_factor=0.5`, `decay_interval=256`.
* `minisat`: Implementation adapted from [this paper](https://arxiv.org/pdf/1506.08905.pdf). Equivalent to `vsids` with set `bump=1`, `decay_factor=0.95`, `decay_interval=1`, and with weights update applied to variables resolved in learning. 

#### Conflict Analysers
We have various implementations of how the solver learns clauses while analysing conflicts. 
* `single_uip`: Get the literals that directly lead to the conflict site, then perform resolution until there is only one literal in the learnt clause that is at the conflict decision level. Please refer to [Section 4.4.3 in this book](https://www.cis.upenn.edu/~alur/CIS673/sat-cdcl.pdf) to find out more.
* `no_uip`: Get the literals that directly lead to the conflict site, then perform resolution until there are no literals in the learnt clause that is at the conflict decision level. Please refer to [Section 4.4.1 in this book](https://www.cis.upenn.edu/~alur/CIS673/sat-cdcl.pdf) to find out more.
* `direct`: Get the literals that directly lead to the conflict site, and use that as the learnt clause.
* `roots`: Use the ancestors of the conflicting nodes as the learnt clause.

## Developer Guide
### Installation
Please ensure all relevant tools to run Java on your machine are set up. 

We built this project with Gradle. To import this project in your IDE, choose the relevant options to import a Gradle project using our `build.gradle` file.

No other special installations are required.

### Adding a Branch Picker
1. Add a new class in `src/main/java/branch_pickers`. 
    1. For VSIDS based branch pickers, this class can extend the `VSIDSBranchPicker.java` class. It should also be in `src/main/java/branch_pickers/vsids`.
    2. Otherwise, this class needs to **extend** the `BranchPicker.java` abstract class.
2. Add a new enum field in `BranchPickerType.java` for your new branch picker.
3. In `Parser.java`, add your branch picker to the `switch` statement in `setBranchPicker`.
4. Add documentation in the [User Guide](#branch-pickers).

### Adding a Conflict Analyzer
1. Add a new class in `src/main/java/conflict_analysers`. 
    1. For UIP based conflict analysers, this class needs to **extend** the `UIPConflictAnalyser.java` abstract class. The class should also be in `src/main/java/conflict_analysers/uip`.
    2. For other conflict analysers, **extend** the `ExtendedConflictAnalyser.java` abstract class.
2. Add a new enum field in `ConflictAnalyserType.java` for your new conflict analyser.
3. In `Parser.java`, add your conflict analyser to the `switch` statement in `setConflictAnalyser`.
4. Add documentation in the [User Guide](#conflict-analysers).

## CNF Generator
**MORE INSTRUCTIONS COMING SOON**

### Check CNF on CrytoMiniSat
`cat myfile.cnf | docker run --rm -i msoos/cryptominisat`

## Future Work
**COMING SOON**

## References
* [Conflict Driven Clause Learning Solvers](https://www.cis.upenn.edu/~alur/CIS673/sat-cdcl.pdf)