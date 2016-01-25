package itmolabs.exam;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;

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
        private FilesService filesService = FilesService.getInstance();
        private HistoryService historyService = HistoryService.getInstance();

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
                File file = new File(".");
                Path path = file.toPath();
                while((request = readInput()) != null) {
                    LOGGER.info("receive: " + request);
                    String response = null;
                    //TODO make it easy
                    String[] params = request.split(" ");
                    if (params.length < 2) {
                        response = "Incorrect command, please try again";
                    } else {
                        String command = params[1];
                        String name = params[0];
                        String arg = params.length > 2
                                ? request.substring((name + " " + command + " ").length(), request.length())
                                : request.substring((name + " " + command).length(), request.length());
                        if (command.startsWith("ls")) {
                            response = filesService.ls(path).stream().reduce((r,s) -> r = r + " " + s)
                                    .orElse("No such file or directory");
                            historyService.history(name,"ls",arg,response);
                        } else if (command.startsWith("mv")) {
                            Path target = null;
                            Path source = null;
//                            if (!arg.startsWith("log.txt")) {
                                try {
                                    String[] param = arg.split(" ");
                                    source = filesService.cd(path, param[0]);
                                    target = Paths.get(arg);

                                } catch (Exception e) {
                                    LOGGER.error(e);
//                                }
                            }

                            response = filesService.mv(source, target) ? "successfully moved " : "failed to move ";
                            historyService.history(name,"mv",arg,response);
                        } else if (command.startsWith("cd")) {
                            Path result = filesService.cd(path, arg);
                            if (result == null) {
                                response = "failed to cd to " + arg;
                            } else {
                                response = "success";
                                path = result;
                            }
                            historyService.history(name,"cd", arg, response);
                        } else if (command.startsWith("stat")) {
                            try {
                                if (params[2].startsWith("get")) {
                                    HistoryService.DateSearchOption option = HistoryService.DateSearchOption.valueOf(params[3]);
                                    if (option == HistoryService.DateSearchOption.anytime)
                                        response = historyService.searchHistory(option, null, params[4]);
                                    else
                                        response = historyService.searchHistory(option, LocalDateTime.parse(params[4]), params[5]);
                                } else if (params[2].startsWith("union")){
                                    //returns just stream of HistoryUnits without filters
                                    Set<HistoryService.HistoryUnit> units = historyService.getSet();
                                    for (int i = 3; i < params.length; i++){
                                        if (params[i].startsWith("get")){
                                            HistoryService.DateSearchOption option =
                                                    HistoryService.DateSearchOption.valueOf(params[i + 1]);

                                            if (option == HistoryService.DateSearchOption.anytime)
                                                units = historyService.filter(units, option, null, params[i + 2]);
                                            else
                                                historyService
                                                        .filter(units, option, LocalDateTime.parse(params[i + 2]), params[i + 3]);
                                        }
                                    }
                                    String result = historyService.reduceToStringAndOrderByTime(units);
                                    if (result != null) {
                                        response = result;
                                    }
                                }
                            } catch (Exception e){
                                LOGGER.error(e);
                            }
                            response = response == null ? "Failed to perform this. Incorrect request." : response;
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
