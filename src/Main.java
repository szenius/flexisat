
import data_structures.Clauses;
import data_structures.Variable;
import parser.Parser;

import java.util.Set;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // todo: if this gets complicated, we can define an object class for input args
        Parser parser = new Parser(filename);
        Clauses form = parser.getClauses();
    }
}
