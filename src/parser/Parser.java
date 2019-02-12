package parser;

import data_structures.Clause;
import data_structures.Formula;
import data_structures.Literal;
import data_structures.Variable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    final static int CNF_TYPE = 3;

    public static Formula parse(String filePath) {
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
            int numVariables = Integer.parseInt(secondLine[1]);
            int numClauses = Integer.parseInt(secondLine[2]);

            List<Clause> clauses = new ArrayList<Clause>();
            for (int i = 0 ; i < numClauses; i++ ) {
                line = br.readLine();
                try {
                    clauses.add(createClause(line));
                } catch (Exception e){
                    System.out.print(e.getMessage());
                    System.exit(1);
                }
            }
            Formula form = new Formula(clauses);
            br.close();
            return form;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static Clause createClause(String line) throws Exception { 
        if (line == null){
            throw new Exception("Clause does not exist.");
        }  
        String[] splitLine = line.split(" ");
        if (splitLine.length != CNF_TYPE) {
            throw new Exception("Clause size is not 3.");
        }
        List<Literal> literals = new ArrayList<Literal>();
        for (int i = 0; i < CNF_TYPE; i++ ) {
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
// # Have a predefined list of CNF formulas
