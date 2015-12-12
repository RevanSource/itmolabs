package itmolabs.thirdlab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

public class HelloUDPServer {

    private final static int MAX_BUFFER_SIZE = 8192;
    private static int MAX_LISTENER_THREADS = 5;
    private final static String RESPONSE_PREFIX = "Hello, ";
    private final static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss.SSSZ");
    private static Integer TIMEOUT = 0;
    private final int mPort;

    private ExecutorService mPool;

    public HelloUDPServer(int port, int threadNumber) {
        mPort = port;
        MAX_LISTENER_THREADS = threadNumber;
    }

    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Please define port and threads number, example: '8082 3'");
        int port = Integer.parseInt(args[0]);
        int threadNumber = Integer.parseInt(args[1]);

        System.out.println(DateFormat.format(new Date()) + " Port: " + port + " Thread number: " + threadNumber);
        new HelloUDPServer(port, threadNumber).start();
    }

    public void start() {
        try (DatagramSocket mSocket = new DatagramSocket(mPort)) {
            mSocket.setReceiveBufferSize(MAX_BUFFER_SIZE);
            mSocket.setSendBufferSize(MAX_BUFFER_SIZE);
            mSocket.setSoTimeout(0);
            for (int i = 0; i < MAX_LISTENER_THREADS; i++) {
                new Thread(new Listener(mSocket)).start();
            }
            while (true){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Listener implements Runnable {

        private final DatagramSocket socket;

        public Listener(DatagramSocket serverSocket) {
            socket = serverSocket;
        }

        private String readLn(DatagramPacket packet) throws IOException {
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        }

        private void writeLn(DatagramPacket packet, String string) throws IOException {
            packet.setData(string.concat("\r\n").getBytes());
            socket.send(packet);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[MAX_BUFFER_SIZE], MAX_BUFFER_SIZE);
                    String s = readLn(packet);
                    System.out.println(DateFormat.format(new Date()) + " Received: " + s.length());
                    Thread.sleep(TIMEOUT * 1000);
                    writeLn(packet, RESPONSE_PREFIX + s);
                    System.out.println(DateFormat.format(new Date()) + " Sent: " + s);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}