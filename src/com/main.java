package com.main;

class Main {
    public static void main(String[] args) {
        String filename = args[0]; // todo: if this gets complicated, we can define an object class for input args
        Formula form = Parser.parse(filename);
    }
}

// import parser
// import algorithms.CDCL.cdcl

// # note: form variable should be in DIMACS CNF format.
// def main():
//     form = parser.readFile
//     form, v = parser.readParameters()
//     algorithms.CDCL.cdcl.CDCL(form, v)

// if __name__ == "__main__":
//     main()