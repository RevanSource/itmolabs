package itmolabs.thirdlab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

public class HelloUDPServer {

    private static Integer TIMEOUT = 30;
    private final static int MAX_BUFFER_SIZE = 100;
    private final static int MAX_LISTENER_THREADS = 5;
    private final static  String RESPONSE_PREFIX = "Hello, ";

    private final static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss.SSSZ");

    private final int mPort;
    private DatagramSocket mSocket;

    private ExecutorService mPool;

    public HelloUDPServer(int port, int threadNumber) {
        mPort = port;
        try {
            mSocket = new DatagramSocket(mPort);
            mSocket.setReceiveBufferSize(MAX_BUFFER_SIZE);
            mSocket.setSendBufferSize(MAX_BUFFER_SIZE);
            mSocket.setSoTimeout(0);

            for (int i = 0; i < MAX_LISTENER_THREADS; i++) {
              new Thread(new Listener(mSocket)).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            try {
                while (true) {
                    mPool.execute(new Listener(mSocket));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            mPool.shutdown();
        }
    }

    private class Listener implements Runnable {

        private final DatagramSocket socket;

        public Listener(DatagramSocket serverSocket) {
            socket = serverSocket;
        }

        private String readLn(DatagramPacket packet) throws IOException {
            socket.receive(packet);
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData())), MAX_BUFFER_SIZE).readLine();
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
                    System.out.println(DateFormat.format(new Date()) + " Received: " + s);
                    Thread.sleep(TIMEOUT * 1000);
                    writeLn(packet, s);
                    System.out.println(DateFormat.format(new Date()) + " Sent: " + s);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Please define port and threads number, example: '8082 3'");
        int port = Integer.parseInt(args[0]);
        int threadNumber = Integer.parseInt(args[1]);

        System.out.println(DateFormat.format(new Date()) + " Port: " + port + " Thread number: " + threadNumber);
        new HelloUDPServer(port, threadNumber).start();
    }
}