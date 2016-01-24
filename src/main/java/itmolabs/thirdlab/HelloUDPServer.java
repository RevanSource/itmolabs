package itmolabs.thirdlab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;

public class HelloUDPServer {

    private final static int BUFFER_SIZE = 8192;
    private static int THREADS;
    private final static String RESPONSE_PREFIX = "Hello, ";
    private final int port;

    private ExecutorService mPool;

    public HelloUDPServer(int port, int threadNumber) {
        this.port = port;
        THREADS = threadNumber;
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int threadNumber = Integer.parseInt(args[1]);

        System.out.println(LocalTime.now()  + " Port: " + port + " Thread number: " + threadNumber);
        new HelloUDPServer(port, threadNumber).start();
    }

    public void start() {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReceiveBufferSize(BUFFER_SIZE);
            datagramSocket.setSendBufferSize(BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
        for (int i = 0; i < THREADS; i++) {
            new Thread(new Receiver(datagramSocket)).start();
        }
    }

    private class Receiver implements Runnable {

        private final DatagramSocket socket;

        public Receiver(DatagramSocket serverSocket) {
            socket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    socket.receive(packet);
                    String s = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(LocalTime.now() + " Received: " + s.length());

                    String response = RESPONSE_PREFIX + s;
                    packet.setData(response.concat("\r\n").getBytes());
                    socket.send(packet);

                    System.out.println(LocalTime.now()  + " Sent: " + s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}