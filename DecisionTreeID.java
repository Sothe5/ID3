import java.util.List;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.io.*;

class Test {
    String[] features;
}

public class DecisionTreeID {

    private static HashMap<String, String[]> nodes = new HashMap<>();
    private static HashMap<String, String[]> node_values = new HashMap<>();
    private static ArrayList<String> node_names = new ArrayList<>();

    public static void main(String[] args) {
        learnDT("small-train.csv");
    }

    public static void learnDT(String ficheroCSV) {
        List<String[]> data = readCSV(ficheroCSV);
        String[] features = data.get(0);
        data.remove(0);
        recursion(data, features);
            
        // Dema dice que hemos acabado
    }

    private static void recursion(List<String[]> data, String[] features) {
        int targetIndex = 0;
        int bestFeatureIndex = -1;
        double bestEntropy = Double.MAX_VALUE;
        for (int feature = 1; feature < features.length; feature++) {
            double entropy = calculateEntropyOfNode(data, feature, targetIndex);
            if (entropy < bestEntropy) {
                bestFeatureIndex = feature;
                bestEntropy = entropy;
            }
            
            System.out.print("Entropy of " + features[feature] + ":");
            System.out.println(entropy);
        }
        if (bestFeatureIndex == -1) {
            return;
        }
        
        System.out.println("Best entropy is: " + features[bestFeatureIndex]);
        

        AbstractMap<String, List<String[]>> rowsOfEachBranch = joinRowsBySameValue(data, bestFeatureIndex);
        for (String possibleValue : rowsOfEachBranch.keySet()) {
            if (hasToExpand(rowsOfEachBranch.get(possibleValue), targetIndex)) {
                System.out.println("Has to expand " + possibleValue);
                List<String[]> tableForThisBranch = GetNewTable(data, bestFeatureIndex, possibleValue);
                features = removeElementAt(bestFeatureIndex, features);
                recursion(tableForThisBranch, features);
            } else {
                System.out.println("If value is " + possibleValue + " then the passenger is " + isLeafPositive(rowsOfEachBranch.get(possibleValue), targetIndex));
            }
        }
        System.out.println("Node closed");
        // Dema dice que hemos acabado
    }

    private static boolean hasToExpand(List<String[]> branchRows, int targetIndex) {
        return calculateEntropyOfBranch(branchRows, targetIndex) != 0;
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

    private static AbstractMap<String, List<String[]>> joinRowsBySameValue(List<String[]> data, int featureIndex) {
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

    private static boolean isLeafPositive(List<String[]> rows, int targetIndex) {
        int numberOfPositives = 0;
        int numberOfNegatives = 0;
        for (String[] row : rows) {
            if (row[targetIndex].equals("1")) {
                numberOfPositives++;
            } else {
                numberOfNegatives++;
            }
        }
        if (numberOfPositives > 0 && numberOfNegatives > 0) {
            throw new RuntimeException("uwu");
        }
        return numberOfPositives > 0;
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

    public Object prediction(String[] registroCSV) {

        return null;
    }

    public static void drawDecisionTree() {
        String[] root = { node_names.get(0) };
        drawTreeAux(root, 0, null);
    }

    private static void drawTreeAux(String[] nodes_aux, int level, String[] values) {
        int idx = 0;
        for (String node : nodes_aux) {
            for (int i = 0; i < level; i++) {
                System.out.print('\t');
            }
            if (values != null) {
                System.out.print(values[idx] + ":");
            }
            System.out.println(node);
            String[] children = nodes.get(node);
            if (children != null) {
                drawTreeAux(children, level + 1, node_values.get(node));
            }
            idx++;
        }
    }

    private static List<String[]> RemoveLine(List<String[]> data, int node_idx, String value_node) {
        List<String[]> new_list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i)[node_idx].equals(value_node)) {
                new_list.add(data.get(i));
            }
        }
        return new_list;
    }

    private static List<String[]> RemoveColumn(List<String[]> data, int node_idx) {
        List<String[]> new_list = new ArrayList<>();
        
        for (String[] line: data) {
            line = removeElementAt(node_idx, line);
            new_list.add(line);
        }
        return new_list;
    }

    private static String[] removeElementAt(int index, String[] array) {
        List<String> values = new ArrayList<>();
        for(int i = 0; i < array.length; i++)  {
            if (i != index) {
                values.add(array[i]);
            }
        }
        String[] ans = new String[values.size()];
        for(int i = 0; i < values.size(); i++) {
            ans[i] = values.get(i);
        }
        return ans;
    }

    private static List<String[]> GetNewTable(List<String[]> data, int node_idx, String value_node) {
        List<String[]> new_list = RemoveLine(data, node_idx, value_node);
        return RemoveColumn(new_list, node_idx);
    }
}
