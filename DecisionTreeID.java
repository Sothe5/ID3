import java.util.List;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.io.*;

class Test {
    String[] features;
}

public class DecisionTreeID {

    public static void main(String[] args) {
        learnDT("small-train.csv");
    }

    public static void learnDT(String ficheroCSV) {
        List<String[]> data = readCSV(ficheroCSV);
        int targetIndex = 0;
        String[] features = data.get(0);
        data.remove(0);
        String bestFeature = features[1];
        double bestEntropy = Double.MAX_VALUE;
        for (int feature = 1; feature < features.length; feature++) {
            double entropy = calculateEntropyOfNode(data, feature, targetIndex);
            if (entropy < bestEntropy) {
                bestFeature = features[feature];
                bestEntropy = entropy;
            }
            System.out.print("Entropy of " + features[feature] + ":");
            System.out.println(entropy);
        }
        System.out.println("Best entropy is: " + bestFeature);
    }

    private static double calculateEntropyOfNode(List<String[]> data, int featureIndex, int targetIndex) {
        AbstractMap<String, List<String[]>> rowsJointBySameValue = joinRowsBySameValue(data, featureIndex);

        double accumulation = 0;
        for (String possibleValue : rowsJointBySameValue.keySet()) {
            List<String[]> rowsOfPossibleValue = rowsJointBySameValue.get(possibleValue);
            accumulation += (rowsOfPossibleValue.size() / (0.0f + data.size()))
                    * calculateEntropyOfBranch(rowsOfPossibleValue, targetIndex);
        }

        return accumulation;
    }

    private static AbstractMap<String, List<String[]>> joinRowsBySameValue(List<String[]> data, int featureIndex ) {
        AbstractMap<String, List<String[]>> rowsOfValue = new HashMap<>();
        for (String[] row : data) {
            List<String[]> rowsOfValueX = rowsOfValue.get(row[featureIndex]);
            if (rowsOfValueX == null) {
                rowsOfValueX = new ArrayList<>();
                rowsOfValue.put(row[featureIndex], rowsOfValueX);
            }
            rowsOfValueX.add(row);
        }
        return rowsOfValue;
    }

    private static double calculateEntropyOfBranch(List<String[]> rows, int targetIndex) {
        int numberOfPositives = 0;
        int numberOfNegatives = 0;
        for (String[] row : rows) {
            if (row[targetIndex].equals("1")) {
                numberOfPositives++;
            } else {
                numberOfNegatives++;
            }
        }
        int numberOfRows = rows.size();
        double entropy = -(numberOfPositives / (0.0 + numberOfRows)) * log(numberOfPositives / (0.0 + numberOfRows), 2)
                - (numberOfNegatives / (0.0 + numberOfRows)) * log(numberOfNegatives / (0.0 + numberOfRows), 2);
        return entropy;
    }

    private static double log(double x, int base) {
        if (x == 0) {
            return 0;
        }
        return Math.log(x) / Math.log(2);
    }

    private static List<String[]> readCSV(String ficheroCSV) {
        String line = "";
        String csvSplitBy = ",";
        List<String[]> elements = new ArrayList<>();
        String[] element;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheroCSV))) {
            while ((line = br.readLine()) != null) {
                element = line.split(csvSplitBy);
                elements.add(element);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return elements;
    }

    private static int getNode(double[] entropies) {
        double min_entropy = entropies[0];
        int min_entropy_idx = 0;
        for (int i = 1; i < entropies.length; i++) {
            if (entropies[i] < min_entropy) {
                min_entropy = entropies[i];
                min_entropy_idx = i;
            }
        }
        return min_entropy_idx;
    }

    public static void drawDecisionTree() {

    }

    public Object prediction(String[] registroCSV) {

        return null;
    }
}

/*
 * private static ArrayList<String[]> RemoveLine(ArrayList<String[]> data,int
 * node_idx, String value_node) { ArrayList<String[]> new_list = new
 * ArrayList<>(); for(int i = 0;i<data.size(); i++) { if(data.get(i)[node_idx]
 * == value_node) { new_list.add(data.get(i)); } } return new_list; }
 * 
 * private static ArrayList<String[]> RemoveColumn(ArrayList<String[]> data,int
 * node_idx){ ArrayList<String[]> new_list = new ArrayList<>(); for(int i =
 * 0;i<data.size(); i++) { for(int j = 0; j<data.get(i).length; i++) {
 * if(node_idx != j) { new_list.add(data.get(i)); } } } return new_list; }
 * 
 * private static ArrayList<String[]> GetNewTable(ArrayList<String[]> data,int
 * node_idx, String value_node){ ArrayList<String[]> new_list = RemoveLine(data,
 * node_idx, value_node); return RemoveColumn(new_list, node_idx); }
 */
