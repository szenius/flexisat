package parser;

import branch_pickers.*;
import branch_pickers.vsids.ChaffVSIDSBranchPicker;
import branch_pickers.vsids.MiniSATVSIDSBranchPicker;
import branch_pickers.vsids.VSIDSBranchPicker;
import conflict_analysers.*;
import conflict_analysers.uip.NoUIPConflictAnalyser;
import conflict_analysers.uip.SingleUIPConflictAnalyser;
import data_structures.Clause;
import data_structures.Clauses;
import data_structures.Literal;
import data_structures.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    private static final int FILENAME_ARGS_INDEX = 0;
    private static final int BRANCH_PICKER_ARGS_INDEX = 1;
    private static final int CONFLICT_ANALYSER_ARGS_INDEX = 2;
    private static final int OPTIONAL_PARAMS_START_INDEX = 3;


    private int numClauses;
    private int numVariables;
    private Clauses clauses;
    private Set<Variable> variables;
    private BranchPicker branchPicker;
    private ConflictAnalyser conflictAnalyser;

    private double decayFactor;
    private int bump;
    private int decayInterval;
    private boolean clauseDeletion;

    public Parser(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Wrong input format.\nUsage: <filename> <pick_branching_type> <conflict_analyser_type> <optional_params>");
        }
        this.variables = new HashSet<>();
        init(args);
    }

    // DO NOT CHANGE ORDER IN THIS METHOD
    private void init(String[] args) {
        setOptionalParameters(args);
        parseCnfFile(args[FILENAME_ARGS_INDEX]);
        setBranchPicker(args[BRANCH_PICKER_ARGS_INDEX]);
        setConflictAnalyser(args[CONFLICT_ANALYSER_ARGS_INDEX]);
    }

    private void setOptionalParameters(String[] args) {
        // Default configurations
        decayFactor = 0.5;
        decayInterval = 1;
        bump = 1;
        clauseDeletion = true;

        // Replace with user configurations, if any
        for (int i = OPTIONAL_PARAMS_START_INDEX; i < args.length; i++) {
            String[] tokens = args[i].trim().split("=");

            if (tokens.length != 2) {
                throw new IllegalArgumentException("Wrong format for optional parameters! Expected format: key=value");
            }

            switch(tokens[0]) {
                case "decay_factor":
                    decayFactor = Double.parseDouble(tokens[1]);
                    break;
                case "decay_interval":
                    decayInterval = Integer.parseInt(tokens[1]);
                    break;
                case "bump":
                    bump = Integer.parseInt(tokens[1]);
                    break;
                case "clause_deletion":
                    clauseDeletion = Boolean.parseBoolean(tokens[1]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid optional parameter key " + tokens[0]);
            }
        }
    }

    private Clauses parseCnfFile(String filename) {
        File file = new File(filename);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        
        String line;
        try {
            // Read all comment lines
            line = br.readLine();
            while (line.startsWith("c")) {
                line = br.readLine();
            }

            // First line after comment lines should start with "p"
            String[] tokens = line.trim().split("\\s+");
            numVariables = Integer.parseInt(tokens[2]);
            numClauses = Integer.parseInt(tokens[3]);

            // Read in clauses
            Set<Clause> clauseSet = new HashSet<>();
            for (int i = 0 ; i < numClauses; i++ ) {
                line = br.readLine();
                try {
                    clauseSet.add(createClause(line));
                } catch (Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            clauses = new Clauses(clauseSet);

            br.close();
            LOGGER.debug("Parsed input file {}", filename);
            return clauses;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private void setConflictAnalyser(String conflictAnalyserType) {
        switch (Enum.valueOf(ConflictAnalyserType.class, conflictAnalyserType.toUpperCase())) {
            case DIRECT:
                conflictAnalyser = new DirectCutConflictAnalyser();
                break;
            case ROOTS:
                conflictAnalyser = new RootsCutConflictAnalyser();
                break;
            case NO_UIP:
                conflictAnalyser = new NoUIPConflictAnalyser();
                break;
            case SINGLE_UIP:
                conflictAnalyser = new SingleUIPConflictAnalyser();
                break;
            default:
                throw new IllegalArgumentException("Conflict Analyser " + conflictAnalyserType + " does not exist!");
        }
    }

    private void setBranchPicker(String branchPickerType) {
        switch (Enum.valueOf(BranchPickerType.class, branchPickerType.toUpperCase())) {
            case RANDOM:
                branchPicker = new RandomBranchPicker(variables);
                break;
            case SEQ:
                branchPicker = new SequentialBranchPicker(variables);
                break;
            case TWO_CLAUSE:
                branchPicker = new TwoClauseBranchPicker(variables, clauses);
                break;
            case VSIDS:
                branchPicker = new VSIDSBranchPicker(variables, decayFactor, bump, decayInterval);
                break;
            case CHAFF:
                branchPicker = new ChaffVSIDSBranchPicker(variables);
                break;
            case MINISAT:
                branchPicker = new MiniSATVSIDSBranchPicker(variables);
                break;
            default:
                throw new IllegalArgumentException("Branch Picker " + branchPickerType + " does not exist!");
        }
    }

    private Clause createClause(String line) {
        if (line == null){
            throw new IllegalArgumentException("Cannot create clause from NULL line.");
        }

        String[] tokens = line.trim().split("\\s+");

        if (Integer.parseInt(tokens[tokens.length - 1]) != 0) {
            // Line did not end with 0. Unexpected format
            throw new IllegalArgumentException("Format of clause is incorrect. Last number of the line should be 0.");
        }

        // Collect literals from line and create new clause
        List<Literal> literals = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++ ) {
            int literalValue = Integer.parseInt(tokens[i]);
            literals.add(createLiteral(literalValue));
            Variable variable = new Variable(Math.abs(literalValue));
            addVariable(variable);
        }
        return new Clause(literals);
    }

    private static Literal createLiteral(int value) {
        Variable variable = new Variable(Math.abs(value));
        Literal literal;
        if (value < 0) {
            literal = new Literal(variable, true);
        } else {
            literal = new Literal(variable, false);
        }
        return literal;
    }

    private void addVariable(Variable variable) {
        variables.add(variable);
    }

    public Clauses getClauses() {
        return this.clauses;
    }

    public Set<Variable> getVariables() {
        return this.variables;
    }

    public BranchPicker getBranchPicker() {
        return branchPicker;
    }

    public ConflictAnalyser getConflictAnalyser() {
        return conflictAnalyser;
    }

    public int getNumClauses() {
        return numClauses;
    }

    public int getNumVariables() {
        return numVariables;
    }

    public boolean getClauseDeletion() {
        return clauseDeletion;
    }
}