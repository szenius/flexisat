package cnf_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CNFGenerator {

    public enum GENERATOR_TYPE {
        EINSTEIN, THREE_CNF,
    }

    public static void main(String[] args) {
        GENERATOR_TYPE generatorType = null;
        System.out.println(args[0]);
        if (args.length == 1 &&
            args[0].equals("einstein")) {
            generatorType = GENERATOR_TYPE.EINSTEIN;
            createCNFFile("0", "0" , generatorType);
        } else if (args.length != 2) {
            System.out.println("Missing program arguments.");
        } else {
            generatorType = GENERATOR_TYPE.THREE_CNF;
            String numVariables = args[0];
            String numClauses = args[1];
            if (numVariables.equals("") || numClauses.equals("")) {
                System.out.println("Error. We require 2 arguments for numVariables and numClauses respectively.");
                System.exit(1);
            }
            createCNFFile(numVariables, numClauses, generatorType);
        }
    }

    private static void createCNFFile(String numVariables, String numClauses, GENERATOR_TYPE generatorType) {
        // Lazy way of creating file name
        String fileName = "";
        if (generatorType == GENERATOR_TYPE.EINSTEIN){
            fileName = "einstein.cnf";
        } else {
            fileName = numVariables + "var-" + numClauses + ".cnf";
        }
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);

                switch (generatorType) {
                    case THREE_CNF:
                        String firstLine = "c Generated CNF file\n";
                        String secondLine = "p cnf " + numVariables + " " + numClauses + "\n";
                        writer.write(firstLine);
                        writer.write(secondLine);
                        for (int i = 0; i < Integer.parseInt(numClauses); i++) {
                            String line = generate3CNFClause(Integer.parseInt(numVariables));
                            writer.write(line);
                        }
                        break;
                    case EINSTEIN:
                        firstLine = "c Generated Einstein file \n";
                        // TODO: Calculate
                        double numEinsteinVariables = 4 * Math.pow(5,3);
                        secondLine = "p cnf " + (int)numEinsteinVariables + " " + numClauses + "\n";
                        writer.write(firstLine);
                        writer.write(secondLine);
                        generateEinsteinConstraints(writer);


                        break;
                    default:
                        System.out.println("Invalid generator type. Please check.");
                }

                writer.close();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error in creating file.");
            System.exit(1);
        }
    }

    private static void generateEinsteinConstraints(FileWriter writer) {
        // Indicator Variables will take up literal Ids from 1 - numIndicatorVariables
        try {
            System.out.println("Generating Einstein Constraints now.");
            EinsteinGeneratorHelper.writeType1Constraints(writer);
            //EinsteinGeneratorHelper.writeType2Constraints(writer);
            EinsteinGeneratorHelper.writeType3Constraints(writer);
            EinsteinGeneratorHelper.writeType4Constraints(writer);
            EinsteinGeneratorHelper.writeType5Constraints(writer);
            EinsteinGeneratorHelper.writeType6Constraints(writer);
            EinsteinGeneratorHelper.writeType7Constraints(writer);
            EinsteinGeneratorHelper.writeType8Constraints(writer);
            EinsteinGeneratorHelper.writeType9Constraints(writer);
            EinsteinGeneratorHelper.writeType10Constraints(writer);
            EinsteinGeneratorHelper.writeType11Constraints(writer);
            EinsteinGeneratorHelper.writeType12Constraints(writer);
            EinsteinGeneratorHelper.writeType13Constraints(writer);
            EinsteinGeneratorHelper.writeType14Constraints(writer);
            EinsteinGeneratorHelper.writeType15Constraints(writer);
            EinsteinGeneratorHelper.writeType16Constraints(writer);
            EinsteinGeneratorHelper.writeType17Constraints(writer);
            System.out.println("Finished generating Einstein constraints.");
        } catch (IOException e){
            e.printStackTrace();
        }



    }






    /**
     * This function generates a string line of clause randomly with equal probabilities across the
     * total number of variables and a 0.5% chance that the literal will be negated.
     * @param numVariables
     * @return
     */
    private static String generate3CNFClause(int numVariables) {
        StringBuilder clause = new StringBuilder();
        for (int i = 0 ; i < 3 ; i++) {
            int variableToChoose = (int) (Math.random() * numVariables + 1);
            int isFalse = (int) (Math.random() * 2);
            if (isFalse == 0) {
                clause.append(Integer.toString(variableToChoose));
            } else {
                clause.append("-");
                clause.append(Integer.toString(variableToChoose));
            }
            if (i == 2) {
                clause.append(" 0\n");
            } else {
                clause.append(" ");
            }
        }
        return clause.toString();
    }
}
