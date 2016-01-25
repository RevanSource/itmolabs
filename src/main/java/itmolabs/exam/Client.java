package itmolabs.exam;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Logger LOGGER = new Logger();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = args.length > 0 && args[0] != null ? args[0] : "Anonymous";
        LOGGER.info("Hello, " + name);
        Socket socket = null;
        try {
            socket = getSocket();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        while (sc.hasNextLine()) {
            String command = sc.nextLine();
            LOGGER.info("send command: " + command);
            String response = null;
            if (command.equals("ls") || command.startsWith("mv ") || command.startsWith("cd ") || command.startsWith("stat ")) {
                response = sendRequest(socket, name + " " + command);
            } else {
                LOGGER.info("Incorrect command, please try again");
            }
            LOGGER.info("response: " + response);
        }
    }

    private static Socket getSocket() throws IOException {
        int port = 8080;
        String address = "127.0.0.1";
        InetAddress ipAddress = InetAddress.getByName(address);
        return new Socket(ipAddress, port);
    }

    private static String sendRequest(Socket socket, String request){
        try {
            DataOutputStream wr = new DataOutputStream(socket.getOutputStream());
            wr.writeUTF(request);
            wr.flush();
            //Get Response
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            return dis.readUTF();
        } catch (Exception e) {
            LOGGER.error(e);
            try {
                socket.close();
            } catch (IOException e1) {
                LOGGER.error(e1);
            }
        }
        return "Can't get response";
    }
}
