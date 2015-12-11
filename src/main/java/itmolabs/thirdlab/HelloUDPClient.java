package itmolabs.thirdlab;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HelloUDPClient {
    private final static int PACKETSIZE = 100 ;

    public static void main( String args[] )
    {
        if (args.length != 5) throw new IllegalArgumentException("Please define host, port, request prefix, " +
                "thread number, requests per thread number");

        DatagramSocket socket = null ;

        try
        {
            InetAddress host = InetAddress.getByName( args[0] ) ;
            int port         = Integer.parseInt( args[1] ) ;

            socket = new DatagramSocket() ;

            byte [] data = "Hello Server".getBytes() ;
            DatagramPacket packet = new DatagramPacket( data, data.length, host, port ) ;

            socket.send( packet ) ;

            socket.setSoTimeout( 2000 ) ;

            packet.setData( new byte[PACKETSIZE] ) ;

            socket.receive( packet ) ;

            System.out.println( new String(packet.getData()) ) ;

        }
        catch( Exception e )
        {
            System.out.println( e ) ;
        }
        finally
        {
            if( socket != null )
                socket.close() ;
        }
    }
}
