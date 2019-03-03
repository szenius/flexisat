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

    public Parser(String filePath) {
        this.variables = new HashSet<>();
        this.varIds = new HashSet<>();
        this.parse(filePath);
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
            // First line: comment line
            line = br.readLine();
            // Second line: p
            line = br.readLine();
            String[] secondLine = line.split(" ");
            int numVariables = Integer.parseInt(secondLine[2]);
            int numClauses = Integer.parseInt(secondLine[3]);

            List<Clause> clauses = new ArrayList<Clause>();
            for (int i = 0 ; i < numClauses; i++ ) {
                line = br.readLine();
                try {
                    clauses.add(createClause(line));
                } catch (Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            this.clauses = new Clauses(clauses);
            br.close();
            System.out.println("Done!");
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
        String[] splitLine = line.split(" ");
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