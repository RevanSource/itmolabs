package itmolabs.exam;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class HistoryService {
    private static String HISTORY_PATH= "log.txt";
    private static HistoryService ourInstance = new HistoryService();

    public static HistoryService getInstance() {
        return ourInstance;
    }
    private Logger LOGGER = new Logger();
    private List<HistoryUnit> historyUnits;
    private PrintWriter printWriter;
    private Lock lock = new ReentrantLock();
    private HistoryService() {
        init();
    }

    private void init(){
        final Path path = Paths.get(HISTORY_PATH);
        final File file = path.toFile();
        try {
            //will create new file only if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        historyUnits = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            LOGGER.info("Path to history file: " + file.getAbsolutePath());
            while (br.ready()){
                String line = br.readLine().trim();
                HistoryUnit unit = parse(line);
                boolean isAdded = unit != null && historyUnits.add(unit);
                LOGGER.info(line + (isAdded ? " has been added" : " failed to add"));
            }
            LOGGER.info("The History has been initialized, total count of HistoryUnits is " + historyUnits.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            printWriter =  new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void history(String userName, String operationName, String operationArg, String result){
        HistoryUnit unit = new HistoryUnit(userName, LocalDateTime.now(), operationName, operationArg, result);
        lock.lock();
        historyUnits.add(unit);
        printWriter.println(unit.toString());
        lock.unlock();
    }

    public String searchHistory(DateSearchOption option, LocalDateTime dateTime, String command){
        final String emptyMessage = "Nothing has been found";
        switch (option) {
            case anytime:
                return historyUnits.stream()
                        .filter(u -> u.operationName.equals(command))
                        .map(HistoryUnit::toString)
                        .reduce((r,s) -> r = r + "\n" + s).orElse(emptyMessage);
            case before:
                return historyUnits.stream()
                        .filter(u -> u.operationName.equals(command))
                        .filter(u -> u.dateTime.isBefore(dateTime))
                        .map(HistoryUnit::toString)
                        .reduce((r,s) -> r = r + "\n" + s).orElse(emptyMessage);
            case after:
                return historyUnits.stream()
                        .filter(u -> u.operationName.equals(command))
                        .filter(u -> u.dateTime.isAfter(dateTime))
                        .map(HistoryUnit::toString)
                        .reduce((r,s) -> r = r + "\n" + s).orElse(emptyMessage);
            default:
                return emptyMessage;
        }
    }

    public Set<HistoryUnit> getSet() {
        return historyUnits.stream().collect(Collectors.toSet());
    }

    public Set<HistoryUnit> filter(Set<HistoryUnit> set, DateSearchOption option, LocalDateTime dateTime, String command){
        switch (option) {
            case anytime:
                return set.stream()
                        .filter(u -> u.operationName.equals(command))
                        .collect(Collectors.toSet());
            case before:
                return set.stream()
                        .filter(u -> u.operationName.equals(command))
                        .filter(u -> u.dateTime.isBefore(dateTime))
                        .collect(Collectors.toSet());
            case after:
                return set.stream()
                        .filter(u -> u.operationName.equals(command))
                        .filter(u -> u.dateTime.isAfter(dateTime))
                        .collect(Collectors.toSet());
            default:
                return set.stream().collect(Collectors.toSet());
        }
    }

    public String reduceToStringAndOrderByTime(Set<HistoryUnit> units) {
        final String emptyMessage = "Nothing has been found";
        return units.stream()
                .sorted((p1, p2) -> p1.dateTime.compareTo(p2.dateTime))
                .map(HistoryUnit::toString)
                .reduce((r, s) -> r = r + "\n" + s).orElse(emptyMessage);
    }

    public enum DateSearchOption{
        before,
        after,
        anytime;
    }

    public static class HistoryUnit {
        private final static int PARAMS_NUMBER = 5;
        private final String userName;
        private final LocalDateTime dateTime;
        private final String operationName;
        private final String operationArg;
        private final String result;

        @Override
        public String toString(){ return userName + ";"
                + dateTime + ";"
                + operationName + ";"
                + (operationArg.length() > 20 ? operationArg.substring(0, 20) : operationArg) +";"
                + (result.length() > 20 ? result.substring(0, 20) : result) +";";
        }

        public HistoryUnit(String userName, LocalDateTime dateTime, String operationName, String operationArg, String result) {
            this.userName = userName;
            this.dateTime = dateTime;
            this.operationName = operationName;
            this.operationArg = operationArg;
            this.result = result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HistoryUnit unit = (HistoryUnit) o;

            if (userName != null ? !userName.equals(unit.userName) : unit.userName != null) return false;
            if (dateTime != null ? !dateTime.equals(unit.dateTime) : unit.dateTime != null) return false;
            if (operationName != null ? !operationName.equals(unit.operationName) : unit.operationName != null)
                return false;
            if (operationArg != null ? !operationArg.equals(unit.operationArg) : unit.operationArg != null)
                return false;
            return !(result != null ? !result.equals(unit.result) : unit.result != null);

        }

        @Override
        public int hashCode() {
            int result1 = userName != null ? userName.hashCode() : 0;
            result1 = 31 * result1 + (dateTime != null ? dateTime.hashCode() : 0);
            result1 = 31 * result1 + (operationName != null ? operationName.hashCode() : 0);
            result1 = 31 * result1 + (operationArg != null ? operationArg.hashCode() : 0);
            result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
            return result1;
        }
    }

    public HistoryUnit parse(String line){

        HistoryUnit unit = null;
        try {
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(";");
                if (parts.length == HistoryUnit.PARAMS_NUMBER) {
                    String username = parts[0];
                    LocalDateTime dateTime = LocalDateTime.parse(parts[1]);
                    final String operationName = parts[2];
                    final String operationArg = parts[3];
                    final String result = parts[4];
                    unit = new HistoryUnit(username, dateTime, operationName, operationArg, result);
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }
        return unit;
    }
}
