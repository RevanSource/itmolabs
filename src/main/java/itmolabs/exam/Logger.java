package itmolabs.exam;

import java.time.LocalDateTime;

public class Logger{
    public Logger() {
    }

    public void info(String message) {
        System.out.println(LocalDateTime.now() + " " + message);
    }

    public void error(String message, Exception e) {
        System.err.println(LocalDateTime.now() + " " + message + e);
    }

    public void error(Exception e) {
        System.err.println(LocalDateTime.now() + " " + e);
    }
    public void error(String message) {
        System.err.println(LocalDateTime.now() + " " + message);
    }
}
