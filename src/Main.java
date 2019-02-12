import data_structures.Formula;
import parser.Parser;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // todo: if this gets complicated, we can define an object class for input args
        Formula form = Parser.parse(filename);
    }
}
