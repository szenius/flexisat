package cnf_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EinsteinGeneratorHelper {

    private static int colourOffset = 0;
    private static int beverageOffset = (int)Math.pow(5,3);
    private static int petOffset = 2 * (int)Math.pow(5,3);
    private static int gCigarOffset = 3 * (int)Math.pow(5,3);

    private static String DEBUG = "==========================DEBUG===================== ";

    // Every house can only have one Colour, Beverage, Pet and Cigar.
    public static void writeType1Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 1\n");
        int[] types = {colourOffset, beverageOffset, petOffset, gCigarOffset};
        for (int type = 0 ; type < types.length ; type++ ) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    for (int k = 0; k < 5; k++) {
                        int leftVar = types[type] + (i*25) + (j*5) + (k+1);
                        StringBuilder leftImplication = new StringBuilder();
                        leftImplication.append(leftVar).append(" ");

                        for (int x = 0 ; x < 5; x++) {
                            int rightVar = 0;
                            // Not conflict with j
                            if (x != k) {
                                rightVar = types[type] + (i*25) + (j*5) + (x+1);
                                leftImplication.append(rightVar).append(" ");
                                writer.write("-" + leftVar + " -" + rightVar + " 0\n");
                            }
                        }
                        writer.write(leftImplication.append("0\n").toString());
                    }
                }
            }
        }
    }

    // Every Nationality can only be matched to one House Order
    public static void writeType2Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 2\n");
        int[] types = {colourOffset, beverageOffset, petOffset, gCigarOffset};
        for (int type = 0 ; type < types.length ; type++ ) {
            for (int i = 0 ; i < 5; i++) {
                for (int j = 0 ; j < 5 ; j++) {
                    for (int k = 0 ; k < 5; k++) {
                        int leftVar = types[type] + (i*25) + (j*5) + (k+1);

                        StringBuilder leftImplication = new StringBuilder();
                        leftImplication.append(leftVar).append(" ");

                        for (int x = 0 ; x < 5; x++) {
                            int rightVar = 0;
                            // Not conflict with j
                            if (x != j) {
                                rightVar = types[type] + (i*25) + (x*5) + (k+1);
                                leftImplication.append(rightVar).append(" ");
                                writer.write("-" + leftVar + " -" + rightVar + " 0\n");
                            }
                            // Not conflict with i
                            if (x != i) {
                                rightVar = types[type] + (x*25) + (j*5) + (k+1);
                                leftImplication.append(rightVar).append(" ");
                                writer.write("-" + leftVar + " -" + rightVar + " 0\n");
                            }

                        }
                        writer.write(leftImplication.append("0\n").toString());
                    }
                }
            }
        }
    }

    // The Brit lives in the red house
    public static void writeType3Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 3\n");
        StringBuilder clause = new StringBuilder();
        for (int j = 0 ; j < 5 ; j++) {
            int literal = (0*25) + (j*5) + 1;
            clause.append(literal).append(" ");

        }
        writer.write(clause.append("0\n").toString());
    }

    // The Swede keeps dogs as pets
    public static void writeType4Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 4\n");
        StringBuilder clause = new StringBuilder();
        for (int j = 0 ; j < 5 ; j++) {
            int literal = petOffset + (1*25) + (j*5) + 1;
            clause.append(literal).append(" ");

        }
        writer.write(clause.append("0\n").toString());
    }

    // The Dane drinks tea
    public static void writeType5Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 5\n");
        StringBuilder clause = new StringBuilder();
        for (int j = 0 ; j < 5 ; j++) {
            int literal = beverageOffset + (2*25) + (j*5) + 1;
            clause.append(literal).append(" ");

        }
        writer.write(clause.append("0\n").toString());
    }

    // The Green house is on the left of the White house
    public static void writeType6Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 6\n");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                // Right implication
                int leftIndicatorVar = (i * 25) + (j * 5) + 2;
                int rightIndicatorVar = (i * 25) + ((j+1)*5) + 3;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }

        for (int i = 0; i < 5; i++) {
            int indicatorVariable = (i * 25) + (4*5) + 2;
            String clause = "-" + indicatorVariable + " 0\n";
            writer.write(clause);
        }
    }

    // The Green house's owner drinks coffee
    public static void writeType7Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 7\n");
        for (int i = 0 ; i < 5 ; i++) {
            for (int j = 0 ; j < 5 ; j++) {
                int leftIndicatorVar = colourOffset + (j*5) + (i*25) + 2;
                int rightIndicatorVar = beverageOffset + (j*5) + (i*25) + 2;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }
    }

    // The person who smokes Pall Mall rears bird
    public static void writeType8Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 8\n");
        for (int i = 0 ; i < 5 ; i++ ) {
            for (int j = 0 ; j < 5; j++) {
                int leftIndicatorVar = colourOffset + (j*5) + (i*25) + 1;
                int rightIndicatorVar = petOffset + (j*5) + (i*25) + 2;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }
    }

    // The owner of the yellow house smokes Dunhill
    public static void writeType9Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 9\n");
        for (int i = 0 ; i < 5; i++) {
            for (int j = 0 ; j < 5 ; j++) {
                int leftIndicator = colourOffset + (i*25) + (j*5) + 4;
                int rightIndicator = gCigarOffset + (i*25) + (j*5) + 2;
                writeBidirectionalImplication(writer, leftIndicator, rightIndicator);
            }
        }
    }

    // The man living in the center house drinks milk
    public static void writeType10Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 10\n");
        StringBuilder clause = new StringBuilder();
        for (int i = 0 ; i < 5; i++) {
            int indicatorVariable = beverageOffset + (i * 25) + (2*5) + 5;
            clause.append(indicatorVariable).append(" ");
        }
        writer.write(clause.append("0\n").toString());
    }

    // The Norwegian lives in the first house
    public static void writeType11Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 11\n");
        StringBuilder clause = new StringBuilder();
        for (int k = 0 ; k < 5; k++) {
            int indicatorVariable = colourOffset + (3 * 25) + (0*5) + k;
            clause.append(indicatorVariable).append(" ");
        }
        writer.write(clause.append("0\n").toString());
    }

    // The man who smokes Blends lives next to the one who keeps cats
    public static void writeType12Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 12\n");
        writeNextToConstraints(writer, gCigarOffset, petOffset, 3, 3);
    }

    // The man who keeps the horse lives next to the man who smokes Dunhill
    public static void writeType13Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 13\n");
        writeNextToConstraints(writer, petOffset, gCigarOffset, 4, 2);
    }

    // The owner who smokes Bluemasters drink beer
    public static void writeType14Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 14\n");
        for (int i = 0 ; i < 5 ; i++ ){
            for (int j = 0 ; j < 5 ; j++ ){
                int leftIndicator = gCigarOffset + (i * 25) + (j * 5) + 4;
                int rightIndicator = beverageOffset + (i * 25) + (j * 5) + 3;
                writeBidirectionalImplication(writer, leftIndicator, rightIndicator);
            }
        }
    }

    // The German smokes Prince
    public static void writeType15Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 15\n");
        StringBuilder constraint = new StringBuilder();
        for (int j = 0 ; j < 5 ; j++) {
            int indicator = gCigarOffset + (4*25) + (j*5) + 5;
            constraint.append(indicator).append(" ");
        }
        writer.write(constraint.append("0\n").toString());
    }

    // The Norwegian lives next to the blue house
    public static void writeType16Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 16\n");
        // First type.
        for (int k = 0 ; k < 5 ; k++) {
            for (int j = 0; j < 5; j++) {
                int leftVarTypeOne = colourOffset + (3*25) + (j*5) + (k+1);
                StringBuilder rightImplicationTypeOne = new StringBuilder();

                int leftVarTypeTwo = colourOffset + (k*25) + (j*5) + 5;
                StringBuilder rightImplicationTypeTwo = new StringBuilder();

                for (int x = 0 ; x < 5 ; x++){
                    // This is for type one.
                    if (x != 3) {
                        int rightVarTypeOne;
                        if (j != 0) {
                            rightVarTypeOne = colourOffset + (x*25) + ((j-1)*5) + 5;
                            rightImplicationTypeOne.append(rightVarTypeOne).append(" ");
                        }
                        if (j != 4) {
                            rightVarTypeOne = colourOffset + (x*25) + ((j+1)*5) + 5;
                            rightImplicationTypeOne.append(rightVarTypeOne).append(" ");
                        }
                    }
                    // This is for type two.
                    if (x != 4) {
                        int rightVarTypeTwo;
                        if (j != 0) {
                            rightVarTypeTwo = colourOffset + (3*25) + ((j-1)*5) + (x+1);
                            rightImplicationTypeTwo.append(rightVarTypeTwo).append(" ");
                        }
                        if (j != 4) {
                            rightVarTypeTwo = colourOffset + (3*25) + ((j+1)*5) + (x+1);
                            rightImplicationTypeTwo.append(rightVarTypeTwo).append(" ");
                        }
                    }
                }
                writer.write("-" + leftVarTypeOne + " " + rightImplicationTypeOne.append("0\n").toString());
                writer.write("-" + leftVarTypeTwo + " " + rightImplicationTypeTwo.append("0\n").toString());
            }
        }
    }


    // The man who smokes Blends has a neighbour who drinks water
    public static void writeType17Constraints(FileWriter writer) throws IOException {
        //writer.write(DEBUG + "TYPE 17\n");
        writeNextToConstraints(writer, gCigarOffset, beverageOffset, 3, 4);
    }


    // Used for Constraint 12, 13, 17
    private static void writeNextToConstraints(FileWriter writer, int typeOneOffset, int typeTwoOffset,
                                               int leftkOffset, int rightkOffset ) throws IOException {
        for (int i = 0 ; i < 5 ; i++) {
            for (int j = 0 ; j < 5; j++) {
                int leftVarTypeOne = typeOneOffset + (i*25) + (j*5) + leftkOffset;
                StringBuilder typeOneImplication = new StringBuilder();
                typeOneImplication.append("-").append(leftVarTypeOne).append(" ");

                int leftVarTypeTwo = typeTwoOffset + (i*25) + (j*5) + rightkOffset;
                StringBuilder typeTwoImplication = new StringBuilder();
                typeTwoImplication.append("-").append(leftVarTypeTwo).append(" ");

                for (int x = 0 ; x < 5 ; x++) {
                    if (x != i) {
                        if (j < 4) {
                            int rightVarTypeOne = typeTwoOffset + (x*25) + ((j+1)*5) + rightkOffset;
                            typeOneImplication.append(rightVarTypeOne).append(" ");

                            int rightVarTypeTwo = typeOneOffset + (x*25) + ((j+1)*5) + leftkOffset;
                            typeTwoImplication.append(rightVarTypeTwo).append(" ");
                        }
                        if (j > 0) {
                            int rightVarTypeOne = typeTwoOffset + (x*25) + ((j-1)*5) + rightkOffset;
                            typeOneImplication.append(rightVarTypeOne).append(" ");

                            int rightVarTypeTwo = typeOneOffset + (x*25) + ((j+1)*5) + leftkOffset;
                            typeTwoImplication.append(rightVarTypeTwo).append(" ");
                        }
                    }
                }
                writer.write(typeOneImplication.append("0\n").toString());
                writer.write(typeTwoImplication.append("0\n").toString());
            }
        }
    }

    private static void writeBidirectionalImplication(FileWriter writer,
                                              int leftIndicator, int rightIndicator) throws IOException{
        String clause = "-" + leftIndicator + " " + rightIndicator + " 0\n";
        writer.write(clause);
        clause = "-" + rightIndicator + " " + leftIndicator + " 0\n";
        writer.write(clause);
    }

}