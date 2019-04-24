package parser;

import branch_pickers.BranchPicker;
import branch_pickers.BranchPickerType;
import branch_pickers.SequentialBranchPicker;
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

    private Clauses clauses;
    private Set<Variable> variables;
    private BranchPicker branchPicker;
    private ConflictAnalyser conflictAnalyser;

    public Parser(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Wrong input format.\nUsage: <filename> <pick_branching_type> <conflict_analyser_type>");
        }
        this.variables = new HashSet<>();

        parse(args[0]);
        setBranchPicker(args[1]);
        setConflictAnalyser(args[2]);
    }

    private Clauses parse(String filename) {
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
            int numClauses = Integer.parseInt(tokens[3]);

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
            default:
                conflictAnalyser = new SingleUIPConflictAnalyser();
        }
    }

    private void setBranchPicker(String branchPickerType) {
        switch (Enum.valueOf(BranchPickerType.class, branchPickerType.toUpperCase())) {
            case SEQ:
            default:
                branchPicker = new SequentialBranchPicker(variables);
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
}