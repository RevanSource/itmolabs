package itmolabs;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by dart_revan on 04/10/15.
 */
public class FirstLab {
    private final Path filesWithRange;
    private final Path fileWithColors;



    public FirstLab(String pathToFilesWithRange, String pathToFileWithColors) {
        Path filesWithRange = null;
        try {
            filesWithRange = Paths.get(pathToFilesWithRange);
        } catch(RuntimeException re){
            //:TODO add something
        }
        Path fileWithColors = null;
        try {
            fileWithColors = Paths.get(pathToFileWithColors);
        } catch (RuntimeException re) {
            //:TODO add something
        }
        this.filesWithRange = filesWithRange;
        this.fileWithColors = fileWithColors;

    }



    public void printResult(){


    }

    private void loadColors(){

    }

    private void loadRangeList(){

    }

    private static class RangeEntry {
        final String productName;
        final double summaryPrice;
        final int count;

        public RangeEntry(String productName, double summaryPrice, int count) {
            this.productName = productName;
            this.summaryPrice = summaryPrice;
            this.count = count;
        }

        public String getProductName() {
            return productName;
        }

        public double getSummaryPrice() {
            return summaryPrice;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args){
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        new FirstLab(args[0], args[1]).printResult();

    }

}
