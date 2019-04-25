package cnf_generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EinsteinGeneratorHelper {

    private static int colourOffset = 0;
    private static int beverageOffset = (int)Math.pow(5,3);
    private static int petOffset = 2 * (int)Math.pow(5,3);
    private static int gCigarOffset = 3 * (int)Math.pow(5,3);

    private static String DEBUG = "==========================DEBUG===================== ";

    Map<String, Integer> variables;
    int varId = 1;

    // The CNFs are simply trying to order the values inside the solution...
    // Each CNF literal is a box entry in the 3D space.
    public void createUniqueConstraints(FileWriter writer) throws IOException {
        this.variables = new HashMap<>();
        String[] nationality = {"Brit", "Swede", "Dane", "Norwegian", "German"};
        String[] colour = {"Red", "Green", "White", "Yellow", "Blue"};
        String[] pet = {"Dog", "Bird", "Cat", "Horse", "Fish"};
        String[] smoke = {"Pall Mall", "Dunhill", "Blends", "Bluemasters", "Prince"};
        String[] beverage = {"Tea", "Coffee", "Beer", "Water", "Milk"};

        mapVariablesToId(writer, nationality);
        mapVariablesToId(writer, colour);
        mapVariablesToId(writer, pet);
        mapVariablesToId(writer, smoke);
        mapVariablesToId(writer, beverage);

        // Rule 1: Brit lives in Red House
        //writer.write("Rule 1 \n");
        addEquals(writer, "Brit", "Red");
        // Rule 2: Swede keeps dogs
        //writer.write("Rule 2 \n");
        addEquals(writer, "Swede", "Dog");
        // Rule 3: Dane drinks tea
        //writer.write("Rule 3 \n");
        addEquals(writer, "Dane", "Tea");
        // Rule 4: Green house on left of white house
        //writer.write("Rule 4 \n");
        addLeft(writer, "Green", "White");
        // Rule 5: Green house owner drinks Coffee
        //writer.write("Rule 5 \n");
        addEquals(writer, "Green", "Coffee");
        // Rule 6: The person who smokes Pall Mall rears bird.
        //writer.write("Rule 6 \n");
        addEquals(writer, "Pall Mall", "Bird");
        // Rule 7: Owner of yellow house smokes Dunhill.
        //writer.write("Rule 7 \n");
        addEquals(writer, "Yellow", "Dunhill");
        // Rule 8: Man living in centre drinks milk. **
        //writer.write("Rule 8 \n");
        addTruePosition(writer, "Milk", 3);
        // Rule 9: Norwegian lives in first house
        //writer.write("Rule 9 \n");
        addTruePosition(writer, "Norwegian", 1);
        // Rule 10: Man who smokes Blends live next to the one who keeps cats
        //writer.write("Rule 10 \n");
        addBesideOneAnother(writer, "Blends", "Cat");
        // Rule 11: Man who keeps Horse live next to man who smokes Dunhill
        //writer.write("Rule 11 \n");
        addBesideOneAnother(writer, "Horse", "Dunhill");
        // Rule 12: Man who smokes Bluemasters drink beer.
        //writer.write("Rule 12 \n");
        addEquals(writer, "Bluemasters", "Beer");
        // Rule 13: The German smokes Prince.
        //writer.write("Rule 13 \n");
        addEquals(writer, "German", "Prince");
        // Rule 14: The Norwegian lives next to the blue house.
        //writer.write("Rule 14 \n");
        addBesideOneAnother(writer,"Norwegian", "Blue");
        // Rule 15: Man who smokes Blends has a neighbour who drinks water.
        //writer.write("Rule 15 \n");
        addBesideOneAnother(writer, "Blends", "Water");
        // Rule 16: Someone must own the fish
        //writer.write("Rule 16 \n");
        int fishId = this.variables.get("Fish");
        StringBuilder clause = new StringBuilder();
        for (int i = 0 ; i < 5; i++) {
            int id = fishId + i;
            clause.append(id).append(" ");
        }
        writer.write(clause.append("0\n").toString());

    }

    private void addBesideOneAnother(FileWriter writer, String firstObj, String secondObj) throws IOException{
        // First object can be on the left or the right of the other object
        int firstObjId = this.variables.get(firstObj);
        int secondObjId = this.variables.get(secondObj);
        // [firstObj <> secondObj] AND[secondObj <> firstObj] AND ... (continue for rest of positions)
        for (int i = 1 ; i < 4; i++) {
            int idFirst = firstObjId + i;
            writer.write("-" + idFirst + " " + (secondObjId + i - 1) + " " + (secondObjId + i + 1) + " 0\n");
            //writer.write("-" + (secondObjId + i - 1) + " " + idFirst + " 0\n");
            //writer.write("-" + (secondObjId + i + 1) + " " + idFirst + " 0\n");
        }

        // i == 0
        writer.write("-" + firstObjId + " " + (secondObjId + 1) + " 0\n");
        writer.write("-" + (secondObjId + 1) + " " + firstObjId + " 0\n");
        //i == 4
        writer.write("-" + (firstObjId+4) + " " + (secondObjId + 3) + " 0\n");
        writer.write("-" + (secondObjId + 3) + " " + (firstObjId+4) + " 0\n");

    }

    private void addLeft(FileWriter writer, String leftObj, String rightObj) throws IOException{
        int leftVarId = this.variables.get(leftObj);
        int rightVarId = this.variables.get(rightObj);
        // EG: 1 <-> 2, 2<->3, 3
        for (int leftIndex = 0 ; leftIndex < 4; leftIndex++) {
            // Right implication
            String clause = "-" + (leftVarId + leftIndex) + " " + (rightVarId+leftIndex+1) + " 0\n";
            writer.write(clause);
            // Left implication
            clause = "-" + (rightVarId+leftIndex+1) + " " + (leftVarId+leftIndex) + " 0\n";
            writer.write(clause);
        }
        // Right object cannot be at position 0.
        writer.write("-" + rightVarId + " 0\n");
        // Left object cannot be at position 5.
        writer.write("-" + (leftVarId+5) + " 0\n");
    }


    private void addEquals(FileWriter writer, String stringOne, String stringTwo) throws IOException{
        // Map all 5 variables of the two ids. Means if 1 is true the other has to be true.
        // Ix <-> Iy
        for (int i = 0 ; i < 5; i++) {
            int firstId = this.variables.get(stringOne) + i;
            int secondId = this.variables.get(stringTwo) + i;
            String clauseOne = "-" + firstId + " " + secondId + " 0\n";
            String clauseTwo = "-" + secondId + " " + firstId + " 0\n";
            writer.write(clauseOne);
            writer.write(clauseTwo);
        }
    }


    // Since we already know the position of this object, we can make it a clause by itself and negate
    // every position that is not pos.
    private void addTruePosition(FileWriter writer, String object, int pos) throws IOException{
        int varId = this.variables.get(object);
        for (int i = 0 ; i < 5 ; i++) {
            if ((pos-1) != i) {
                String clause = "-" + (varId+i);
                writer.write(clause + " 0\n");
            } else {
                String clause = Integer.toString(varId+i);
                writer.write(clause + " 0\n");
            }
        }
    }


    private void mapVariablesToId(FileWriter writer, String[] variablesToMap) throws IOException{
        // The 5 ids must be unique.
        for (String variable : variablesToMap) {
            this.variables.put(variable, varId);
            // Create 5 ids for each variable where each id represents a position.
            createOnlyOneClauses(writer, this.varId);
            this.varId += 5;
        }

    }

    // Create constraints and to make sure only 1 position exists.
    private void createOnlyOneClauses(FileWriter writer, int startingId) throws IOException{
        // 1<>-2-3-4-5 == (-1 -2) (-1 -3) (-1 -4) (-1 -5) (1 2 3 4 5)

        // Right implication
        for (int i = 0 ; i < 4; i++){
            for (int j = i+1; j < 5; j++) {
                String clause = "-" + (startingId+i) + " -" + (j+startingId) + " 0\n";
                writer.write(clause);
            }
        }
        // Left implication
        String clause = "";
        for (int i = 0 ; i < 5; i++) {
            clause += (startingId + i) + " ";
        }
        writer.write(clause + "0\n");
    }
}