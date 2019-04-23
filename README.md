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
# java -jar build/libs/sat-solver-all-1.0.jar input/sat/sat_input1.cnf seq uip
```
* `filename`: A CNF file following the DIMACS format
* `pick_branching_type`: How variables should be picked for assignment. Can be "seq". Please see [Branch Pickers](#branch-pickers) for more explanation.
* `conflict_analyser_type`: How clauses should be learnt during a conflict. Can be "uip". Please see [Conflict Analysers](#conflict-analysers) for more explanation.

#### Branch Pickers
We have various implementations of how the solver picks the next variable for assignment. 
* `seq`: Pick sequentially by numerical order of variable IDs.

#### Conflict Analysers
We have various implementations of how the solver learns clauses while analysing conflicts. 
* `uip`: Get the literals that directly lead to the conflict site, then perform resolution until there is only one literal in the learnt clause that is at the conflict decision level. Please refer to [Section 4.4.3 in this book](https://www.cis.upenn.edu/~alur/CIS673/sat-cdcl.pdf) to find out more.

## Developer Guide
### Installation
Please ensure all relevant tools to run Java on your machine are set up. 

We built this project with Gradle. To import this project in your IDE, choose the relevant options to import a Gradle project using our `build.gradle` file.

No other special installations are required.

### Adding a Branch Picker
1. Add a new class in `src/main/java/branch_pickers`. This class needs to **implement** the `BranchPicker.java` interface.
2. Add a new enum field in `BranchPickerType.java` for your new branch picker.
3. In `Parser.java`, add your branch picker to the `switch` statement in `setBranchPicker`.
4. Add documentation in the [User Guide](#branch-pickers).

### Adding a Conflict Analyzer
1. Add a new class in `src/main/java/conflict_analysers`. This class needs to **extend** the `ResolutionBasedConflictAnalyser.java` abstract class OR **implement** the `ConflictAnalyser.java` interface.
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