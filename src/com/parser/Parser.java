package com.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import com.data_structures.Formula;
import com.data_structures.Literal;
import com.data_structures.Clause;
import com.data_structures.Variable;

class Parser {

    final static int CNF_TYPE = 3;

    public static Formula parse(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        
        String line;
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
                clauses.addClause(createClause(line));
            } catch (Exception e){
                System.out.print(e.getMessage());
                System.exit(1);
            }
        }
        Formula form = new Formula(clauses);
        br.close();
        return form;
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
            literal = new Literal(variable.getId(), true);
        } else {
            literal = new Literal(variable.getId(), false);
        }
        return literal;
    }

}
// # Have a predefined list of CNF formulas
