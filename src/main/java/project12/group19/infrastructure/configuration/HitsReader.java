package project12.group19.infrastructure.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HitsReader {

    public static void main(String[] args) throws IOException {
        HitsReader test = new HitsReader();
        test.read("C:\\Users\\Alvaro\\Documents\\GitHub\\Project-1.2\\input-example-for-hits.txt");
    }

    public double[][] read(String path) throws IOException {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int index = 0;
        int xIndex = 0;
        int lines = 0;
        String currentNumber = "";

        while ((st = br.readLine()) != null) {
            lines++;
        }
        double[][] hits = new double[lines][2]; // storing the hits in a 2d array (x & y positions)

        BufferedReader fr = new BufferedReader(new FileReader(file));

        while ((st = fr.readLine()) != null) {
            currentNumber = "";
            index = 0;
            while (st.charAt(index) != '=') {
                index++;
            }
            if (st.charAt(index) == '=') {
                index += 2;
                while (st.charAt(index) != ',') {
                    currentNumber += st.charAt(index);
                    index++;
                }
                hits[xIndex][0] = Double.parseDouble(currentNumber);
                currentNumber = "";
            }

            while (st.charAt(index) != '=') {
                index++;
            }
            if (st.charAt(index) == '=') {
                index += 2;
                while (index != st.length()) {
                    currentNumber += st.charAt(index);
                    index++;
                }
                hits[xIndex][1] = Double.parseDouble(currentNumber);
                xIndex += 1;
                currentNumber = "";
            }

        }

        // System.out.println(Arrays.deepToString(hits));

        return hits;
    }

}
