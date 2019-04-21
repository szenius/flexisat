package parser;

import data_structures.Clause;
import data_structures.Clauses;
import data_structures.Literal;
import data_structures.Variable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

    private final static int EXPECTED_CLAUSE_SIZE = 3;

    private Clauses clauses;
    private Set<Variable> variables;
    private Set<Integer> varIds;

    public Parser() {
        this.variables = new HashSet<>();
        this.varIds = new HashSet<>();
    }

    public Clauses getClauses() {
        return this.clauses;
    }

    public Set<Variable> getVariables() {
        return this.variables;
    }

    public Set<Integer> getVarIds() {
        return this.varIds;
    }

    public Clauses parse(String filePath) {
        System.out.println("Trying to parse CNF file " + filePath);

        File file = new File(filePath);
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
            System.out.println("Read: " + line);
            while (line.startsWith("c")) {
                line = br.readLine();
                System.out.println("Read: " + line);
            }

            System.out.println("Finished");

            // First line after comment lines should start with "p"
            String[] secondLine = line.trim().split("\\s+");
            int numClauses = Integer.parseInt(secondLine[3]);

            // Read in clauses
            Set<Clause> clauses = new HashSet<>();
            for (int i = 0 ; i < numClauses; i++ ) {
                line = br.readLine();
                System.out.println("Found clause line " + line);
                try {
                    clauses.add(createClause(line));
                } catch (Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            this.clauses = new Clauses(clauses);
            br.close();
            System.out.println("Parsed input file.");
            return this.clauses;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private Clause createClause(String line) throws Exception {
        if (line == null){
            throw new Exception("Clause does not exist.");
        }  
        String[] splitLine = line.trim().split("\\s+");
        // The last number of each line should be 0
        if (splitLine.length != EXPECTED_CLAUSE_SIZE + 1) {
            System.out.println(splitLine.length);
            throw new Exception("Clause size is not " + EXPECTED_CLAUSE_SIZE);
        }
        if (Integer.parseInt(splitLine[3]) != 0) {
            throw new Exception("Format of clause is incorrect. Last number of the line should be 0.");
        }
        List<Literal> literals = new ArrayList<Literal>();
        // Temporarily stores the variables
        for (int i = 0; i < EXPECTED_CLAUSE_SIZE; i++ ) {
            int literalValue = Integer.parseInt(splitLine[i]);
            Variable variable = new Variable(Math.abs(literalValue));
            this.variables.add(variable);
            this.varIds.add(Math.abs(literalValue));
            literals.add(createLiteral(Integer.parseInt(splitLine[i])));
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
}