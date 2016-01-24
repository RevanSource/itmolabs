package itmolabs.exam;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CalculationService {
    private static final String DICTIONARY_PATH = "dictionary.txt";
    private static final Logger LOGGER = new Logger();
    private static final int SENTENCE_LEN = 10;
    private static final int PARAGRAPH_LEN = 35;

    private static CalculationService ourInstance = new CalculationService();

    private Set<String> dictionary = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private PrintWriter dictionaryWriter;

    private CalculationService() {
        init();
    }

    public static CalculationService getInstance() {
        return ourInstance;
    }

    private void init() {
        final Path path = Paths.get(DICTIONARY_PATH);
        final File file = path.toFile();
        try {
            //will create new file only if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            LOGGER.info("Path to dictionary file: " + file.getAbsolutePath());
            while (br.ready()){
                String word = br.readLine().trim();
                boolean isAdded = dictionary.add(word);
                LOGGER.info(word + (isAdded ? " has been added" : " is already exists"));
            }
            LOGGER.info("The dictionary has been initialized, total count of words is " + dictionary.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dictionaryWriter =  new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method is ThreadSafe
    public String addWord(String word, String user) {
        boolean result = dictionary.add(word);
        String logInfo = result ? "User " + user + " has successfully added the word \"" + word + "\""
                : "User " + user + " try to add the word \"" + word + "\", but it's already exists";
        if (result) {
            //synchronized println
            dictionaryWriter.println(word);
        }
        return logInfo;
    }

    public String check(String text, String user) {
        String result = text;
        for (String word : dictionary) {
            result = result.replace(word, "");
        }
        //TODO is it correct?
        result = formatText(result);
        String logInfo = "User " + user + " has successfully transform the text \"" + text + "\" to \"" + result + "\"";
        return result;
    }

    public String style(String text, String user) {
        String result = formatText(text);
        String logInfo = "User " + user + " has successfully transform the text \"" + text + "\" to \"" + result + "\"";
        return result;
    }

    public List<String> statGet(String user, Integer numberOfCommands, String command){
        return null;
    }

    private String formatText(String text) {
        StringBuilder result = new StringBuilder();
        String[] paragraphs = text.split("\n");
        for (String p : paragraphs) {
            String[] sentences = p.split("\\.|\\!|\\?");
            char splitArgs[] = new char[sentences.length];
            int counter = 0;
            for (char c : p.toCharArray()) {
                if (c == '.' || c == '?' || c == '!') {
                    splitArgs[counter++] = c;
                }
            }

            final int maxParagraphLen = result.length() + PARAGRAPH_LEN;

            for (int i = 0; i < sentences.length; i++) {
                String s = sentences[i];
                s = s.length() >= SENTENCE_LEN ? s.substring(0, SENTENCE_LEN) + "..." : s + splitArgs[i];
                //check reaching the max paragraph len and break iterations if max len has been reached
                if (result.length() + s.length() > maxParagraphLen) {
                    result.append(s.substring(0, result.length() + s.length() - maxParagraphLen)).append("...");
                    break;
                }
                result.append(s);
            }
            result.append("\n");
        }
        return result.toString();    }
}
