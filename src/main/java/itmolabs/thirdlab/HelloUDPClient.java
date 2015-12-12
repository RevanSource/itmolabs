package itmolabs.thirdlab;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class HelloUDPClient {
    private static final int PACKETSIZE = 1460;
    private static final int TIMEOUT = 20000;

    private static InetAddress HOST;
    private static int PORT;
    private static int THREAD_NUMBER;
    private static int REQUESTS_PER_THREAD_NUMBER;
    private static String REQUEST_PREFIX;

    public static void main(String args[]) throws UnknownHostException {
        if (args.length != 5) throw new IllegalArgumentException("Please define host, port, request prefix, " +
                "thread number, requests per thread number");

        HOST = InetAddress.getByName(args[0]);
        PORT = Integer.parseInt(args[1]);
        REQUEST_PREFIX = args[2];
        THREAD_NUMBER = Integer.parseInt(args[3]);
        REQUESTS_PER_THREAD_NUMBER = Integer.parseInt(args[4]);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);
        IntStream.of(THREAD_NUMBER).forEach(i -> executorService.execute(new Client(i)));
    }

    private static class Client implements Runnable {
        final int personalNumber;

        Client(int personalNumber) {
            this.personalNumber = personalNumber;
        }

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                for (int personalNumberWithinThread = 0;
                     personalNumberWithinThread < REQUESTS_PER_THREAD_NUMBER;
                     personalNumberWithinThread++) {

                    String request = REQUEST_PREFIX + personalNumber + "_" + personalNumberWithinThread;
                    byte[] data = request.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, HOST, PORT);
                    System.out.println("send: " + request + " size: " + data.length);
                    socket.send(packet);
                    socket.setSoTimeout(TIMEOUT);
                    packet.setData(new byte[PACKETSIZE]);
                    try {
                        socket.receive(packet);
                    } catch (SocketTimeoutException ste) {
                        ste.printStackTrace();
                        socket.send(packet);
                    }
                    System.out.println(new String(packet.getData()));
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
