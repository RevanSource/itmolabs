package itmolabs.firstlab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by dart_revan on 04/10/15.
 */
public class RangeCalculator {
    private final Path filesWithRange;
    private final Path fileWithColors;

    Map<String, Entry> ranges = new TreeMap<>();
    Map<String, Entry> colors = new TreeMap<>();

    public RangeCalculator(String pathToFilesWithRange, String pathToFileWithColors) {
        Path filesWithRange = null;
        try {
            filesWithRange = Paths.get(pathToFilesWithRange);
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        Path fileWithColors = null;
        try {
            fileWithColors = Paths.get(pathToFileWithColors);
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        this.filesWithRange = filesWithRange;
        this.fileWithColors = fileWithColors;

    }

    private void readRanges(){
        try {
            Files.walk(filesWithRange)
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().endsWith(".txt"))
                    .forEach(filePath -> {
                        System.out.println(filePath);
                        readFromFileToMap(filePath, ranges, RangeEntry.class);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readColors(){
        readFromFileToMap(fileWithColors,colors, ColorEntry.class);
    }


    private void readFromFileToMap(Path file, Map<String, Entry> map, Class toCast) {
        System.out.println(file);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file, Charset.defaultCharset());
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            if (toCast == ColorEntry.class){
                colors.put("",new ColorEntry("",0));
            }
        }
        if (lines != null) {
            readFromLines(map, lines.toArray(new String[lines.size()]), toCast);
        }

    }

    public void readFromLines(Map<String, Entry> entries, String[] lines,Class toCast){
        Set<String> usedNames = new HashSet<>();
        for (String line : lines) {
            Entry entry = readEntry(line, toCast);//get entry from

            if (entry == null) continue;
            if (!usedNames.contains(entry.getName())) {
                Entry oldEntry = entries.get(entry.getName());
                if (oldEntry != null) {
                    entry = entry.merge(oldEntry);
                }
                entries.put(entry.getName(), entry);
                usedNames.add(entry.getName());
            }
        }

    }

    private Entry readEntry(String line, Class toCast){
        Entry entry = null;
        try {
            String[] parts = line.split(" ");
            if (parts.length != 2 || !parts[1].matches("\\d+")) return null;
            String name = parts[0];
            int price = Integer.valueOf(parts[1]);

            entry = new Entry(name, price);
            if (toCast == ColorEntry.class) {
                entry = new ColorEntry(entry);
            }
            if (toCast == RangeEntry.class) {
                entry = new RangeEntry(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entry;
    }

    public void printResult(){
        readRanges();
        readColors();
        try {
            PrintStream ps = new PrintStream("result.txt");
            System.setOut(ps);
            if (ranges.isEmpty()) {
                System.out.println("Directory with ranges is empty or files have bad format");
            }

            ranges.values().forEach(
                    range -> colors.values().forEach(
                            color -> System.out.println(range.merge(color).toString())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        new RangeCalculator(args[0], args[1]).printResult();

    }

}
