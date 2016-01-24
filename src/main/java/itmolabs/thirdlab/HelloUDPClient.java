package itmolabs.thirdlab;

import java.net.*;

public class HelloUDPClient {
    private static InetAddress HOST;
    private static int PORT;
    private static int THREADS_NUMBER;
    private static int REQUESTS_NUMBER;
    private static String REQUEST_PREFIX;

    private static final int PACKETSIZE = 64;
    private static final int TIMEOUT = 10000;

    public static void main(String args[]) throws UnknownHostException {
        HOST = InetAddress.getByName(args[0]);
        PORT = Integer.parseInt(args[1]);
        REQUEST_PREFIX = args[2];
        THREADS_NUMBER = Integer.parseInt(args[3]);
        REQUESTS_NUMBER = Integer.parseInt(args[4]);

        for (int i = 0; i < THREADS_NUMBER; i++){
            new Thread(new Client(i)).start();
        }
//        while (true){
//
//        }
    }

    private static class Client implements Runnable {
        int numberAtThread;

        Client(int numberAtThread) {
            System.out.println("init: " + numberAtThread);
            this.numberAtThread = numberAtThread;
        }

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                for (int requestNumber = 0;
                     requestNumber < REQUESTS_NUMBER;
                     requestNumber++) {

                    String request = REQUEST_PREFIX + numberAtThread + "_" + requestNumber;
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
                        socket.receive(packet);
                    }
                    System.out.println(new String(packet.getData(), 0, packet.getLength()));
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
