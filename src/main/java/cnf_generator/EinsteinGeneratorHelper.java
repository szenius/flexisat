package cnf_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EinsteinGeneratorHelper {

    private static double colourOffset = 0;
    private static double beverageOffset = Math.pow(5,3);
    private static double petOffset = 2 * Math.pow(5,3);
    private static double gCigarOffset = 3 * Math.pow(5,3);

    // Every house can only have one Colour, Beverage, Pet and Cigar.
    public static void writeType1Constraints(FileWriter writer, double numIndicatorVariables) throws IOException {
        for (int i = 0; i < (numIndicatorVariables / 5); i++) {
            // Have to do in blocks of 5 because of cardinality
            for (int j = 0; j < 5; j++) {
                int currentIndicatorVariable = (i * 5 + 1) + j;
                StringBuilder leftImplication = new StringBuilder();
                for (int k = 0; k < 5; k++) {
                    if (k != j) {
                        int otherIndicatorVariable = (i * 5 + 1) + k;
                        String rightImplication = "-" + currentIndicatorVariable + " -" + otherIndicatorVariable + " 0\n";
                        writer.write(rightImplication);
                        leftImplication.append(otherIndicatorVariable).append(" ");
                    }
                }
                leftImplication.append(currentIndicatorVariable).append(" 0\n");
                writer.write(leftImplication.toString());
            }
        }
    }

    // Every Nationality can only be matched to one House Order
    public static void writeType2Constraints(FileWriter writer) throws IOException {
        double[] types = {colourOffset, beverageOffset, petOffset, gCigarOffset};
        for (int type = 0 ; type < types.length ; type++ ) {
            for (int i = 0 ; i < 5; i++) {
                for (int j = 0 ; j < 5 ; j++) {
                    for (int k = 0 ; k < 5; k++) {
                        double leftVar = types[type] + (i*25) + (j*5) + (k+1);
                        StringBuilder leftImplication = new StringBuilder();
                        leftImplication.append("-").append(leftVar).append(" ");

                        StringBuilder rightImplication = new StringBuilder();
                        rightImplication.append(leftVar).append(" ");

                        for (int x = 0 ; x < 5; x++) {
                            // Not conflict with j
                            if (x != j) {
                                double rightVar = types[type] + (i*25) + (x*5) + (k+1);
                                leftImplication.append("-").append(rightVar).append(" ");
                                writer.write(leftImplication.append("0\n").toString());

                                rightImplication.append(rightVar).append(" ");
                            }
                            // Not conflict with 1
                            if (x != i) {
                                double rightVar = types[type] + (x*25) + (j*5) + (k+1);
                                leftImplication.append("-").append(rightVar).append(" ");
                                writer.write(leftImplication.append("0\n").toString());
                            }
                        }
                        writer.write(rightImplication.append("0\n").toString());
                    }
                }
            }
        }
    }

    // The Brit lives in the red house
    public static void writeType3Constraints(FileWriter writer) throws IOException {
        String clause = "1 6 11 16 21 0\n";
        writer.write(clause);
    }

    // The Swede keeps dogs as pets
    public static void writeType4Constraints(FileWriter writer) throws IOException {
        String clause = (petOffset + 26) + " " + (petOffset + 31) + " " +
                (petOffset + 36) + " " + (petOffset + 41) + " " + (petOffset + 46) + " 0\n";
        writer.write(clause);
    }

    // The Dane drinks tea
    public static void writeType5Constraints(FileWriter writer) throws IOException {
        double teaOffset = Math.pow(5, 3);
        String clause = (teaOffset + 51) + " " + (teaOffset + 56) + " " +
                (teaOffset + 61) + " " + (teaOffset + 66) + (teaOffset + 71) + " 0\n";
        writer.write(clause);
    }

    // The Green house is on the left of the White house
    public static void writeType6Constraints(FileWriter writer) throws IOException {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                // Right implication
                int leftIndicatorVar = (j * 5) * (i * 25) + 2;
                int rightIndicatorVar = leftIndicatorVar + 5 + 1;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }
        for (int i = 0; i < 5; i++) {
            int indicatorVariable = (i * 25) + 20 + 2;
            String clause = "-" + indicatorVariable + " 0\n";
            writer.write(clause);
        }
    }

    // The Green house's owner drinks coffee
    public static void writeType7Constraints(FileWriter writer) throws IOException {
        for (int i = 0 ; i < 5 ; i++) {
            for (int j = 0 ; j < 5 ; j++) {
                int leftIndicatorVar = (j*5) + (i*25) + 2;
                double rightIndicatorVar = beverageOffset + (j*5) + (i*25) + 2;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }
    }

    // The person who smokes Pall Mall rears bird
    public static void writeType8Constraints(FileWriter writer) throws IOException {
        for (int i = 0 ; i < 5 ; i++ ) {
            for (int j = 0 ; j < 5; j++) {
                int leftIndicatorVar = (j*5) + (i*25) + 1;
                double rightIndicatorVar = petOffset + (j*5) + (i*25) + 2;
                writeBidirectionalImplication(writer, leftIndicatorVar, rightIndicatorVar);
            }
        }
    }

    // The owner of the yellow house smokes Dunhill
    public static void writeType9Constraints(FileWriter writer) throws IOException {
        for (int i = 0 ; i < 5; i++) {
            for (int j = 0 ; j < 5 ; j++) {
                int leftIndicator = (j*5) + (i*25) + 4;
                double rightIndicator = gCigarOffset + (j*5) + (i*25) + 4;
                writeBidirectionalImplication(writer, leftIndicator, rightIndicator);
            }
        }
    }

    // The man living in the center house drinks milk
    public static void writeType10Constraints(FileWriter writer) throws IOException {
        StringBuilder clause = new StringBuilder();
        for (int i = 0 ; i < 5; i++) {
            double indicatorVariable = beverageOffset + (i * 25) + 15;
            clause.append(indicatorVariable).append(" ");
        }
        writer.write(clause.append("0\n").toString());
    }

    // The Norwegian lives in the first house
    public static void writeType11Constraints(FileWriter writer) throws IOException {
        StringBuilder clause = new StringBuilder();
        int startingOffset = 25 * 3;
        for (int i = 1 ; i < 6 ; i++){
            clause.append((startingOffset + i)).append(" ");
        }
        writer.write(clause.append("0\n").toString());
    }

    // The man who smokes Blends lives next to the one who keeps cats
    public static void writeType12Constraints(FileWriter writer) throws IOException {
        writeNextToConstraints(writer, gCigarOffset, petOffset, 3, 3);
    }

    // The man who keeps the horse lives next to the man who smokes Dunhill
    public static void writeType13Constraints(FileWriter writer) throws IOException {
        writeNextToConstraints(writer, petOffset, gCigarOffset, 4, 2);
    }

    // The owner who smokes Bluemasters drink beer
    public static void writeType14Constraints(FileWriter writer) throws IOException {
        for (int i = 0 ; i < 5 ; i++ ){
            for (int j = 0 ; j < 5 ; j++ ){
                double leftIndicator = gCigarOffset + (i * 25) + (j * 5) + 4;
                double rightIndicator = beverageOffset + (i * 25) + (j * 5) + 3;
                writeBidirectionalImplication(writer, leftIndicator, rightIndicator);
            }
        }
    }

    // The German smokes Prince
    public static void writeType15Constraints(FileWriter writer) throws IOException {
        StringBuilder constraint = new StringBuilder();
        for (int j = 0 ; j < 5 ; j++) {
            double indicator = gCigarOffset + (4*25) + (j*5) + 5;
            constraint.append(indicator).append(" ");
        }
        writer.write(constraint.append("0\n").toString());
    }

    // The Norwegian lives next to the blue house
    public static void writeType16Constraints(FileWriter writer) throws IOException {
        for (int k = 0 ; k < 5 ; k++) {
            for (int j = 0; j < 5; j++) {
                double leftImplicationK = colourOffset + (4*25) + (j*5) + (k+1);
                StringBuilder rightImplicationK = new StringBuilder();

                double leftImplicationI = colourOffset + (k*25) + (j*5) + 5;
                StringBuilder rightImplicationI = new StringBuilder();

                for (int x = 0 ; x < 5 ; x++){
                    if (x != 3) {
                        double rightImplication;
                        if (j != 0) {
                            rightImplication = colourOffset + (x*25) + ((j-1)*5) + 5;
                            rightImplicationK.append(rightImplication).append(" ");
                        }
                        if (j != 4) {
                            rightImplication = colourOffset + (x*25) + ((j+1)*5) + 5;
                            rightImplicationK.append(rightImplication).append(" ");
                        }
                    }
                    if (x != 4) {
                        double rightImplication;
                        if (j != 0) {
                            rightImplication = colourOffset + (3*25) + ((j-1)*5) + (x+1);
                            rightImplicationI.append(rightImplication).append(" ");
                        }
                        if (j != 4) {
                            rightImplication = colourOffset + (3*25) + ((j+1)*5) + (x+1);
                            rightImplicationI.append(rightImplication).append(" ");
                        }
                    }
                }
                writer.write("-" + leftImplicationK + " " + rightImplicationK + "0\n");
                writer.write("-" + leftImplicationI + " " + rightImplicationI + "0\n");
            }
        }
    }


    // The man who smokes Blends has a neighbour who drinks water
    public static void writeType17Constraints(FileWriter writer) throws IOException {
        writeNextToConstraints(writer, gCigarOffset, beverageOffset, 3, 4);
    }


    // Used for Constraint 12, 13, 17
    private static void writeNextToConstraints(FileWriter writer, double type1IVCombiOffset, double type2IVCombiOffset,
                                               int leftkOffset, int rightkOffset ) throws IOException {
        for (int i = 0 ; i < 5 ; i++) {
            // Case j == 1
            double leftIndicatorNO1G = type1IVCombiOffset + (i * 25) + leftkOffset;
            StringBuilder firstNOG1Implication = new StringBuilder();
            firstNOG1Implication.append("-").append(leftIndicatorNO1G).append(" ");
            double leftIndicatorNO1P = type2IVCombiOffset + (i * 25) + rightkOffset;
            StringBuilder firstNOP1Implication = new StringBuilder();
            firstNOP1Implication.append("-").append(leftIndicatorNO1P).append(" ");

            // Case j == 5
            double leftIndicatorNO5G = type1IVCombiOffset + (i * 25) + leftkOffset;
            StringBuilder NO5GImplication = new StringBuilder();
            NO5GImplication.append("-").append(leftIndicatorNO5G).append(" ");
            double leftIndicatorNO5P = type2IVCombiOffset + (i * 25) + rightkOffset;
            StringBuilder NO5PImplication = new StringBuilder();
            NO5PImplication.append("-").append(leftIndicatorNO5P).append(" ");

            for (int k = 0 ; k < 5; k++ ){
                if (k != i) {
                    double rightIndicatorNO1G = type2IVCombiOffset + (k*25) + (2*5) +3;
                    firstNOG1Implication.append(rightIndicatorNO1G).append(" ");

                    double rightIndicatorNO1P = type1IVCombiOffset + (k*25) + (2*5) + 3;
                    firstNOP1Implication.append(rightIndicatorNO1P).append(" ");

                    double rightIndicatorNO5G = type2IVCombiOffset + (k*25) + (2*5) + 3;
                    NO5GImplication.append(rightIndicatorNO5G).append(" ");

                    double rightIndicatorNO5P = type1IVCombiOffset + (k*25) + (2*5) + 3;
                    NO5PImplication.append(rightIndicatorNO5P).append(" ");
                }
            }
            writer.write(firstNOG1Implication.append("0\n").toString());
            writer.write(firstNOP1Implication.append("0\n").toString());
            writer.write(NO5GImplication.append("0\n").toString());
            writer.write(NO5PImplication.append("0\n").toString());

            // Case j - 2:5
            for (int j = 2; j < 5; j++) {
                StringBuilder firstNOG234Implication = new StringBuilder();
                double leftIndicatorNOG3 = type1IVCombiOffset + (i*25) + (j*5) + 3;
                firstNOG234Implication.append("-").append(leftIndicatorNOG3).append(" ");

                StringBuilder secondNOP234Implication = new StringBuilder();
                double leftIndicatorNOP3 = type2IVCombiOffset + (i*25) + (j*5) + 3;
                secondNOP234Implication.append("-").append(leftIndicatorNOP3).append(" ");
                for (int k = 0 ; k < 5; k++) {
                    if (k != i) {
                        double rightIndicatorNOG3Minus1 = type2IVCombiOffset + (k * 25) + ((j-1)*5) + 3;
                        firstNOG234Implication.append(rightIndicatorNOG3Minus1).append(" ");
                        double rightIndicatorNOG3Plus1 = type2IVCombiOffset + (k*25) + ((j+1)*5) + 3;
                        firstNOG234Implication.append(rightIndicatorNOG3Plus1).append(" ");

                        double rightIndicatorNOP3Minus1 = type1IVCombiOffset + (k*25) + ((j-1)*5) + 3;
                        secondNOP234Implication.append(rightIndicatorNOP3Minus1).append(" ");
                        double rightIndicatorNOP3Plus1 = type1IVCombiOffset + (k*25) + ((j+1)*5) + 3;
                        secondNOP234Implication.append(rightIndicatorNOP3Plus1).append(" ");
                    }
                }
                writer.write(firstNOG234Implication.append("0\n").toString());
                writer.write(secondNOP234Implication.append("0\n").toString());
            }
        }


    }

    private static void writeBidirectionalImplication(FileWriter writer,
                                              double leftIndicator, double rightIndicator) throws IOException{
        String clause = "-" + leftIndicator + " " + rightIndicator + " 0\n";
        writer.write(clause);
        clause = "-" + rightIndicator + " " + leftIndicator + " 0\n";
        writer.write(clause);
    }

}