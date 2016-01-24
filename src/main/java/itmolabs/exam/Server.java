package itmolabs.exam;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger LOGGER = new Logger();

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8080);
            LOGGER.info("Server started. Port: " + ss.toString());
            while (true) {
                Socket s = ss.accept();
                LOGGER.info("Client accepted");
                new Thread(new SocketProcessor(s)).start();
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private static class SocketProcessor implements Runnable {
        private CalculationService calculationService = CalculationService.getInstance();

        private Socket s;
        private DataInputStream is;
        private DataOutputStream os;

        private SocketProcessor(Socket s) throws IOException {
            this.s = s;
            this.is = new DataInputStream(s.getInputStream());
            this.os = new DataOutputStream(s.getOutputStream());
        }

        public void run() {
            try {
                String request = null;
                while((request = readInput()) != null) {
                    LOGGER.info("receive: " + request);
                    String response = null;
                    //TODO make it easy
                    String[] params = request.split(" ");
                    if (params.length < 3) {
                        response = "Incorrect command, please try again";
                    } else {
                        String command = params[1];
                        String name = params[0];
                        String arg = request.substring((name + " " + command + " ").length(), request.length());
                        if (command.startsWith("add")) {
                            response = calculationService.addWord(arg, name);
                        } else if (command.startsWith("check")) {
                            response = calculationService.check(arg, name);
                        } else if (command.startsWith("style")) {
                            response = calculationService.style(arg, name);
                        } else {
                            response = "Incorrect command, please try again";
                        }
                    }
                    LOGGER.info(response);
                    writeResponse(response);
                }
                LOGGER.info("Client processing finished");
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }

        private void writeResponse(String s) throws IOException {
            os.writeUTF(s);
            os.flush();
        }

        private String readInput() {
            try {
                return is.readUTF();
            } catch (IOException e) {
                LOGGER.info("InputStream was closed ");
                return null;
            }
        }
    }
}
